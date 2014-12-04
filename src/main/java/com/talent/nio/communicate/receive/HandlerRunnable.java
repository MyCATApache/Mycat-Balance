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
package com.talent.nio.communicate.receive;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;
import com.talent.platform.threadpool.AbstractQueueRunnable;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class HandlerRunnable extends AbstractQueueRunnable<List<Packet>>
{
    private static final Logger log = LoggerFactory.getLogger(HandlerRunnable.class);

    private DecodeRunnable parent = null;

    // private String msgType = null;

    private ChannelContext channelContext = null;

    public HandlerRunnable(ChannelContext channelContext)
    {
        this.setSocketChannelContext(channelContext);
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    @Override
    public String getCurrentProcessor()
    {
        return channelContext.getPacketHandler().getClass().getSimpleName();
    }

    @Override
    public void run()
    {
        List<Packet> packets = null;
        while ((packets = getMsgQueue().poll()) != null)
        {
            // packet = msgQueue.poll();
            // if (packet == null)
            // {
            // continue;
            // }
            try
            {
                processPacket(packets, parent.getSocketChannelContext());

            } catch (Exception e)
            {
                log.error(packets.toString());
                throw new RuntimeException(e.getMessage(), e);
            }

            this.getProcessedMsgCount().incrementAndGet();
            if (log.isDebugEnabled())
            {
                log.debug("total processed[" + DecodeRunnable.getAllProcessedMsgCount().get() + "];" + "["
                        + parent.getSocketChannelContext().getId() + "] processed[" + parent.getProcessedMsgCount().get() + "],waiting["
                        + parent.getMsgQueue().size() + "]；" + channelContext.getPacketHandler().getClass().getName() + " processed["
                        + this.getProcessedMsgCount().get() + "],waiting[" + getMsgQueue().size() + "]");
            }
        }
    }

    private int processPacket(List<Packet> packets, ChannelContext channelContext) throws Exception
    {
        int ret = 0;
        if (packets != null && packets.size() > 0)
        {

            for (Packet packet : packets)
            {
                try
                {
                    channelContext.getPacketHandler().onReceived(packet, channelContext);
                    ret++;
                } catch (Exception e)
                {
                    log.error(e.getMessage(), e);
                    return ret;
                }
            }
        }

        return ret;
    }

    /**
     * 添加要处理的消息
     * 
     * @param packet
     */
    public void addMsg(List<Packet> packets)
    {
        getMsgQueue().add(packets);
    }

    public void setParent(DecodeRunnable parent)
    {
        this.parent = parent;
    }

    public DecodeRunnable getParent()
    {
        return parent;
    }

    public ChannelContext getSocketChannelContext()
    {
        return channelContext;
    }

    public void setSocketChannelContext(ChannelContext channelContext)
    {
        this.channelContext = channelContext;
    }

    // public String getMsgType()
    // {
    // return msgType;
    // }
    //
    // public void setMsgType(String msgType)
    // {
    // this.msgType = msgType;
    // }

}