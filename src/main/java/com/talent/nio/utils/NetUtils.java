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
package com.talent.nio.utils;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @filename:	 com.talent.nio.utils.NetUtils
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月23日 上午11:30:55
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
public class NetUtils
{
	private static Logger log = LoggerFactory.getLogger(NetUtils.class);

	/**
	 * 
	 */
	public NetUtils()
	{
	}

	/**
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public static boolean isConnectable(String ip, int port)
	{
		try
		{
			TelnetClient client = new TelnetClient();
			client.connect(ip, port);
			return true;

		} catch (Exception e)
		{
			return false;
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			long start1 = System.currentTimeMillis();
			boolean isConnect = isConnectable("127.0.0.1", 9898);

			long end1 = System.currentTimeMillis();
			System.out.println(isConnect + ":" + (end1 - start1) + "");

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}