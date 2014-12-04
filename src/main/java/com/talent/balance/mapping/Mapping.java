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
package com.talent.balance.mapping;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.conf.BackendConf;
import com.talent.balance.conf.BackendServerConf;
import com.talent.balance.frontend.ext.FrontendExt;
import com.talent.nio.api.Nio;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.utils.NetUtils;

/**
 * 
 * @filename:	 com.talent.balance.mapping.Mapping
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月23日 上午11:55:40
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
public class Mapping
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(Mapping.class);

	/**
	 * key: frontendChannelContext; value: 后端BackendServer对象
	 */
	private static Map<ChannelContext, BackendServerConf> frontChannelcontextAndBackserverMap = new HashMap<ChannelContext, BackendServerConf>();

	/**
	 * 
	 * @param frontendChannelContext
	 */
	public static void remove(ChannelContext frontendChannelContext)
	{
		frontChannelcontextAndBackserverMap.remove(frontendChannelContext);
	}

	/**
	 * 根据frontendChannelContext找到后端的BackendServer对象
	 * @param frontip
	 * @return
	 */
	public static BackendServerConf getBackendServer(ChannelContext frontendChannelContext)
	{
		//		String frontip = frontendChannelContext.getRemoteNode().getIp();
		BackendServerConf backendServer = frontChannelcontextAndBackserverMap.get(frontendChannelContext);
		if (backendServer == null)
		{
			backendServer = assignServer();
			frontChannelcontextAndBackserverMap.put(frontendChannelContext, backendServer);
		} else
		{
			ChannelContext backendChannelContext = FrontendExt.getBackend(frontendChannelContext);
			if (!backendChannelContext.isAppOn())
			{
				if (!NetUtils.isConnectable(backendServer.getIp(), backendServer.getPort()))
				{
					Nio.getInstance().removeConnection(backendChannelContext, "not connectable");
					backendServer = assignServer();
					frontChannelcontextAndBackserverMap.put(frontendChannelContext, backendServer);
				}
			}
		}
		return backendServer;
	}

	/**
	 * 为前端分配后端服务器
	 * @param frontip
	 * @return
	 */
	private static BackendServerConf assignServer()
	{
		BackendServerConf[] servers = BackendConf.getInstance().getServers();

		Arrays.sort(servers, new Comparator<BackendServerConf>()
		{
			@Override
			public int compare(BackendServerConf o1, BackendServerConf o2)
			{
				if (o1.isConnectable() && !o2.isConnectable())
				{
					return -1;
				} else if (o2.isConnectable() && !o1.isConnectable())
				{
					return 1;
				} else if (!o2.isConnectable() && !o1.isConnectable())
				{
					return 0;
				}

				float s1 = (o1.getStat().getReceivedBytes() + o1.getStat().getSentBytes()) / o1.getWeight();
				float s2 = (o2.getStat().getReceivedBytes() + o2.getStat().getSentBytes()) / o2.getWeight();
				return (int) (s1 - s2);
			}
		});
		for (BackendServerConf server : servers)
		{
			//			boolean isConnectable = NetUtils.isConnectable(server.getIp(), server.getPort());
			if (server.isConnectable())
			{
				return server;
			}
		}
		throw new RuntimeException("no server for use");

		//		int index = assignServerIndex(frontip, servers.length);
		//
		//		BackendServerConf server = servers[index];
		//		BackendServerConf ret = null;
		//		for (int i = 0; i < servers.length; i++)
		//		{
		//			//			boolean isConnectable = NetUtils.isConnectable(server.getIp(), server.getPort());
		//			if (server.isConnectable())
		//			{
		//				ret = server;
		//				break;
		//			} else
		//			{
		//				index = next(index, servers.length);
		//				server = servers[index];
		//				continue;
		//			}
		//		}
		//
		//		if (ret != null)
		//		{
		//			return ret;
		//		} else
		//		{
		//			throw new RuntimeException("no server for user");
		//		}
	}

	/**
	 * 
	 * @param frontip 形如:"192.168.0.23"
	 * @param count 整数范围(1-n)
	 * @return
	 */
	private static int assignServerIndex(String frontip, int count)
	{
		int hashCode = frontip.hashCode();
		return Math.abs(hashCode % count);
	}

	/**
	 * 
	 * @param currIndex
	 * @param count
	 * @return
	 */
	private static int next(int currIndex, int count)
	{
		int _currIndex = currIndex + 1;
		if (_currIndex > (count - 1))
		{
			_currIndex = 0;
		}
		return _currIndex;
	}

	/**
	 * 
	 */
	public Mapping()
	{

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		BackendServerConf b1 = new BackendServerConf();
		BackendServerConf b2 = new BackendServerConf();
		BackendServerConf b3 = new BackendServerConf();
		BackendServerConf b4 = new BackendServerConf();

		b1.setIp("1");
		b2.setIp("2");
		b3.setIp("3");
		b4.setIp("4");

		b1.setWeight(1);
		b2.setWeight(9);
		b3.setWeight(5);
		b4.setWeight(8);

		b1.getStat().setReceivedBytes(1000);
		b2.getStat().setReceivedBytes(1000);
		b3.getStat().setReceivedBytes(1000);
		b4.getStat().setReceivedBytes(1000);
		
		b4.setConnectable(false);
		b2.setConnectable(false);

		BackendServerConf[] servers = new BackendServerConf[] { b1, b2, b3, b4 };

		Arrays.sort(servers, new Comparator<BackendServerConf>()
		{
			@Override
			public int compare(BackendServerConf o1, BackendServerConf o2)
			{
				if (o1.isConnectable() && !o2.isConnectable())
				{
					return -1;
				} else if (o2.isConnectable() && !o1.isConnectable())
				{
					return 1;
				} else if (!o2.isConnectable() && !o1.isConnectable())
				{
					return 0;
				}

				float s1 = (o1.getStat().getReceivedBytes() + o1.getStat().getSentBytes()) / o1.getWeight();
				float s2 = (o2.getStat().getReceivedBytes() + o2.getStat().getSentBytes()) / o2.getWeight();
				return (int) (s1 - s2);
			}
		});

		int d = assignServerIndex("125.23.23.23", 5);
		System.out.println("125.23.23.23".hashCode());

		for (int i = 1; i < 1000; i++)
		{
			d = assignServerIndex("125.23.23.23", i);
			System.out.println(d);
		}
	}
}