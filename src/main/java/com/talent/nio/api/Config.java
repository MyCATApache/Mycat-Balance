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
package com.talent.nio.api;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class Config
{

	private long sleepSize = 200000L;

	private long sleepTime = 500;

	private long threadPoolInitNum = Runtime.getRuntime().availableProcessors() * 8L;

	private long threadPoolMaxNum = threadPoolInitNum * 3L;

	private long threadPoolKeepAliveTime = 90L;

	/**
	 * 当待处理的消息数量大于此值时，就不再往队列里放消息了
	 */
	private long queueMaxSizeOfpacketHandlerTask = 800000000L;

	private long periodOfSendHeartMsg = 3000L;

	private long periodOfHeartMsgCheck = 6000L;

	private long periodOfLinkCheck = 3000L;

	private static Config instance = null;

	private Config()
	{

	}

	public static Config getInstance()
	{
		if (instance == null)
		{
			synchronized (Config.class)
			{
				if (instance == null)
				{
					instance = new Config();
				}
			}
		}
		return instance;
	}

	public static void main(String[] args) throws Exception, InvocationTargetException, NoSuchMethodException
	{
		File f = new File("G:/ruiyiteng/project/caiji/lib");
		File[] fs = f.listFiles();
		String x = "";
		for (File file : fs)
		{
			x += ".\\lib\\" + file.getName() + ";";
		}
		System.out.println(x);
	}

	public long getSleepSize()
	{
		return sleepSize;
	}

	public void setSleepSize(long sleepSize)
	{
		this.sleepSize = sleepSize;
	}

	public long getSleepTime()
	{
		return sleepTime;
	}

	public void setSleepTime(long sleepTime)
	{
		this.sleepTime = sleepTime;
	}

	public long getThreadPoolInitNum()
	{
		return threadPoolInitNum;
	}

	public void setThreadPoolInitNum(long threadPoolInitNum)
	{
		this.threadPoolInitNum = threadPoolInitNum;
	}

	public long getThreadPoolMaxNum()
	{
		return threadPoolMaxNum;
	}

	public void setThreadPoolMaxNum(long threadPoolMaxNum)
	{
		this.threadPoolMaxNum = threadPoolMaxNum;
	}

	public long getThreadPoolKeepAliveTime()
	{
		return threadPoolKeepAliveTime;
	}

	public void setThreadPoolKeepAliveTime(long threadPoolKeepAliveTime)
	{
		this.threadPoolKeepAliveTime = threadPoolKeepAliveTime;
	}

	public long getQueueMaxSizeOfpacketHandlerTask()
	{
		return queueMaxSizeOfpacketHandlerTask;
	}

	public void setQueueMaxSizeOfpacketHandlerTask(long queueMaxSizeOfpacketHandlerTask)
	{
		this.queueMaxSizeOfpacketHandlerTask = queueMaxSizeOfpacketHandlerTask;
	}

	public long getPeriodOfSendHeartMsg()
	{
		return periodOfSendHeartMsg;
	}

	public void setPeriodOfSendHeartMsg(long periodOfSendHeartMsg)
	{
		this.periodOfSendHeartMsg = periodOfSendHeartMsg;
	}

	public long getPeriodOfHeartMsgCheck()
	{
		return periodOfHeartMsgCheck;
	}

	public void setPeriodOfHeartMsgCheck(long periodOfHeartMsgCheck)
	{
		this.periodOfHeartMsgCheck = periodOfHeartMsgCheck;
	}

	public long getPeriodOfLinkCheck()
	{
		return periodOfLinkCheck;
	}

	public void setPeriodOfLinkCheck(long periodOfLinkCheck)
	{
		this.periodOfLinkCheck = periodOfLinkCheck;
	}
}