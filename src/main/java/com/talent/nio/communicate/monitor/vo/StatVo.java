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
package com.talent.nio.communicate.monitor.vo;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.utils.SystemTimer;

/**
 * 
 * @filename: com.talent.nio.communicate.monitor.vo.StatVo
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013年9月23日 上午8:44:19
 * @record <table cellPadding="3" cellSpacing="0" style="width:600px">
 *         <thead style="font-weight:bold;background-color:#e3e197">
 *         <tr>
 *         <td>date</td>
 *         <td>author</td>
 *         <td>version</td>
 *         <td>description</td>
 *         </tr>
 *         </thead> <tbody style="background-color:#ffffeb">
 *         <tr>
 *         <td>2013年9月23日</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class StatVo
{
	private static Logger log = LoggerFactory.getLogger(StatVo.class);

	/**
	 * 
	 */
	public StatVo()
	{

	}

	/**
	 * 待组包的bytebuffer个数
	 */
	private int packetOgnzerQueueSize = 0;

	/**
	 * 待发送的packet个数
	 */
	private int senderQueueSize = 0;

	/**
	 * 待处理的packet个数
	 */
	private int packetHandlerQueueSize = 0;

	/**
	 * 本连接已发送的字节数
	 */
	private long sentBytes = 0;

	/**
	 * 本连接已接收的字节数
	 */
	private long receivedBytes = 0;

	/**
	 * 总发送量
	 */
	private static AtomicLong allSentMsgCount = new AtomicLong();

	/**
	 * 所有连接的消息发送量，单位：byte
	 */
	private static AtomicLong allSentBytes = new AtomicLong();

	/**
	 * 从通道读数据的次数
	 */
	private AtomicLong readCount = new AtomicLong(); //

	/**
	 * TCP_OFF,
		TCP_BUILDING,
		TCP_ON,
		TCP_LINKFAILED,
		APP_OFF,
		APP_BUILDING,
		APP_ON,
		APP_LINKFAILED,
		LOGOUTING,
		REMOVED
	 * tcp建链时间
	 */
	
	private Timestamp stateTimeTcpOff = new Timestamp(0L);
	private Timestamp stateTimeTcpBuilding = new Timestamp(0L);
	private Timestamp stateTimeTcpOn = new Timestamp(0L);
	private Timestamp stateTimeTcpLinkfailed = new Timestamp(0L);
	private Timestamp stateTimeAppOff = new Timestamp(0L);
	private Timestamp stateTimeAppBuilding = new Timestamp(0L);
	private Timestamp stateTimeAppOn = new Timestamp(0L);
	private Timestamp stateTimeAppLinkfailed = new Timestamp(0L);
	private Timestamp stateTimeLogouting = new Timestamp(0L);
	private Timestamp stateTimeRemoved = new Timestamp(0L);
	
	

	/**
	 * 最近一次接收消息的时间
	 */
	private Timestamp currentReceivedTime = new Timestamp(SystemTimer.currentTimeMillis());

	/**
	 * 最近一次提交组包的时间
	 */
	private Timestamp currentOgnzTimestamp = new Timestamp(0L);

	/**
	 * 最近一次发送消息时间
	 */
	private Timestamp currentSendTime = new Timestamp(SystemTimer.currentTimeMillis());

	/**
	 * 建链异常的次数
	 */
	private AtomicLong buildExceptionTimes = new AtomicLong();

	/**
	 * 读异常次数
	 */
	private AtomicLong countOfReadException = new AtomicLong();

	/**
	 * 写异常次数
	 */
	private AtomicLong countOfWriteException = new AtomicLong();

	/**
	 * 接收到错误包的个数
	 */
	private AtomicLong countOfErrorPackage = new AtomicLong();

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	public int getPacketOgnzerQueueSize()
	{
		return packetOgnzerQueueSize;
	}

	public void setPacketOgnzerQueueSize(int packetOgnzerQueueSize)
	{
		this.packetOgnzerQueueSize = packetOgnzerQueueSize;
	}

	public int getSenderQueueSize()
	{
		return senderQueueSize;
	}

	public void setSenderQueueSize(int senderQueueSize)
	{
		this.senderQueueSize = senderQueueSize;
	}

	public int getPacketHandlerQueueSize()
	{
		return packetHandlerQueueSize;
	}

	public void setPacketHandlerQueueSize(int packetHandlerQueueSize)
	{
		this.packetHandlerQueueSize = packetHandlerQueueSize;
	}

	public long getSentBytes()
	{
		return sentBytes;
	}

	public void setSentBytes(long sentBytes)
	{
		this.sentBytes = sentBytes;
	}

	public long getReceivedBytes()
	{
		return receivedBytes;
	}

	public void setReceivedBytes(long receivedBytes)
	{
		this.receivedBytes = receivedBytes;
	}

	public Timestamp getStateTimeTcpBuilding()
	{
		return stateTimeTcpBuilding;
	}

	public void setStateTimeTcpBuilding(long stateTimeTcpBuilding)
	{
		this.stateTimeTcpBuilding.setTime(stateTimeTcpBuilding);
	}

	public Timestamp getStateTimeAppBuilding()
	{
		return stateTimeAppBuilding;
	}

	public void setStateTimeAppBuilding(long stateTimeAppBuilding)
	{
		this.stateTimeAppBuilding.setTime(stateTimeAppBuilding);
	}

	public Timestamp getCurrentReceivedTime()
	{
		return currentReceivedTime;
	}

	public void setCurrentReceivedTime(long currentReceivedTime)
	{
		this.currentReceivedTime.setTime(currentReceivedTime);
	}

	public Timestamp getCurrentSendTime()
	{
		return currentSendTime;
	}

	public void setCurrentSendTime(long currentSendTime)
	{
		this.currentSendTime.setTime(currentSendTime);
	}

	public AtomicLong getBuildExceptionTimes()
	{
		return buildExceptionTimes;
	}

	public void setBuildExceptionTimes(AtomicLong buildExceptionTimes)
	{
		this.buildExceptionTimes = buildExceptionTimes;
	}

	public AtomicLong getCountOfReadException()
	{
		return countOfReadException;
	}

	public void setCountOfReadException(AtomicLong countOfReadException)
	{
		this.countOfReadException = countOfReadException;
	}

	public AtomicLong getCountOfWriteException()
	{
		return countOfWriteException;
	}

	public void setCountOfWriteException(AtomicLong countOfWriteException)
	{
		this.countOfWriteException = countOfWriteException;
	}

	public AtomicLong getCountOfErrorPackage()
	{
		return countOfErrorPackage;
	}

	public void setCountOfErrorPackage(AtomicLong countOfErrorPackage)
	{
		this.countOfErrorPackage = countOfErrorPackage;
	}

	public AtomicLong getReadCount()
	{
		return readCount;
	}

	public void setReadCount(AtomicLong readCount)
	{
		this.readCount = readCount;
	}

	public static AtomicLong getAllSentMsgCount()
	{
		return allSentMsgCount;
	}

	public static void setAllSentMsgCount(AtomicLong allSentMsgCount)
	{
		StatVo.allSentMsgCount = allSentMsgCount;
	}

	public static AtomicLong getAllSentBytes()
	{
		return allSentBytes;
	}

	public static void setAllSentBytes(AtomicLong allSentBytes)
	{
		StatVo.allSentBytes = allSentBytes;
	}

	public Timestamp getCurrentOgnzTimestamp()
	{
		return currentOgnzTimestamp;
	}

	public void setCurrentOgnzTimestamp(long currentOgnzTimestamp)
	{
		this.currentOgnzTimestamp.setTime(currentOgnzTimestamp);
	}

	public Timestamp getStateTimeTcpOff()
	{
		return stateTimeTcpOff;
	}

	public void setStateTimeTcpOff(long stateTimeTcpOff)
	{
		this.stateTimeTcpOff.setTime(stateTimeTcpOff);
	}

	public Timestamp getStateTimeTcpOn()
	{
		return stateTimeTcpOn;
	}

	public void setStateTimeTcpOn(long stateTimeTcpOn)
	{
		this.stateTimeTcpOn.setTime(stateTimeTcpOn);
	}

	public Timestamp getStateTimeTcpLinkfailed()
	{
		return stateTimeTcpLinkfailed;
	}

	public void setStateTimeTcpLinkfailed(long stateTimeTcpLinkfailed)
	{
		this.stateTimeTcpLinkfailed.setTime(stateTimeTcpLinkfailed);
	}

	public Timestamp getStateTimeAppOff()
	{
		return stateTimeAppOff;
	}

	public void setStateTimeAppOff(long stateTimeAppOff)
	{
		this.stateTimeAppOff.setTime(stateTimeAppOff);
	}

	public Timestamp getStateTimeAppOn()
	{
		return stateTimeAppOn;
	}

	public void setStateTimeAppOn(long stateTimeAppOn)
	{
		this.stateTimeAppOn.setTime(stateTimeAppOn);
	}

	public Timestamp getStateTimeAppLinkfailed()
	{
		return stateTimeAppLinkfailed;
	}

	public void setStateTimeAppLinkfailed(long stateTimeAppLinkfailed)
	{
		this.stateTimeAppLinkfailed.setTime(stateTimeAppLinkfailed);
	}

	public Timestamp getStateTimeLogouting()
	{
		return stateTimeLogouting;
	}

	public void setStateTimeLogouting(long stateTimeLogouting)
	{
		this.stateTimeLogouting.setTime(stateTimeLogouting);
	}

	public Timestamp getStateTimeRemoved()
	{
		return stateTimeRemoved;
	}

	public void setStateTimeRemoved(long stateTimeRemoved)
	{
		this.stateTimeRemoved.setTime(stateTimeRemoved);
	}

}