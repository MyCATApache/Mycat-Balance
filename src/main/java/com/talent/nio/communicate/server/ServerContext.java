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
package com.talent.nio.communicate.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.handler.intf.PacketHandlerIntf;
import com.talent.nio.communicate.intf.DecoderIntf;

/**
 * 
 * @filename: com.talent.nio.communicate.server.ServerContext
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-9-16 下午4:43:45
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
public class ServerContext
{
    private static Logger log = LoggerFactory.getLogger(ServerContext.class);

    private String bindIp;
    private int bindPort;

    private String protocol;
    private Class<? extends DecoderIntf> packetOgnzerClass;
    private Class<? extends PacketHandlerIntf> packetHandlerClass;
    
    private ChannelContextCompleter channelContextCompleter = null;

    public ServerContext(String bindIp, int bindPort, String protocol, Class<? extends DecoderIntf> packetOgnzerClass,
            Class<? extends PacketHandlerIntf> packetHandlerClass, ChannelContextCompleter channelContextCompleter)
    {
        super();
        this.bindIp = bindIp;
        this.bindPort = bindPort;
        this.protocol = protocol;
        this.packetOgnzerClass = packetOgnzerClass;
        this.packetHandlerClass = packetHandlerClass;
        this.setChannelContextCompleter(channelContextCompleter);
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
    }

    public String getBindIp()
    {
        return bindIp;
    }

    public void setBindIp(String bindIp)
    {
        this.bindIp = bindIp;
    }

    public int getBindPort()
    {
        return bindPort;
    }

    public void setBindPort(int bindPort)
    {
        this.bindPort = bindPort;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public Class<? extends DecoderIntf> getPacketOgnzerClass()
    {
        return packetOgnzerClass;
    }

    public void setPacketOgnzerClass(Class<DecoderIntf> packetOgnzerClass)
    {
        this.packetOgnzerClass = packetOgnzerClass;
    }

    public Class<? extends PacketHandlerIntf> getPacketHandlerClass()
    {
        return packetHandlerClass;
    }

    public void setPacketHandlerClass(Class<PacketHandlerIntf> packetHandlerClass)
    {
        this.packetHandlerClass = packetHandlerClass;
    }

	public ChannelContextCompleter getChannelContextCompleter()
	{
		return channelContextCompleter;
	}

	public void setChannelContextCompleter(ChannelContextCompleter channelContextCompleter)
	{
		this.channelContextCompleter = channelContextCompleter;
	}
}