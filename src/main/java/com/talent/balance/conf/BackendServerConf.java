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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.stat.Stat;

/**
 * 
 * @filename:	 com.talent.balance.conf.ServerVo
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月21日 下午3:50:22
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月21日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class BackendServerConf
{
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(ip);
		builder.append(":");
		builder.append(port);
		return builder.toString();
	}

	private static Logger log = LoggerFactory.getLogger(BackendServerConf.class);

	private String ip;
	private int port;
	private int weight = 1;
	private Map<String, String> props = null;
	
	//
	private boolean isConnectable = true;
	private Stat stat = new Stat();

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getWeight()
	{
		return weight;
	}

	public void setWeight(int weight)
	{
		this.weight = weight;
	}

	/**
	 * 
	 */
	public BackendServerConf()
	{

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	public Stat getStat()
	{
		return stat;
	}

	public void setStat(Stat stat)
	{
		this.stat = stat;
	}

	public boolean isConnectable()
	{
		return isConnectable;
	}

	public void setConnectable(boolean isConnectable)
	{
		this.isConnectable = isConnectable;
	}

	public Map<String, String> getProps()
	{
		return props;
	}

	public void setProps(Map<String, String> props)
	{
		this.props = props;
	}
}