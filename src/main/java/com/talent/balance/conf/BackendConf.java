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
package com.talent.balance.conf;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.common.JsonWrap;

/**
 * 
 * @filename:	 com.talent.balance.conf.ClientConf
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月20日 下午4:39:31
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月20日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class BackendConf
{
	private static Logger log = LoggerFactory.getLogger(BackendConf.class);

	private static BackendConf instance = null;

	public static BackendConf getInstance()
	{
		if (instance == null)
		{
			synchronized (log)
			{
				if (instance == null)
				{
					try
					{
						instance = JsonWrap.toBean(FileUtils.readFileToString(new File("./conf/backend-conf.json"), "utf-8"), BackendConf.class);
					} catch (Exception e)
					{
						log.error("", e);
						throw new RuntimeException(e);
					}
				}
			}
		}

		return instance;
	}

	private BackendServerConf[] servers = null;
	
	private String protocol;
	
	private byte byteOrder;
	
	private int channelCacheSize;
	
	

	/**
	 * 
	 */
	public BackendConf()
	{

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		BackendConf ii = BackendConf.getInstance();
		System.out.println();

	}

	public BackendServerConf[] getServers()
	{
		return servers;
	}

	public void setServers(BackendServerConf[] servers)
	{
		this.servers = servers;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}

	public int getChannelCacheSize()
	{
		return channelCacheSize;
	}

	public void setChannelCacheSize(int channelCacheSize)
	{
		this.channelCacheSize = channelCacheSize;
	}

	public byte getByteOrder()
	{
		return byteOrder;
	}

	public void setByteOrder(byte byteOrder)
	{
		this.byteOrder = byteOrder;
	}

	

	
}