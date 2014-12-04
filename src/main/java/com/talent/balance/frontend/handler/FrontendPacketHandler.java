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
package com.talent.balance.frontend.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.backend.ext.BackendExt;
import com.talent.balance.common.BalancePacket;
import com.talent.balance.conf.BackendServerConf;
import com.talent.balance.frontend.ext.FrontendExt;
import com.talent.nio.api.Nio;
import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.ChannelContext.ConnectionState;
import com.talent.nio.communicate.handler.intf.PacketHandlerIntf;
import com.talent.nio.communicate.util.NioUtils;

/**
 * 
 * @filename: com.talent.http.server.HttpPacketHandler
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-9-16 下午5:44:15
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
 *         <td>2013-9-16</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class FrontendPacketHandler implements PacketHandlerIntf
{
	private static Logger log = LoggerFactory.getLogger(FrontendPacketHandler.class);

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
	}

	/**
	 * 
	 */
	public FrontendPacketHandler()
	{

	}

	@Override
	public void onReceived(Packet packet, ChannelContext channelContext) throws Exception
	{
		ChannelContext backendChannelContext = FrontendExt.getBackend(channelContext);
		//		if (backendChannelContext == null || !backendChannelContext.isAppOn())
		//		{
		//			BalanceBackendClient.registerFrontendClient(channelContext);
		//		}

		int c = 0;
		while (backendChannelContext == null && c++ < 1000)
		{
			backendChannelContext = FrontendExt.getBackend(channelContext);
			Thread.sleep(5);
		}
		if (backendChannelContext == null)
		{
			throw new IOException("backendChannelContext == null");
		}

//		if (backendChannelContext.isNeedBuildLink(backendChannelContext.getConnectionState()))
//		{
//			NioUtils.buildLink(backendChannelContext);
//		}

		c = 0;
		while (backendChannelContext.getConnectionState() != ConnectionState.APP_ON && c++ < 1000)
		{
			Thread.sleep(5);
		}
		if (backendChannelContext.getConnectionState() != ConnectionState.APP_ON)
		{
			throw new IOException("backendChannelContext.getConnectionState() != ConnectionState.APP_ON");
		}

		BalancePacket balancePacket = (BalancePacket) packet;
		//		log.warn("receive from front {}", balancePacket.getBuffer().capacity());
		//		
		//		byte[] bs = balancePacket.getBuffer().array();
		//		FileUtils.writeStringToFile(new File("h:/"+FrontendExt.getBackend(channelContext).hashCode()+"__"+channelContext.hashCode()+"/fromFront.txt"), Arrays.toString(bs), true);

		BackendServerConf backendServerConf = BackendExt.getBackendServer(backendChannelContext);
		backendServerConf.getStat().increReceivedBytes(balancePacket.getBuffer().capacity());

		Nio.getInstance().asySend(balancePacket, backendChannelContext);
	}

	@Override
	public byte[] onSend(Packet packet, ChannelContext channelContext) throws Exception
	{
		BalancePacket balancePacket = (BalancePacket) packet;

		try
		{
			byte[] bs = balancePacket.getBuffer().array();
			//			FileUtils.writeStringToFile(new File("h:/"+FrontendExt.getBackend(channelContext).hashCode()+"__"+channelContext.hashCode()+"/toFront.txt"), Arrays.toString(bs), true);
			return bs;
		} catch (UnsupportedOperationException e)
		{
			log.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

}