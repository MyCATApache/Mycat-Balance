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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.handler.intf.MessageChangeListener;
import com.talent.nio.communicate.send.SendUtils;
import com.talent.nio.communicate.server.ServerContext;
import com.talent.nio.communicate.util.NioUtils;
import com.talent.nio.connmgr.ConnectionManager;
import com.talent.nio.startup.Startup;

/**
 * 
 * 
 * @filename: com.talent.nio.api.Nio
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2012年9月6日 上午7:07:13
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
 *         <td>2012年9月6日</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class Nio
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(Nio.class);

	private static Nio instance = null;

	public static Nio getInstance()
	{
		if (instance == null)
		{
			instance = new Nio();
		}
		return instance;
	}

	/**
	 * 
	 */
	public Nio()
	{

	}

	public void disconnect(ChannelContext channelContext, String reasonString)
	{
		NioUtils.disconnect(channelContext, reasonString);
	}

	public void acceptAt(ServerContext serverContext) throws IOException
	{
		Startup.getTcpListener().acceptAt(serverContext);
	}

	public List<ChannelContext> getConnectionsByProtocol(String protocol)
	{
		return ConnectionManager.getInstance().getConnectionsByProtocol(protocol);
	}

	public Collection<ChannelContext> getConnections()
	{
		return ConnectionManager.getInstance().getConnections();
	}

	// ------------------------------ 客户端 start
	// ----------------------------------------------

	public boolean asySend(Packet packet, boolean isNeedAppOn, ChannelContext channelContext) throws Exception
	{
		return SendUtils.asySend(packet, isNeedAppOn, channelContext);
	}

	public Packet synSend(Packet packet, ChannelContext channelContext) throws Exception
	{
		return synSend(packet, 5000, channelContext);
	}

	public void addConnection(ChannelContext channelContext)
	{
		ConnectionManager.getInstance().addConnection(channelContext);

		NioUtils.buildLink(channelContext);
	}

	public Packet synSend(Packet packet, boolean isNeedAppOn, long timeout, ChannelContext channelContext)
			throws Exception
	{
		return SendUtils.synSend(packet, isNeedAppOn, timeout, channelContext);
	}

	public List<Packet> blockSend(Packet packet, MessageChangeListener msgChangeListener, boolean isNeedAppOn,
			long timeout, ChannelContext channelContext) throws Exception
	{
		return com.talent.nio.communicate.send.SendUtils.blockSend(packet, msgChangeListener, isNeedAppOn, timeout,
				channelContext);
	}

	public void updateConnection(ChannelContext channelContext)
	{
		removeConnection(channelContext, "update");
		addConnection(channelContext);
	}

	public void removeConnection(ChannelContext channelContext, String reason)
	{
		NioUtils.remove(channelContext, reason);
	}

	public boolean asySend(Packet packet, ChannelContext channelContext) throws Exception
	{
		return asySend(packet, true, channelContext);
	}

	public Packet synSend(Packet packet, long timeout, ChannelContext channelContext) throws Exception
	{
		return synSend(packet, true, timeout, channelContext);
	}

	public List<Packet> blockSend(Packet packet, MessageChangeListener msgChangeListener, long timeout,
			ChannelContext channelContext) throws Exception
	{
		return blockSend(packet, msgChangeListener, true, timeout, channelContext);
	}

}