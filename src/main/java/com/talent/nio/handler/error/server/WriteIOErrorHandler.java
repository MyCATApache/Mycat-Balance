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
package com.talent.nio.handler.error.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.handler.error.intf.WriteIOErrorHandlerIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class WriteIOErrorHandler implements WriteIOErrorHandlerIntf

{
    /**
     * 
     */
    private static final long serialVersionUID = 5503700166173408457L;

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(WriteIOErrorHandler.class);

    private static WriteIOErrorHandler instance = null;

    public static WriteIOErrorHandler getInstance()
    {
        if (instance == null)
        {
            instance = new WriteIOErrorHandler();
        }
        return instance;
    }

    /**
     * 
     */
    protected WriteIOErrorHandler()
    {

    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    @Override
    public void handle(SocketChannel socketChannel, IOException e, ChannelContext channelContext, String customMsg)
    {
        String customMsg1 = customMsg;
        if (customMsg1 == null || "".equals(customMsg1))
        {
            customMsg1 = "IOException occured when writing";
        }
        if (channelContext != null)
        {
            channelContext.getStatVo().getCountOfWriteException().incrementAndGet();// .addWriteExceptionTimes();
        }
        DefaultIOErrorHandler.getInstance().handle(socketChannel, e, channelContext, customMsg1);

    }

}