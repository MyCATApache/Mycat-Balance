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
package com.talent.balance.backend.handler;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.backend.ext.BackendExt;
import com.talent.balance.common.BalancePacket;
import com.talent.balance.conf.BackendServerConf;
import com.talent.mysql.ext.MysqlExt;
import com.talent.mysql.packet.MysqlRequestPacket;
import com.talent.mysql.packet.request.AuthPacket;
import com.talent.mysql.packet.response.HandshakePacket;
import com.talent.nio.api.Nio;
import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.ChannelContext.ConnectionState;
import com.talent.nio.communicate.handler.intf.PacketHandlerIntf;

/**
 * 
 * @filename: com.talent.nio.demo.PacketHandlerDemo
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
public class BackendPacketHandler implements PacketHandlerIntf
{
	static long count = 0;

	private static Logger log = LoggerFactory.getLogger(BackendPacketHandler.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	/**
	 * 
	 */
	public BackendPacketHandler()
	{

	}

	@Override
	public void onReceived(Packet packet, ChannelContext channelContext) throws Exception
	{
		BackendServerConf backendServerConf = BackendExt.getBackendServer(channelContext);

		if (MysqlExt.getHandshakePacket(channelContext) == null && BackendExt.PROTOCOL_MYSQL.equals(channelContext.getProtocol()))
		{
			HandshakePacket handshakePacket = (HandshakePacket) packet;
			MysqlExt.setHandshakePacket(channelContext, handshakePacket);

			AuthPacket authPacket = new AuthPacket();
			authPacket.charsetIndex = 33;//(byte) (handshakePacket.charset & 0xff);
			authPacket.user = backendServerConf.getProps().get("user").getBytes();
			authPacket.password = AuthPacket.getPass(backendServerConf.getProps().get("pwd"), handshakePacket);
			authPacket.passwordLen = (byte) authPacket.password.length;
			authPacket.database = backendServerConf.getProps().get("db").getBytes();
			Nio.getInstance().asySend(authPacket, channelContext);
			return;
		}

		BalancePacket balancePacket = (BalancePacket) packet;

//		byte[] bs = balancePacket.getBuffer().array();
//		log.warn("receive from back {}, {}, {}", balancePacket.getBuffer().capacity(), Arrays.toString(bs), new String(
//				bs, "utf-8"));
//		FileUtils.writeStringToFile(new File("h:/"+channelContext.hashCode()+"__"+BackendExt.getFrontend(channelContext).hashCode()+"/fromBackend.txt"), Arrays.toString(bs), true);

		backendServerConf.getStat().increSentBytes(balancePacket.getBuffer().capacity());

		// check frontendChannelContext start
		ChannelContext frontendChannelContext = BackendExt.getFrontend(channelContext);
		if (frontendChannelContext == null)
		{
			throw new IOException("frontendChannelContext is null");
		}
		
		if (frontendChannelContext.getConnectionState() != ConnectionState.APP_ON)
		{
			throw new IOException("frontendChannelContext.getConnectionState() = " + frontendChannelContext.getConnectionState());
		}
		// check frontendChannelContext end

		Nio.getInstance().asySend(balancePacket, frontendChannelContext);
	}

	@Override
	public byte[] onSend(Packet packet, ChannelContext channelContext) throws Exception
	{
		BalancePacket balancePacket = (BalancePacket) packet;

		if (BackendExt.PROTOCOL_MYSQL.equals(channelContext.getProtocol()))
		{
			MysqlRequestPacket mysqlRequestPacket = (MysqlRequestPacket) balancePacket;
			ByteBuf byteBuf = mysqlRequestPacket.encode();
			byte[] bs1 = byteBuf.array();
			log.warn("sent to backend:{}", Arrays.toString(bs1));
			return bs1;
		}

		byte[] bs = balancePacket.getBuffer().array();
//		log.warn("sent to back {}, {}, {}", balancePacket.getBuffer().capacity(), Arrays.toString(bs), new String(bs,
//				"utf-8"));
//		FileUtils.writeStringToFile(new File("h:/"+channelContext.hashCode()+"__"+BackendExt.getFrontend(channelContext).hashCode()+"/toBackend.txt"), Arrays.toString(bs), true);

		return bs;
	}
}