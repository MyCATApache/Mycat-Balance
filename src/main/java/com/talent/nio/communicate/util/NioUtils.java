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
package com.talent.nio.communicate.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.ChannelContext.ConnectionState;
import com.talent.nio.communicate.monitor.vo.StatVo;
import com.talent.nio.communicate.receive.DecodeRunnable;
import com.talent.nio.communicate.receive.TcpListener;
import com.talent.nio.communicate.send.SendUtils;
import com.talent.nio.communicate.send.SendRunnable;
import com.talent.nio.connmgr.ConnectionManager;
import com.talent.nio.debug.DebugUtils;
import com.talent.nio.startup.Startup;
import com.talent.nio.utils.SystemTimer;

/**
 * 
 * 
 * @author 谭耀武 2012-1-9 上午09:29:05
 * 
 */
public class NioUtils
{
	private static final Logger log = LoggerFactory.getLogger(NioUtils.class);

	/**
	 * 
	 */
	private NioUtils()
	{

	}

	/**
	 * 给SelectionKey添加监听选项
	 * 
	 * @param key
	 * @param opt
	 */
	public static void addOpt(SelectionKey key, int opt)
	{
		if (key.isValid())
		{
			key.interestOps(key.interestOps() | opt);
		}
	}

	/**
	 * 注销掉SelectionKey的监听选项
	 * 
	 * @param key
	 * @param opt
	 */
	public static void removeOpt(SelectionKey key, int opt)
	{
		if (key.isValid())
		{
			key.interestOps(key.interestOps() & (~opt));
		}
	}

	/**
	 * 略过通道的所有数据
	 * 
	 * @param socketChannel
	 */
	public static int skipChannelData(ReadableByteChannel socketChannel) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int allLength = 0;
		int length = 0;
		while (socketChannel.isOpen() && (length = socketChannel.read(buffer)) > 0)
		{
			allLength += length;
			buffer.clear();
		}
		log.debug("skipped length [{}]", allLength);
		return allLength;
	}

	/**
	 * 统计发送流量
	 * 
	 * @param length
	 *            消息长度
	 * @param channelContext
	 */
	public static void statSend(int length, ChannelContext channelContext)
	{
		long sentBytes = channelContext.getStatVo().getSentBytes() + length;
		channelContext.getStatVo().setSentBytes(sentBytes);

		log.debug("{} has sent {} bytes", channelContext.getId(), sentBytes);
	}

	/**
	 * 保存注销的时间
	 */
	private static Map<ChannelContext, Long> logoutTime = new HashMap<ChannelContext, Long>();

	/**
	 * 断开连接
	 * 
	 * @param channelContext
	 * @param reason
	 * @param isComplete
	 *            是否完全彻底的注销，true：彻底地断开(所保存的统计数据将全部丢失)，移除连接时必须为true；
	 */
	private static boolean _disconnect(ChannelContext channelContext, String reason, boolean isComplete)
	{
		log.warn("start disconnect {}, reason:{}, isComplete:{}", channelContext.getId(), reason, isComplete);
		SocketChannel socketChannel = channelContext.getSocketChannel();
		if (ConnectionState.LOGOUTING == channelContext.getConnectionState())
		{
//			try
//			{
//				if (socketChannel.isOpen())
//				{
//					socketChannel.close();
//				}
//			} catch (Exception e)
//			{
//				log.error("exception occured when close socketchannel:" + channelContext, e);
//			}
			return false;
		}

		synchronized (channelContext)
		{
			
//			try
//			{
//				channelContext.setConnectionState(ConnectionState.LOGOUTING);
//				if (socketChannel.isOpen())
//				{
//					socketChannel.close();
//				}
//			} catch (Exception e)
//			{
//				log.error("exception occured when close socketchannel:" + channelContext, e);
//			}
//
//			try
//			{
//				Startup.getSelector().wakeup();
//			} catch (Exception e)
//			{
//				log.error("exception occured when selector.wakeup():" + channelContext, e);
//			}

			Long lastLogoutTime = logoutTime.get(channelContext);
			long currTime = SystemTimer.currentTimeMillis();
			if (lastLogoutTime != null)
			{
				long x = currTime - lastLogoutTime;
				if ((x) < 100L)
				{
					log.error("注销得好频繁，上次注销在{}毫秒前, {} logouted at {}", x, channelContext.getId(), new Timestamp(
							lastLogoutTime).toString());
					return false;
				}
			}

			if (DebugUtils.isNeedDebug(channelContext))
			{
				log.error("may be logout, reason {}, {}", reason, channelContext);
			}
//
//			if (ConnectionState.TCP_ON == channelContext.getConnectionState())
//			{
//				log.warn("can not logout, channelContext is TCP_ON. {}", channelContext);
//				return false;
//			}

			logoutTime.put(channelContext, currTime);

			try
			{
				// 注销通道的监听-- start
				try
				{

					if (DebugUtils.isNeedDebug(channelContext))
					{
						log.warn("start logout, reason {}, {}", reason, channelContext.getId());
					}

					if (socketChannel != null)
					{
						// 注销通道的监听
						TcpListener.getMapOfSocketChannelAndChannelContext().remove(socketChannel);
						Startup.getTcpListener().logout(channelContext);
					}
				} catch (Exception e)
				{
					log.error("", e);
				}
				// 注销通道的监听-- end

				// 清空socketChannelId对应的发送队列和发送线程工厂中的缓存对象 -- start
				SendRunnable sendRunnable = channelContext.getSendRunnable();
				if (sendRunnable != null)
				{
					sendRunnable.clearMsgQueue();
					if (isComplete)
					{
						SendUtils.removeStat(channelContext);
					} else
					{
						SendUtils.recordStat(channelContext, sendRunnable.getProcessedMsgCount(),
								sendRunnable.getSubmitCount(),
								sendRunnable.getProcessedMsgByteCount());
					}
				}
				// 清空socketChannelId对应的发送队列和发送线程工厂中的缓存对象 -- end

				// 清空socketChannelId对应的接收处理队列和接收处理线程工厂中的缓存对象 -- start
				DecodeRunnable decodeRunnable = channelContext.getDecodeRunnable();
				if (decodeRunnable != null)
				{
					decodeRunnable.clearMsgQueue();
					if (isComplete)
					{
						StatUtils.removeStat(channelContext);
					} else
					{

						StatUtils.recordStat(channelContext, decodeRunnable.getProcessedMsgCount(),
								decodeRunnable.getSubmitCount(),
								decodeRunnable.getProcessedMsgByteCount());
					}
				}

			} catch (Throwable e)
			{
				log.error(" Throwable occured when logout " + channelContext.getId(), e);
				channelContext.setDesc4Err("logout fail:" + e.getMessage());
			} finally
			{
				
				if (channelContext != null)
				{
					channelContext.setConnectionState(ConnectionState.TCP_OFF);
					channelContext.setDesc4Err(reason);
				} else
				{
					log.warn(" channelContext is null");
				}
				if (DebugUtils.isNeedDebug(channelContext))
				{
					log.warn("end logout, reason {}, {}", reason, channelContext.getId());
				}
				Startup.getSelector().wakeup();
			}
			return true;
		}
	}

	/**
	 * 删除连接
	 * 
	 * @param _socketChannel
	 * @param channelContext
	 * @param reason
	 */
	public static void remove(ChannelContext channelContext, String reason)
	{
		boolean b = _disconnect(channelContext, reason, true);
		if (b)
		{
			logoutTime.remove(channelContext);
			ConnectionManager.getInstance().removeConnection(channelContext);
			channelContext.setConnectionState(ConnectionState.REMOVED);
		}

	}

	/**
	 * 断开连接
	 * 
	 * @param channelContext
	 * @param reason
	 */
	public static void disconnect(ChannelContext channelContext, String reason)
	{
		_disconnect(channelContext, reason, false);
	}

	/**
	 * 将缓冲区的数据读到字节数组中
	 * 
	 * @param buffer
	 *            不允许为null
	 * @param byteArray
	 *            不允许为null
	 */
	public static void readBufferToByteArray(ByteBuffer buffer, byte[] byteArray)
	{
		if (byteArray == null)
		{
			throw new RuntimeException("byteArray is null");
		}
		if (buffer == null)
		{
			throw new RuntimeException("buffer is null");
		}

		buffer.flip();
		buffer.get(byteArray); // 将数据从buffer读到字节数组中
	}

	private static Set<ChannelContext.ConnectionState> canSendTcpMsgStates = new HashSet<ChannelContext.ConnectionState>();

	static
	{
		canSendTcpMsgStates.add(ConnectionState.TCP_ON);
		canSendTcpMsgStates.add(ConnectionState.APP_BUILDING);
		canSendTcpMsgStates.add(ConnectionState.APP_ON);
		canSendTcpMsgStates.add(ConnectionState.APP_OFF);
		canSendTcpMsgStates.add(ConnectionState.APP_LINKFAILED);
	}

	public static boolean canSendTcpMsg(ChannelContext channelContext)
	{
		if (channelContext == null)
		{
			return false;
		} else
		{
			return canSendTcpMsgStates.contains(channelContext.getConnectionState());
		}
	}

	/**
	 * 
	 * @param channelContext
	 * @return
	 */
	public static boolean buildLink(ChannelContext channelContext)
	{
		if (channelContext == null)
		{
			log.error("channelContext is null!");
			return false;
		}

		final String ip = channelContext.getRemoteNode().getIp();
		final int port = channelContext.getRemoteNode().getPort();
		channelContext.getStatVo().getCountOfErrorPackage().set(0);// .setCountOfErrorPackage(0);

		InetSocketAddress address = new InetSocketAddress(ip, port);

		try
		{
			// 注册消息接收监听
			Startup.getTcpListener().register(address, channelContext);
		} catch (IOException e)
		{
			channelContext.getWriteIOErrorHandler().handle(null, e, channelContext,
					"exception occured when build link!");
			return false;
		}

		return true;
	}

	/**
	 * 创建统计信息
	 * 
	 * @param channelContext
	 * @return
	 */
	public static StatVo createStatInfo(ChannelContext channelContext)
	{
		StatVo ret = channelContext.getStatVo();

		int packetOgnzerQueueSize = channelContext.getDecodeRunnable().getMsgQueue().size();
		int senderQueueSize = channelContext.getSendRunnable().getMsgQueue().size();
		int packetHandlerQueueSize = channelContext.getHandlerRunnable().getMsgQueue().size();

		ret.setPacketOgnzerQueueSize(packetOgnzerQueueSize);
		ret.setSenderQueueSize(senderQueueSize);
		ret.setPacketHandlerQueueSize(packetHandlerQueueSize);

		return ret;
		//
		// int size1 =
		// channelContext.getPacketOgnzerRunnable().getMsgQueue().size();
		// int size2 =
		// channelContext.getSocketMsgSendRunnable().getMsgQueue().size();
		// int size3 =
		// channelContext.getPacketHandlerRunnable().getMsgQueue().size();
		// long size4 = channelContext.getSentBytes().longValue();
		// long size5 = channelContext.getReceivedBytes().longValue();
		//
		// StringBuilder sb = new StringBuilder();
		// sb.append(channelContext.getId()).append(StringUtil.NEWLINE);
		// sb.append("PacketOgnzer Queue:").append(size1).append(StringUtil.NEWLINE);
		// sb.append("Sender Queue:").append(size2).append(StringUtil.NEWLINE);
		// sb.append("Handler Queue:").append(size3).append(StringUtil.NEWLINE);
		// sb.append("SentBytes:").append(size4).append(StringUtil.NEWLINE);
		// sb.append("ReceivedBytes:").append(size5).append(StringUtil.NEWLINE);
		// return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		// Desktop.getDesktop().edit(new File("E:/work/2011-01/sss.dfda"));
		// Desktop.getDesktop().open(new File("E:/work/2011-01/index.php.htm"));
		Runtime.getRuntime().exec("cmd /c start E:/work/2011-01/index.php.htm");
	}
}