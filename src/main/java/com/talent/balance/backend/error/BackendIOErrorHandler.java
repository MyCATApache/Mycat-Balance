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
package com.talent.balance.backend.error;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.backend.ext.BackendExt;
import com.talent.balance.conf.BackendServerConf;
import com.talent.nio.api.Nio;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.handler.error.intf.ReadIOErrorHandlerIntf;
import com.talent.nio.handler.error.intf.WriteIOErrorHandlerIntf;
import com.talent.nio.utils.NetUtils;

/**
 * 
 * @filename:	 com.talent.balance.frontend.error.FrontendIOErrorHandler
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月23日 上午10:14:15
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月23日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class BackendIOErrorHandler implements WriteIOErrorHandlerIntf, ReadIOErrorHandlerIntf
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5369456663702756847L;
	private static Logger log = LoggerFactory.getLogger(BackendIOErrorHandler.class);

	private static BackendIOErrorHandler instance = new BackendIOErrorHandler();

	public static BackendIOErrorHandler getInstance()
	{
		return instance;
	}

	/**
	 * 
	 */
	private BackendIOErrorHandler()
	{

	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{

	}

	@Override
	public void handle(SocketChannel socketChannel, IOException e, ChannelContext backendChannelContext,
			String customMsg)
	{
		String reasonString = "";
		if (backendChannelContext != null)
		{
			StringBuilder buffer = new StringBuilder();
			String _customMsg = customMsg == null ? "IOException" : customMsg;
			buffer.append(backendChannelContext.getId() + " " + _customMsg);
			if (e != null)
			{
				reasonString = e.getMessage();
				log.error(buffer.toString(), e);

				if (e instanceof java.net.ConnectException)
				{
					BackendServerConf backendServerConf = BackendExt.getBackendServer(backendChannelContext);

					if (backendServerConf != null)
					{
						backendServerConf.setConnectable(NetUtils.isConnectable(backendServerConf.getIp(),
								backendServerConf.getPort()));
					}
				}
			} else
			{
				log.error(buffer.toString());
			}
		}
		Nio.getInstance().removeConnection(backendChannelContext, reasonString);

		ChannelContext frontendChannelContext = BackendExt.getFrontend(backendChannelContext);
		if (frontendChannelContext != null)
		{
			Nio.getInstance().removeConnection(frontendChannelContext, reasonString);
			BackendExt.removeFrontend(backendChannelContext);
			BackendExt.removeBackendServer(backendChannelContext);
		}
	}
}