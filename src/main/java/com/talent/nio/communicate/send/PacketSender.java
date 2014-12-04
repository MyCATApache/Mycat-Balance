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
package com.talent.nio.communicate.send;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;
import com.talent.platform.threadpool.SynThreadPoolExecutor;
import com.talent.platform.threadpool.intf.SynRunnableIntf;
import com.talent.platform.threadpool.monitor.ThreadPoolMonitor;

/**
 * 消息包发送者
 * 
 * @author 谭耀武
 * @date 2011-12-26
 * 
 */
public class PacketSender
{
    private static final Logger log = LoggerFactory.getLogger(PacketSender.class);

    /**
     * 线程池
     */
    private static SynThreadPoolExecutor<SynRunnableIntf> queueThreadPoolExecutor = null;

    /**
     * 消息计数变量，用来统计，已经处理了多少条数据
     */
    private static AtomicLong count = new AtomicLong();

    /**
     * 初始化发送线程池及相关内容
     */
    public static void init(SynThreadPoolExecutor<SynRunnableIntf> synThreadPoolExecutor)
    {
        queueThreadPoolExecutor = synThreadPoolExecutor;
        ThreadPoolMonitor.getInstance().register(queueThreadPoolExecutor);
    }

    /**
     * 
     */
    private PacketSender()
    {

    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    /**
     * 发送消息
     * 
     * @param packet
     * @return
     */
    public static boolean send(Packet packet, ChannelContext channelContext)
    {
        String socketChannelId = channelContext.getId();
        SendRunnable socketMsgSendRunnable = channelContext.getSendRunnable();

        if (socketMsgSendRunnable == null)
        {
            log.error(socketChannelId + " socketMsgSendRunnable is null");
            return false;
        }

        if (socketMsgSendRunnable.getMsgQueue().size() > 3000000)
        {
            log.warn("queue size is " + socketMsgSendRunnable.getMsgQueue().size() + ". not allowed to add msg to queue!");
            socketMsgSendRunnable.recordFailMsg(packet, "send queue is " + socketMsgSendRunnable.getMsgQueue().size()
                    + ". not allowed to add msg to queue!");
            return false;
        } else
        {
            socketMsgSendRunnable.addMsg(packet); // 将消息添加到相应发送线程的队列中
        }

        queueThreadPoolExecutor.execute(socketMsgSendRunnable);
        log.debug("deliver msg to {}, have delivered {} items", socketMsgSendRunnable, count.incrementAndGet());
        return true;
    }

    public static SynThreadPoolExecutor<SynRunnableIntf> getThreadExecutor()
    {
        return queueThreadPoolExecutor;
    }
}