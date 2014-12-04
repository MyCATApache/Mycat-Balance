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
package com.talent.nio.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.ChannelContext;

/**
 * 
 * @filename: com.talent.nio.debug.DebugUtils
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013年9月25日 下午3:40:30
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
 *         <td>2013年9月25日</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class DebugUtils
{
    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(DebugUtils.class);
    
    private static boolean canDebug = false;

    public static String debugIp = null;//"*"
    public static int debugPort = 0;

    /**
     * 
     * @param channelContext
     * @return
     */
    public static boolean isNeedDebug(ChannelContext channelContext)
    {
        if (!canDebug || debugIp == null)
        {
            return false;
        }
        
        if ("*".equals(debugIp))
        {
            return true;
        }

        if (channelContext.getRemoteNode().getIp().equals(DebugUtils.debugIp))
        {
            if (channelContext.getRemoteNode().getPort() == (DebugUtils.debugPort))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     */
    public DebugUtils()
    {

    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
    	
    }
}