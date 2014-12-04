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
package com.talent.nio.communicate;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.Proxy;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.handler.PacketHandlerProxy;
import com.talent.nio.communicate.handler.intf.PacketHandlerIntf;
import com.talent.nio.communicate.intf.DecoderIntf;
import com.talent.nio.communicate.monitor.vo.StatVo;
import com.talent.nio.communicate.receive.DecodeRunnable;
import com.talent.nio.communicate.receive.HandlerRunnable;
import com.talent.nio.communicate.send.SendRunnable;
import com.talent.nio.handler.error.client.DefaultErrorPackageHandler;
import com.talent.nio.handler.error.client.ReadIOErrorHandler;
import com.talent.nio.handler.error.client.WriteIOErrorHandler;
import com.talent.nio.handler.error.intf.ErrorPackageHandlerIntf;
import com.talent.nio.handler.error.intf.ReadIOErrorHandlerIntf;
import com.talent.nio.handler.error.intf.WriteIOErrorHandlerIntf;
import com.talent.nio.listener.ConnectionStateListener;
import com.talent.nio.utils.SystemTimer;

/**
 * 
 * @author 谭耀武
 * @date 2011-12-23
 * 
 */
public class ChannelContext
{

	/**
	 * 链路状态的枚举
	 * 
	 * @author 谭耀武
	 * @date 2011-12-28
	 * 
	 */
	public static enum ConnectionState
	{
		/**
		 * 未连接
		 */
		TCP_OFF,
		/**
		 * TCP正在建链
		 */
		TCP_BUILDING,
		/**
		 * TCP层已经连上链路
		 */
		TCP_ON,
		/**
		 * TCP建链失败
		 */
		TCP_LINKFAILED,

		/**
		 * 应用层链路断开
		 */
		APP_OFF,
		/**
		 * 应用层正在建链
		 */
		APP_BUILDING,
		/**
		 * 应用层已经连上链路
		 */
		APP_ON,
		/**
		 * 应用层建链失败
		 */
		APP_LINKFAILED,

		/**
		 * 正在注销
		 */
		LOGOUTING,

		/**
		 * 已经被删除了
		 */
		REMOVED
	}

	/**
	 * 便于应用扩展自己的属性
	 */
	private Map<String, Object> props = new HashMap<String, Object>();

	/**
	 * connection状态监听者
	 */
	private ConnectionStateListener connectionStateListener = null;

	public void addProperty(String key, Object value)
	{
		props.put(key, value);
	}

	public Object getProperty(String key)
	{
		return props.get(key);
	}

	public Object getProperty(String key, Object dftValue)
	{
		if (props.containsKey(key))
		{
			return props.get(key);
		} else
		{
			return dftValue;
		}
	}

	
	public boolean isNeedRemoved(long interval)
	{
		ConnectionState s = this.connectionState;
		long currTime = SystemTimer.currentTimeMillis();

		long compareTime = currTime - interval;
		boolean ret = (s == ConnectionState.TCP_OFF && statVo.getStateTimeTcpOff().getTime() < compareTime)
				|| (s == ConnectionState.TCP_BUILDING && statVo.getStateTimeTcpBuilding().getTime() < compareTime)
				|| (s == ConnectionState.TCP_ON && statVo.getStateTimeTcpOn().getTime() < compareTime)
				|| (s == ConnectionState.TCP_LINKFAILED && statVo.getStateTimeTcpLinkfailed().getTime() < compareTime)
				|| (s == ConnectionState.APP_OFF && statVo.getStateTimeAppOff().getTime() < compareTime)
				|| (s == ConnectionState.APP_BUILDING && statVo.getStateTimeAppBuilding().getTime() < compareTime)
				|| (s == ConnectionState.APP_LINKFAILED && statVo.getStateTimeAppLinkfailed().getTime() < compareTime);

		return ret;
	}

	public String getString(String key, Object dftValue)
	{
		return (String) getProperty(key, dftValue);
	}

	public boolean getBoolean(String key, Object dftValue)
	{
		return (boolean) Boolean.parseBoolean(getProperty(key, dftValue).toString());
	}

	public int getInt(String key, Object dftValue)
	{
		return (int) Integer.parseInt(getProperty(key, dftValue).toString());
	}

	public long getLong(String key, Object dftValue)
	{
		return (long)Long.parseLong(getProperty(key, dftValue).toString()) ;
	}

	private static final Logger log = LoggerFactory.getLogger(ChannelContext.class);

	private DecoderIntf decoder = null; // 组包者，应用提供

	private DecodeRunnable decodeRunnable = null; // 组包runnable，框架提供

	private SendRunnable sendRunnable = null; // 发送消息的runnable，框架提供

	private PacketHandlerIntf packetHandler = null; // 收到消息后的handler，应用提供

	private HandlerRunnable handlerRunnable = null; // 收到消息后的runnable，框架提供

	/**
	 * 发生write io异常时的处理者
	 */

	private WriteIOErrorHandlerIntf writeIOErrorHandler = WriteIOErrorHandler.getInstance();

	/**
	 * 发生write io异常时的处理者
	 */

	private ReadIOErrorHandlerIntf readIOErrorHandler = ReadIOErrorHandler.getInstance();

	/**
	 * 收到乱包时的处理者
	 */

	private ErrorPackageHandlerIntf errorPackageHandler = DefaultErrorPackageHandler.getInstance();

	/**
	 * 是否自动建链(主动建链)。true: 自动建链
	 */
	private boolean isAutoConnection = true;

	/**
	 * socket通道
	 */

	private SocketChannel socketChannel;

	/**
	 * 唯一标识
	 */
	private String id = null;
	
	private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

	/**
	 * 交互使用的协议名，这个名字可以随便取，但建议取有意义的，譬如smpp，http等
	 */
	private String protocol = null;

	/**
	 * 链路状态
	 */
	private ConnectionState connectionState = ConnectionState.TCP_OFF;

	/**
	 * 错误描述
	 */
	private String desc4Err;

	/** RemoteNode基本信息 */
	private RemoteNode remoteNode = null;

	private StatVo statVo = new StatVo();
	/**
	 * 
	 */
	private String bindIp = null;

	/**
	 * 
	 */
	private int bindPort = 0;
	/**
	 * 
	 */
	private String myIp = null;

	/**
	 * 
	 */
	private int myPort = 0;

	private Proxy proxy = null;

	private AtomicLong seqNo = new AtomicLong();

	/**
	 * 是否需要记录发送失败的信息。true:需要记录.
	 */
	private boolean isNeedRecordSendFailMsg = false;

	/**
	 * 记录发送失败记录的个数(只记录这么多条发送失败的消息)
	 */
	private int countOfRecordSendFail = 50;

	/**
	 * 需要建链的链路状态集
	 */
	private static Set<ConnectionState> needBuildConnectionStateSet = new HashSet<ConnectionState>(4);

	static
	{
		needBuildConnectionStateSet.add(ConnectionState.TCP_OFF);
		needBuildConnectionStateSet.add(ConnectionState.TCP_LINKFAILED);
		needBuildConnectionStateSet.add(ConnectionState.APP_OFF);
		needBuildConnectionStateSet.add(ConnectionState.APP_LINKFAILED);
	}

	/**
	 * 当前状态下是否需要检查建链超时
	 * 
	 * @author tanyaowu
	 * @param connectionState
	 * @return
	 */
	public static boolean isNeedCheckBuildTimeout(ConnectionState connectionState)
	{
		return (connectionState == ConnectionState.TCP_BUILDING) || (connectionState == ConnectionState.APP_BUILDING);
	}

	public static void main(String[] args)
	{
	}

	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public ChannelContext(RemoteNode remoteNode, String protocol, DecoderIntf packetOgnzerIntf,
			PacketHandlerIntf packetHandler)
	{
		this(null, 0, remoteNode, protocol, packetOgnzerIntf, packetHandler);

	}

	/**
	 * 
	 * @param myIp
	 * @param myPort
	 * @param remoteNode
	 * @param protocol
	 * @param packetOgnzerIntf
	 * @param packetHandler
	 */
	public ChannelContext(String myIp, int myPort, RemoteNode remoteNode, String protocol,
			DecoderIntf packetOgnzerIntf, PacketHandlerIntf packetHandler)
	{
		this(myIp, myPort, remoteNode, protocol, packetOgnzerIntf, packetHandler, null);
	}

	/**
	 * 
	 * @param myIp
	 * @param myPort
	 * @param remoteNode
	 * @param protocol
	 * @param packetOgnzerIntf
	 * @param packetHandler
	 * @param connectionStateListener
	 */
	public ChannelContext(String myIp, int myPort, RemoteNode remoteNode, String protocol,
			DecoderIntf packetOgnzerIntf, PacketHandlerIntf packetHandler,
			ConnectionStateListener connectionStateListener)
	{
		super();
		this.remoteNode = remoteNode;
		this.protocol = protocol;
		this.decoder = packetOgnzerIntf;
		PacketHandlerProxy PacketHandlerProxy = new PacketHandlerProxy(packetHandler);
		this.packetHandler = PacketHandlerProxy;
		this.myIp = myIp;
		this.myPort = myPort;

		this.decodeRunnable = new DecodeRunnable(this);
		this.handlerRunnable = new HandlerRunnable(this);
		this.handlerRunnable.setParent(decodeRunnable);
		this.sendRunnable = new SendRunnable(this);

		this.setConnectionStateListener(connectionStateListener);

		generateId();

	}

	private void addPropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.addPropertyChangeListener(listener);
	}

	protected void firePropertyChange(String prop, Object old, Object newValue)
	{
		listeners.firePropertyChange(prop, old, newValue);
	}

	/**
	 * 生成形如"127.0.0.1:9573-->smpp://127.10.12.124:8090"的字符串
	 * 
	 * @param remoteNode
	 * @param procotol
	 * @return
	 */
	public void generateId()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(myIp).append(":").append(myPort).append("-->");
		builder.append(this.protocol).append("://").append(remoteNode.getIp()).append(":").append(remoteNode.getPort());
		this.setId(builder.toString());
	}

	public String getBindIp()
	{
		return bindIp;
	}

	public int getBindPort()
	{
		return bindPort;
	}

	public ConnectionState getConnectionState()
	{
		return connectionState;
	}

	public int getCountOfRecordSendFail()
	{
		return countOfRecordSendFail;
	}

	public String getDesc4Err()
	{
		return desc4Err;
	}

	public ErrorPackageHandlerIntf getErrorPackageHandler()
	{
		return errorPackageHandler;
	}

	/**
	 * 获取id
	 * 
	 * @return String
	 */
	public String getId()
	{
		return id;
	}

	public String getMyIp()
	{
		return myIp;
	}

	public int getMyPort()
	{
		return myPort;
	}

	public PacketHandlerIntf getPacketHandler()
	{
		return packetHandler;
	}

	public HandlerRunnable getHandlerRunnable()
	{
		return handlerRunnable;
	}

	public DecoderIntf getDecoder()
	{
		return decoder;
	}

	public DecodeRunnable getDecodeRunnable()
	{
		return decodeRunnable;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public Proxy getProxy()
	{
		return proxy;
	}

	public ReadIOErrorHandlerIntf getReadIOErrorHandler()
	{
		return readIOErrorHandler;
	}

	/**
	 * 获取Route基本信息
	 * 
	 * @return String
	 */
	public RemoteNode getRemoteNode()
	{
		return remoteNode;
	}

	public AtomicLong getSeqNo()
	{
		return seqNo;
	}

	public SocketChannel getSocketChannel()
	{
		return socketChannel;
	}

	public SendRunnable getSendRunnable()
	{
		return sendRunnable;
	}

	public StatVo getStatVo()
	{
		return statVo;
	}

	public WriteIOErrorHandlerIntf getWriteIOErrorHandler()
	{
		return writeIOErrorHandler;
	}

	/**
	 * true : ConnectionState.APP_ON
	 * 
	 * @return
	 */
	public boolean isAppOn()
	{
		return connectionState == ConnectionState.APP_ON;
	}

	// public Map<String, HandlerRunnable>
	// getMapOfMsgtypeAndPacketHandlerTask()
	// {
	// return mapOfMsgtypeAndPacketHandlerTask;
	// }
	//
	// public void setMapOfMsgtypeAndPacketHandlerTask(Map<String,
	// HandlerRunnable> mapOfMsgtypeAndPacketHandlerTask)
	// {
	// this.mapOfMsgtypeAndPacketHandlerTask = mapOfMsgtypeAndPacketHandlerTask;
	// }

	public boolean isAutoConnection()
	{
		return isAutoConnection;
	}

	/**
	 * 当前链路状态下，是否需要建链
	 * 
	 * @author tanyaowu
	 * @param connectionState
	 * @return
	 */
	public boolean isNeedBuildLink(ConnectionState connectionState)
	{
		return isAutoConnection && (needBuildConnectionStateSet.contains(connectionState));
	}

	public boolean isNeedRecordSendFailMsg()
	{
		return isNeedRecordSendFailMsg;
	}

	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		listeners.removePropertyChangeListener(l);
	}

	public void setAutoConnection(boolean isAutoConnection)
	{
		this.isAutoConnection = isAutoConnection;
	}

	public void setBindIp(String bindIp)
	{
		this.bindIp = bindIp;
	}

	public void setBindPort(int bindPort)
	{
		this.bindPort = bindPort;
	}

	public void setConnectionState(ConnectionState connectionState)
	{
		if (this.connectionState == connectionState)
		{
			return;
		}

		ConnectionState oldConnectionState = this.connectionState;
		this.connectionState = connectionState;
		firePropertyChange("connectionState", oldConnectionState, connectionState);

	}

	public void setCountOfRecordSendFail(int countOfRecordSendFail)
	{
		this.countOfRecordSendFail = countOfRecordSendFail;
	}

	public void setDesc4Err(String desc4Err)
	{
		this.desc4Err = desc4Err;
	}

	public void setErrorPackageHandler(ErrorPackageHandlerIntf errorPackageHandler)
	{
		this.errorPackageHandler = errorPackageHandler;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setMyIp(String myIp)
	{
		this.myIp = myIp;
	}

	public void setMyPort(int myPort)
	{
		this.myPort = myPort;
	}

	public void setNeedRecordSendFailMsg(boolean isNeedRecordSendFailMsg)
	{
		this.isNeedRecordSendFailMsg = isNeedRecordSendFailMsg;
	}

	public void setPacketHandler(PacketHandlerIntf packetHandler)
	{
		this.packetHandler = packetHandler;
	}

	public void setHandlerRunnable(HandlerRunnable handlerRunnable)
	{
		this.handlerRunnable = handlerRunnable;
	}

	public void setDecoder(DecoderIntf decoder)
	{
		this.decoder = decoder;
	}

	public void setDecodeRunnable(DecodeRunnable decodeRunnable)
	{
		this.decodeRunnable = decodeRunnable;
		this.handlerRunnable.setParent(decodeRunnable);
	}

	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}

	public void setProxy(Proxy proxy)
	{
		this.proxy = proxy;
	}

	public void setReadIOErrorHandler(ReadIOErrorHandlerIntf readIOErrorHandler)
	{
		this.readIOErrorHandler = readIOErrorHandler;
	}

	public void setSeqNo(AtomicLong seqNo)
	{
		this.seqNo = seqNo;
	}

	public void setSocketChannel(SocketChannel socketChannel)
	{
		this.socketChannel = socketChannel;
	}

	public void setSendRunnable(SendRunnable sendRunnable)
	{
		this.sendRunnable = sendRunnable;
	}

	public void setStatVo(StatVo statVo)
	{
		this.statVo = statVo;
	}

	public void setWriteIOErrorHandler(WriteIOErrorHandlerIntf writeIOErrorHandler)
	{
		this.writeIOErrorHandler = writeIOErrorHandler;
	}

	@Override
	public String toString()
	{

		return id;
	}

	public Map<String, Object> getProps()
	{
		return props;
	}

	public void setProps(Map<String, Object> props)
	{
		this.props = props;
	}

	public ConnectionStateListener getConnectionStateListener()
	{
		return connectionStateListener;
	}

	private static java.util.concurrent.atomic.AtomicLong c = new java.util.concurrent.atomic.AtomicLong();

	public void setConnectionStateListener(final ConnectionStateListener connectionStateListener)
	{
		this.connectionStateListener = connectionStateListener;

		if (connectionStateListener != null)
		{
			this.addPropertyChangeListener(new PropertyChangeListener()
			{
				@Override
				public void propertyChange(PropertyChangeEvent evt)
				{
					if ("connectionState".equals(evt.getPropertyName()))
					{
						ConnectionState newValue = (ConnectionState) evt.getNewValue();
						log.info("{}---remote: {}", newValue, ChannelContext.this.getId());

						log.warn(newValue + "-" + c.incrementAndGet());

						if (newValue == ConnectionState.TCP_OFF)
						{
							try
							{
								connectionStateListener.onTcpOff(ChannelContext.this);
							} catch (Exception e)
							{
								throw new RuntimeException(e);
							}
						} else if (newValue == ConnectionState.TCP_BUILDING)
						{
							try
							{
								connectionStateListener.onTcpBuilding(ChannelContext.this);
							} catch (Exception e)
							{
								throw new RuntimeException(e);
							}
						} else if (newValue == ConnectionState.TCP_ON)
						{
							try
							{
								connectionStateListener.onTcpOn(ChannelContext.this);
							} catch (Exception e)
							{
								throw new RuntimeException(e);
							}
						} else if (newValue == ConnectionState.TCP_LINKFAILED)
						{
							try
							{
								connectionStateListener.onTcpLinkFailed(ChannelContext.this);
							} catch (Exception e)
							{
								throw new RuntimeException(e);
							}
						} else if (newValue == ConnectionState.APP_OFF)
						{
							try
							{
								connectionStateListener.onAppOff(ChannelContext.this);
							} catch (Exception e)
							{
								throw new RuntimeException(e);
							}
						} else if (newValue == ConnectionState.APP_BUILDING)
						{
							try
							{
								connectionStateListener.onAppBuilding(ChannelContext.this);
							} catch (Exception e)
							{
								throw new RuntimeException(e);
							}
						} else if (newValue == ConnectionState.APP_ON)
						{
							ChannelContext.this.getStatVo().setStateTimeAppBuilding(SystemTimer.currentTimeMillis());
							try
							{
								connectionStateListener.onAppOn(ChannelContext.this);
							} catch (Exception e)
							{
								throw new RuntimeException(e);
							}
						} else if (newValue == ConnectionState.APP_LINKFAILED)
						{
							try
							{
								connectionStateListener.onAppLinkFailed(ChannelContext.this);
							} catch (Exception e)
							{
								throw new RuntimeException(e);
							}
						} else if (newValue == ConnectionState.LOGOUTING)
						{
							try
							{
								connectionStateListener.onLogouting(ChannelContext.this);
							} catch (Exception e)
							{
								throw new RuntimeException(e);
							}
						}
					}
				}
			});
		}
	}

	public ByteOrder getByteOrder()
	{
		return byteOrder;
	}

	public void setByteOrder(ByteOrder byteOrder)
	{
		this.byteOrder = byteOrder;
	}

}