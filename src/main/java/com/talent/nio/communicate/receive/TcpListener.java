/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package com.talent.nio.communicate.receive;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.api.Config;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.ChannelContext.ConnectionState;
import com.talent.nio.communicate.RemoteNode;
import com.talent.nio.communicate.handler.intf.PacketHandlerIntf;
import com.talent.nio.communicate.intf.DecoderIntf;
import com.talent.nio.communicate.send.SendUtils;
import com.talent.nio.communicate.server.ServerContext;
import com.talent.nio.communicate.util.NioProxy;
import com.talent.nio.connmgr.ConnectionManager;
import com.talent.nio.utils.SystemTimer;
import com.talent.platform.threadpool.SynThreadPoolExecutor;
import com.talent.platform.threadpool.intf.SynRunnableIntf;
import com.talent.platform.threadpool.monitor.ThreadPoolMonitor;

/**
 * 
 * @author 谭耀武
 * @date 2011-08-04
 * 
 */
public class TcpListener
{
	private static final Logger log = LoggerFactory.getLogger(TcpListener.class);

	private final Selector selector;

	private SynThreadPoolExecutor<SynRunnableIntf> synThreadPoolExecutor = null;

	private static Map<SocketChannel, ChannelContext> mapOfSocketChannelAndChannelContext = new ConcurrentHashMap<SocketChannel, ChannelContext>();

	/**
	 * 用于服务监听。 key: port, value: protocol
	 */
	private final static Map<ServerSocketChannel, ServerContext> mapOfServerSocketChannelAndServerContext = new HashMap<ServerSocketChannel, ServerContext>();

	public static Map<SocketChannel, ChannelContext> getMapOfSocketChannelAndChannelContext()
	{
		return mapOfSocketChannelAndChannelContext;
	}

	/**
	 * 
	 * @param selector
	 */
	public TcpListener(Selector selector, SynThreadPoolExecutor<SynRunnableIntf> synThreadPoolExecutor)
	{

		this.selector = selector;

		this.synThreadPoolExecutor = synThreadPoolExecutor;

		ThreadPoolMonitor.getInstance().register(synThreadPoolExecutor);
	}

	/**
	 * 接收消息，将消息入队列
	 * 
	 * @param key
	 * @return
	 */
	public void read(SelectionKey key)
	{
		SocketChannel sc = (SocketChannel) key.channel();
		ChannelContext channelContext = mapOfSocketChannelAndChannelContext.get(sc);

		if (channelContext == null)
		{
			log.error("channelContext is null");
			try
			{
				sc.close();
				this.selector.wakeup();
			} catch (IOException ioe)
			{
				log.error("channelContext is null,IOException occured when close the SocketChannel", ioe);
			}
			return;
		}

		try
		{
			ByteBuf datas = ChannelReader.read(sc, channelContext);// (key);
			if (datas != null && datas.capacity() > 0)
			{
				submitMsg(datas, channelContext);
			}
		} catch (IOException e)
		{
			channelContext.getReadIOErrorHandler().handle(sc, e, channelContext, null);
			return;
		}
	}

	/**
	 * 提交原始消息给组包线程
	 * 
	 * @param datas
	 * @param channelContext
	 * @return
	 */
	public boolean submitMsg(ByteBuf datas, ChannelContext channelContext)
	{
		DecodeRunnable packetOgnzerRunnable = channelContext.getDecodeRunnable();

		if (packetOgnzerRunnable == null)
		{
			log.error("socketMsgProcessRunnable for {} is null", channelContext.getId());
			return false;
		}

		if (packetOgnzerRunnable.getMsgQueue().size() > Config.getInstance().getSleepSize())
		{
			try
			{
				log.warn("there are {} msgs waiting for processing", packetOgnzerRunnable.getMsgQueue().size());
				Thread.sleep(Config.getInstance().getSleepTime());
			} catch (InterruptedException e)
			{
				log.error("", e);
			}
		}

		packetOgnzerRunnable.addMsg(datas);

		synThreadPoolExecutor.execute(packetOgnzerRunnable);

		return true;
	}

	/**
	 * 在某端口接受client连接
	 * 
	 * @param bindIp
	 * @param bindPort
	 * @throws IOException
	 */
	public void acceptAt(ServerContext serverContext) throws IOException
	{
		if (serverContext.getBindPort() <= 0)
		{
			throw new RuntimeException("port can not less than 0");
		}
		ServerSocketChannel socketChannel = ServerSocketChannel.open();

		InetSocketAddress inetSocketAddress = null;

		if (serverContext.getBindIp() == null || "".equals(serverContext.getBindIp()))
		{
			inetSocketAddress = new InetSocketAddress(serverContext.getBindPort());
			// serverContext.setBindIp(inetSocketAddress.getAddress().getHostAddress());
			// serverContext.setBindPort(inetSocketAddress.getPort());
		} else
		{
			inetSocketAddress = new InetSocketAddress(serverContext.getBindIp(), serverContext.getBindPort());
		}

		ServerSocket socket = socketChannel.socket();
		socket.bind(inetSocketAddress);

		socketChannel.configureBlocking(false);

		socketChannel.register(selector, SelectionKey.OP_ACCEPT, this);

		selector.wakeup();

		mapOfServerSocketChannelAndServerContext.put(socketChannel, serverContext);

	}

	/**
	 * 监听消息，如果协议有消息发送过来，则处理消息(消息数据的接收是阻塞的，数据接收后，处理数据的过程是用线程完成的)
	 */
	public void listen()
	{
		new Thread(this.getClass().getSimpleName() + ":receive msg thread")
		{
			int c = 0;

			@Override
			public void run()
			{
				while (true)
				{
					try
					{
						if (selector.select(5000) <= 0)
						{
							if (log.isDebugEnabled())
							{
								log.debug("selector select(): nothing coming! count:" + c + ". selector.keys().size():"
										+ selector.keys().size() + " " + selector.keys());
							}

							if (selector.keys().size() == 0)
							{
								Thread.sleep(1000);
							}

							c++;
							if (c > 1)
							{
								c = 0;
								try
								{
									selector.wakeup();
								} catch (Exception e)
								{
									log.error("exception occured when selector wakeup", e);
								}
							}
							continue;
						}
						Set<SelectionKey> readyKeys = selector.selectedKeys();
						Iterator<SelectionKey> it = readyKeys.iterator();
						while (it.hasNext())
						{
							SelectionKey key = null;
							try
							{
								key = it.next();
								it.remove(); // 此行很重要，容易被忘记

								if (key.isValid() && key.isAcceptable()) // 作为服务器端时用到的
								{
									SocketChannel socketChannel = null;
									ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
									try
									{
										ServerContext serverContext = mapOfServerSocketChannelAndServerContext
												.get(serverSocketChannel);

										socketChannel = serverSocketChannel.accept();
										if (socketChannel == null)
										{
											return;
										}
										InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel
												.getRemoteAddress();

										InetSocketAddress localAddress = (InetSocketAddress) socketChannel
												.getLocalAddress();

										socketChannel.configureBlocking(false);

										RemoteNode remoteNode = new RemoteNode(inetSocketAddress.getAddress()
												.getHostAddress(), inetSocketAddress.getPort());
										DecoderIntf packetOgnzerIntf = serverContext.getPacketOgnzerClass()
												.newInstance();
										PacketHandlerIntf packetHandler = serverContext.getPacketHandlerClass()
												.newInstance();

										ChannelContext channelContext = new ChannelContext(localAddress.getHostName(),
												localAddress.getPort(), remoteNode, serverContext.getProtocol(),
												packetOgnzerIntf, packetHandler);

										if (serverContext.getChannelContextCompleter() != null)
										{
											serverContext.getChannelContextCompleter().complete(channelContext);
										}

										mapOfSocketChannelAndChannelContext.put(socketChannel, channelContext);
										channelContext.setSocketChannel(socketChannel);
										channelContext.getStatVo().setStateTimeTcpBuilding(SystemTimer.currentTimeMillis());
										channelContext.setDesc4Err("");

										Socket socket = socketChannel.socket();
										socket.setSendBufferSize(1048576);
										socket.setReceiveBufferSize(1048576);

										socketChannel.register(selector, SelectionKey.OP_READ, TcpListener.this);

										TcpListener.this.selector.wakeup();

										log.info("{} socket connection has been built, waiting app connection ",
												channelContext.getId());

										channelContext.setConnectionState(ConnectionState.TCP_ON);
										channelContext.getStatVo().setStateTimeAppBuilding(SystemTimer.currentTimeMillis());

										channelContext.setAutoConnection(false);
										channelContext
												.setReadIOErrorHandler(com.talent.nio.handler.error.server.ReadIOErrorHandler
														.getInstance());
										channelContext
												.setWriteIOErrorHandler(com.talent.nio.handler.error.server.WriteIOErrorHandler
														.getInstance());
										channelContext
												.setErrorPackageHandler(com.talent.nio.handler.error.server.DefaultErrorPackageHandler
														.getInstance());

										ConnectionManager.getInstance().addConnection(channelContext);

									} catch (IOException e)
									{
										try
										{
											socketChannel.close();
										} catch (IOException e1)
										{
											log.error(e1.getMessage(), e1);
										}
									}

								} else if (key.isValid() && key.isReadable())
								{ // 只处理可读的情况
									TcpListener socketMsgListener = (TcpListener) key.attachment();

									if (socketMsgListener != null)
									{
										socketMsgListener.read(key);
									} else
									{
										log.error("key.attachment() is null");
									}
								} else
								{
									// 正常情况到不了此处
									log.error(
											"key.isValid:{}, key.isAcceptable:{}, key.isConnectable:{}, key.isReadable:{}, key.isWritable{}",
											key.isValid(), key.isAcceptable(), key.isConnectable(), key.isReadable(),
											key.isWritable());
								}
							} catch (Exception e)
							{
								log.error(e + " Exception in loop of while(it.hasNext())", e); // 不处理这里面的异常，只是打印
							}
						}
					} catch (IOException e)
					{
						log.error(e + " IOException occured when selector.select()");
						try
						{
							Thread.sleep(100);
						} catch (InterruptedException e1)
						{
							log.error("InterruptedException occured when sleep in selector.select()", e1);
						}
					} catch (Exception e)
					{
						log.error(e + "Exception occured when selector.select()--while(true)");
					}
				}
			}
		}.start();
	}

	// private static ChannelContext
	// contain(Collection<ChannelContext> channelContexts, String
	// remoteIp, int remotePort)
	// {
	// if (channelContexts == null)
	// {
	// return null;
	// }
	//
	// for (ChannelContext channelContext : channelContexts)
	// {
	// if (remoteIp.equals(channelContext.getRemoteNode().getIp()) &&
	// channelContext.getRemoteNode().getPort() == remotePort)
	// {
	// return channelContext;
	// }
	// }
	// return null;
	// }

	// /**
	// * 注册要监听的地址
	// *
	// * @param ip
	// * 要监听的ip
	// * @param port
	// * 要监听的port
	// * @param proxy
	// * 代理地址，如果不用代理则传null
	// * @return
	// */
	// public void register(String ip, int port, ChannelContext
	// channelContext, String socketChannelId) throws IOException
	// {
	// InetSocketAddress address = new InetSocketAddress(ip, port);
	// register(address, channelContext);
	// }

	/**
	 * 注册要监听的地址
	 * 
	 * @param socketAddress
	 *            要监听的SocketAddress
	 * @param proxy
	 *            代理地址，如果不用代理则传null
	 * @return
	 * @throws IOException
	 */
	public void register(InetSocketAddress socketAddress, ChannelContext channelContext) throws IOException
	{
		try
		{
			Thread thread = new Thread(new BuildLinkThread(socketAddress, this, channelContext,
					channelContext.getProxy()));
			thread.setName("build link [" + channelContext.getId() + "]");
			log.info(channelContext.getId() + " start thread for building link!");
			thread.start();
			channelContext.setConnectionState(ConnectionState.TCP_BUILDING);
			channelContext.getStatVo().setStateTimeTcpBuilding(SystemTimer.currentTimeMillis());
			channelContext.getStatVo().setCurrentReceivedTime(SystemTimer.currentTimeMillis());
		} catch (Throwable e)
		{
			log.error("Exception occured when start building link thread", e);
		}
	}

	/**
	 * 
	 * @author 谭耀武
	 * @date 2012-08-09
	 * 
	 */
	private static class BuildLinkThread implements Runnable
	{
		private InetSocketAddress socketAddress = null;
		private TcpListener socketMsgListener = null;

		private ChannelContext channelContext = null;

		/**
		 * 
		 * @param socketAddress
		 * @param socketMsgListener
		 * @param channelContext
		 * @param socketChannelId
		 * @param proxy
		 *            如果没有代理就传null
		 */
		BuildLinkThread(InetSocketAddress socketAddress, TcpListener socketMsgListener, ChannelContext channelContext,
				Proxy proxy)
		{
			this.socketAddress = socketAddress;
			this.socketMsgListener = socketMsgListener;
			this.channelContext = channelContext;
		}

		@Override
		public void run()
		{
			synchronized (channelContext)
			{
				try
				{

					log.debug("start buildlink for {}", channelContext.getId());

					SocketChannel socketChannel = SocketChannel.open();
					
					
					// String xx = socketChannel.toString();
					bind(channelContext, socketChannel); // 绑定ip
					
			

					if (channelContext.getProxy() == null)
					{
						try
						{
							socketChannel.connect(socketAddress);
						} catch (IOException e)
						{
							log.error(
									channelContext.getBindIp() + ":" + channelContext.getBindPort() + "---"
											+ e.getLocalizedMessage(), e);
							socketChannel.close();
							throw e;
						}
						socketChannel.configureBlocking(false); // 非阻塞，此行不能少，否则IllegalBlockingModeException
					} else
					// 使用代理
					{
						socketChannel.connect(channelContext.getProxy().address());
						socketChannel.configureBlocking(false); // 非阻塞，此行不能少，否则IllegalBlockingModeException
						NioProxy.proxyImpl(socketChannel, socketAddress);
					}
					
					
					

					Socket socket = socketChannel.socket();
					socket.setSendBufferSize(1048576); // 262142
					socket.setReceiveBufferSize(1048576);

					channelContext.setMyIp(socketChannel.socket().getLocalAddress().getHostAddress());
					channelContext.setMyPort(socketChannel.socket().getLocalPort());
					channelContext.generateId();
					
					// int logIndex = 1;
					// log.warn("" + logIndex++);

					// CommunicateManager.getMapOfSocketChannelAndsocketChannelId().put(socketChannel,
					// channelContext.getsocketChannelId());
					mapOfSocketChannelAndChannelContext.put(socketChannel, channelContext);
					channelContext.setSocketChannel(socketChannel);
					channelContext.getStatVo().setStateTimeTcpBuilding(SystemTimer.currentTimeMillis());
					channelContext.setDesc4Err("");
					

					SendUtils.resumeCount(channelContext);

					socketChannel.register(socketMsgListener.selector, SelectionKey.OP_READ, socketMsgListener); // 注册到selector中去
					
					
					
					
					socketMsgListener.selector.wakeup();
					log.info("{} socket connection has been built, waiting app connection ", channelContext.getId());
					channelContext.setConnectionState(ConnectionState.TCP_ON);
					
					
					
					
				} catch (Exception t)
				{
					log.error("occured when build link " + channelContext.getId(), t);
					channelContext.getStatVo().getBuildExceptionTimes().incrementAndGet();
					channelContext.setConnectionState(ConnectionState.TCP_LINKFAILED);
					channelContext.setDesc4Err(t.getMessage());

				} finally
				{
					// skip it
				}
			}
			
		}

		/**
		 * 
		 * 绑定到指定的ip和port(服务器有时候会有多个ip，需要绑定指定的ip)
		 * 
		 * @author tanyaowu
		 * @param channelContext
		 * @param socketChannel
		 */
		private static void bind(ChannelContext channelContext, SocketChannel socketChannel) throws Exception
		{
			if (channelContext.getBindIp() != null)
			{
				InetSocketAddress socketAddress = new InetSocketAddress(channelContext.getBindIp(),
						channelContext.getBindPort());
				socketChannel.socket().setReuseAddress(true);
				socketChannel.socket().bind(socketAddress);
			}
		}
	}

	/**
	 * 注销对协议的接收监听
	 * 
	 * @param socketChannelId
	 */
	public void logout(ChannelContext channelContext)
	{
		String socketChannelId = channelContext.getId();
		if (channelContext != null)
		{
			log.debug("logout listen for [{}]", socketChannelId);

			SocketChannel socketChannel = channelContext.getSocketChannel();

			if (socketChannel == null)
			{
				log.error(socketChannelId + " socketChannel is null");
				return;
			}

			try
			{
				if (socketChannel.isOpen())
				{
					socketChannel.close();
				}
			} catch (Exception e)
			{
				log.error(socketChannelId + " exception occured when close socketchannel", e);
			}

			try
			{
				this.selector.wakeup();
			} catch (Exception e)
			{
				log.error(socketChannelId + " exception occured when selector.wakeup()", e);
			}
		}
	}

	public Selector getSelector()
	{
		return selector;
	}

	public void setThreadExecutor(SynThreadPoolExecutor<SynRunnableIntf> threadExecutor)
	{
		this.synThreadPoolExecutor = threadExecutor;
	}

	public SynThreadPoolExecutor<SynRunnableIntf> getThreadExecutor()
	{
		return synThreadPoolExecutor;
	}
}