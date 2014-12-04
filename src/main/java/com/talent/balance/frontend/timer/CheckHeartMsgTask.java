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
package com.talent.balance.frontend.timer;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.conf.FrontendConf;
import com.talent.nio.api.Nio;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.utils.SystemTimer;

/**
 * 
 * 
 * @filename: com.talent.http.server.timer.CheckHeartMsgTask
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-9-20 下午6:52:09
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
 *         <td>2013-9-20</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class CheckHeartMsgTask extends java.util.TimerTask
{
	private static final Logger log = LoggerFactory.getLogger(CheckHeartMsgTask.class);

	private static void checkHeartMsgs()
	{
		try
		{
			log.debug("start checking heart msgs");

			long interval = 1000 * 60 * 60 * 24L;

			List<ChannelContext> channelContexts = Nio.getInstance().getConnectionsByProtocol(
					FrontendConf.getInstance().getProtocol());
			if (channelContexts == null)
			{
				return;
			}

			for (int i = 0; i < channelContexts.size(); i++)
			{
				ChannelContext channelContext;
				try
				{
					channelContext = channelContexts.get(i);
				} catch (IndexOutOfBoundsException e) // 有可能有些元素被删除掉了，所以不用再进一步处理
				{
					break;
				}

				Timestamp cTimestamp = new Timestamp(SystemTimer.currentTimeMillis());
				long currTime = cTimestamp.getTime();

				Timestamp currentReceivedTime1 = channelContext.getStatVo().getCurrentReceivedTime();
				Timestamp currentSendTime1 = channelContext.getStatVo().getCurrentSendTime();
				Timestamp currentOgnzTimestamp1 = channelContext.getStatVo().getCurrentOgnzTimestamp();

				long currentReceivedTime = currentReceivedTime1.getTime();
				long currentSendTime = currentSendTime1.getTime();
				long currentOgnzTimestamp = currentOgnzTimestamp1.getTime();

				long t1 = currTime - currentReceivedTime;
				long t2 = currTime - currentSendTime;
				long t3 = currTime - currentOgnzTimestamp;
				if (((t1) > interval) && (t2 > interval) && (t3 > interval))
				{
					if (t1 > interval)
					{
						log.info("{}--(currTime({}) - currentReceivedTime({})) = {} > interval({})",
								channelContext.getId(), cTimestamp, currentReceivedTime1, t1, interval);
					}
					if (t2 > interval)
					{
						log.info("{}--(currTime({}) - currentSendTime({})) = {} > interval({})",
								channelContext.getId(), cTimestamp, currentSendTime1, t2, interval);
					}

					Nio.getInstance().removeConnection(channelContext,
							"it received/sent nothing in " + t1 + "/" + t2 + " ms");
				}
			}
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	/**
	 * 
	 */
	public CheckHeartMsgTask()
	{

	}

	@Override
	public void run()
	{
		checkHeartMsgs();
	}
}