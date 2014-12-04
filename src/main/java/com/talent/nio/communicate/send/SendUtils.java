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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.handler.PacketHandlerProxy;
import com.talent.nio.communicate.handler.intf.BlockSupportPacketHandler;
import com.talent.nio.communicate.handler.intf.MessageChangeListener;
import com.talent.nio.communicate.util.StatUtils;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class SendUtils
{
    private static final Logger log = LoggerFactory.getLogger(SendUtils.class);

    /**
     * key: ChannelContext; value:消息量
     */
    private static Map<ChannelContext, AtomicLong> mapOfSocketChannelContextAndMsgCount = new HashMap<ChannelContext, AtomicLong>();

    /**
     * key: ChannelContext; value:任务被提交的次数
     */
    private static Map<ChannelContext, AtomicLong> mapOfSocketChannelContextAndSubmitCount = new HashMap<ChannelContext, AtomicLong>();

    /**
     * key: ChannelContext; value:消息量(字节)
     */
    private static Map<ChannelContext, AtomicLong> mapOfSocketChannelContextAndMsgSize = new HashMap<ChannelContext, AtomicLong>();

    /**
     * 
     * @author tanyaowu
     * @param ChannelContext
     * @return
     */
    public static void resumeCount(ChannelContext channelContext)
    {

        StatUtils.resumeCount(channelContext.getSendRunnable(), mapOfSocketChannelContextAndMsgCount.get(channelContext),
                mapOfSocketChannelContextAndSubmitCount.get(channelContext), mapOfSocketChannelContextAndMsgSize.get(channelContext));

    }

    /**
     * 从缓存中删除channelContext对应的记录
     * 
     * @param channelContext
     * @return
     */
    public static void removeStat(ChannelContext channelContext)
    {
        synchronized (mapOfSocketChannelContextAndMsgCount)
        {
            mapOfSocketChannelContextAndMsgCount.remove(channelContext);
            mapOfSocketChannelContextAndSubmitCount.remove(channelContext);
            mapOfSocketChannelContextAndMsgSize.remove(channelContext);
        }
    }

    /**
     * 保存统计量
     * 
     * @param channelContext
     * @param msgCount
     * @param submitCount
     * @param msgSize
     */
    public static void recordStat(ChannelContext channelContext, AtomicLong msgCount, AtomicLong submitCount, AtomicLong msgSize)
    {
        synchronized (channelContext)
        {
            mapOfSocketChannelContextAndMsgCount.put(channelContext, msgCount);
            mapOfSocketChannelContextAndSubmitCount.put(channelContext, submitCount);
            mapOfSocketChannelContextAndMsgSize.put(channelContext, msgSize);
        }
    }

    public static AtomicLong getRecordMsgCount(ChannelContext channelContext)
    {
        return mapOfSocketChannelContextAndMsgCount.get(channelContext);
    }

    public static AtomicLong getRecordSubmitCount(ChannelContext channelContext)
    {
        return mapOfSocketChannelContextAndSubmitCount.get(channelContext);
    }

    public static AtomicLong getRecordMsgSize(ChannelContext channelContext)
    {
        return mapOfSocketChannelContextAndMsgSize.get(channelContext);
    }

    /**
     * 发送数据前,先检查链路的一些情况
     * 
     * @param packet
     * @param channelContext
     * @param isNeedAppOn
     * @return
     * @throws Exception
     */
    private static boolean checkBeforeSend(Packet packet, ChannelContext channelContext, boolean isNeedAppOn) throws Exception
    {
        if (packet == null)
        {
            log.error("{} is null", Packet.class.getSimpleName());
        }

        if (channelContext == null)
        {
            log.error("{} is null", ChannelContext.class.getSimpleName());
            throw new Exception(ChannelContext.class.getSimpleName() + " is null");
        }

        if (isNeedAppOn && (!channelContext.isAppOn()))
        {
            log.error("isNeedAppOn: " + isNeedAppOn + ", link is off, the state is " + channelContext.getConnectionState());
            throw new Exception("isNeedAppOn: " + isNeedAppOn + ", link is off, the state is " + channelContext.getConnectionState());
        }
        return true;
    }

    @SuppressWarnings("finally")
    public static Packet synSend(Packet packet, boolean isNeedAppOn, long timeout, ChannelContext channelContext) throws Exception
    {
        try
        {
            packet.setSyn(true);
            send(packet, isNeedAppOn, channelContext);
            synchronized (packet)
            {
                try
                {
                    log.debug("waiting syn message...");
                    packet.wait(timeout);
                } catch (InterruptedException e)
                {
                    log.error("", e);
                }
            }
        } catch (Exception e)
        {
            log.error("", e);
        } finally
        {
            PacketHandlerProxy packetHandlerProxy = (PacketHandlerProxy) channelContext.getPacketHandler();
            Packet receivedPacket = packetHandlerProxy.removeSynSeqNo(packet.getSeqNo());

            if (receivedPacket == null)
            {
                log.error("receivedPacket is null");
                return null;
            }

            if (receivedPacket != packet)
            {
                log.debug("successful for synSending, packet is {}", packet);
                return receivedPacket;
            } else
            {
                log.error("timeout for synSending, packet is {}", packet);
                return null;
            }
        }
    }

    public static boolean asySend(Packet packet, boolean isNeedAppOn, ChannelContext channelContext) throws Exception
    {
        packet.setSyn(false);
        return send(packet, isNeedAppOn, channelContext);
    }

    private static boolean send(Packet packet, boolean isNeedAppOn, ChannelContext channelContext) throws Exception
    {
        if (!isNeedAppOn)
        {
            return PacketSender.send(packet, channelContext);
        } else
        {
            if (checkBeforeSend(packet, channelContext, isNeedAppOn))
            {
                return PacketSender.send(packet, channelContext);
            } else
            {
                return false;
            }
        }
    }

    @SuppressWarnings("finally")
    public static List<Packet> blockSend(Packet packet, MessageChangeListener msgChangeListener, boolean isNeedAppOn, long timeout,
            ChannelContext channelContext) throws Exception
    {
        final BlockSupportPacketHandler packetHandler = (BlockSupportPacketHandler) channelContext.getPacketHandler();

        try
        {
            packetHandler.setMsgChangeListener(msgChangeListener);

            synchronized (packetHandler)
            {
                int count = 0;
                while (packetHandler.isRunning())
                {
                    count++;
                    if (count * 500 >= timeout)
                    {
                        log.warn("wait timeout");
                        break;
                    }
                    log.debug("packetHandler.isRunning() = true");
                    Thread.sleep(500);
                }
                packetHandler.setRunning(true);
                send(packet, isNeedAppOn, channelContext);
                synchronized (packet)
                {
                    try
                    {
                        log.debug("waiting blocking message");
                        packet.wait(timeout);
                    } catch (Exception e)
                    {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e)
        {
            throw e;
        } finally
        {
            new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(1000 * 5);
                    } catch (InterruptedException e)
                    {
                        log.error(e.getMessage(), e);
                    }
                    BlockSupportPacketHandler.removeKey(packetHandler);
                    packetHandler.setRunning(false);
                }

            }).start();

            Packet receivedPacket = BlockSupportPacketHandler.getKey(packetHandler);

            if (receivedPacket == null)
            {
                log.error("receivedPacket is null");
                throw new Exception("receivedPacket is null");
            }

            if (receivedPacket != packet)
            {
                log.info("successful for blockSending, packet is {}", packet);
                return packetHandler.getReceivedPackets();
            } else
            {
                log.error("timeout for blockSending, packet is " + packet);
                throw new Exception("timeout for blockSending, packet is " + packet);
            }
        }
    }

}