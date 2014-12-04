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
package com.talent.nio.handler.error.client;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.api.Nio;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.util.NioUtils;
import com.talent.nio.handler.error.intf.ErrorPackageHandlerIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class DefaultErrorPackageHandler implements ErrorPackageHandlerIntf
{
    private static final int MAX_COUNT = 1;

    private static final Logger log = LoggerFactory.getLogger(DefaultErrorPackageHandler.class);

    private static DefaultErrorPackageHandler instance = new DefaultErrorPackageHandler();

    public static DefaultErrorPackageHandler getInstance()
    {
        return instance;
    }

    private DefaultErrorPackageHandler()
    {

    }

    @Override
    public int handle(SocketChannel socketChannel, ChannelContext channelContext, String errorReason)
    {
        channelContext.getStatVo().getCountOfErrorPackage().incrementAndGet();
        log.error("[" + "] received error package, reason is " + errorReason);

        if (channelContext.getStatVo().getCountOfErrorPackage().get() >= MAX_COUNT)
        {
            channelContext.getStatVo().getCountOfErrorPackage().set(0);
            Nio.getInstance().disconnect(channelContext, "received an error package");
        }
        return (int) channelContext.getStatVo().getCountOfErrorPackage().get();
    }
}