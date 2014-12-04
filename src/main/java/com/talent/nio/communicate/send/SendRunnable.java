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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.monitor.vo.PacketVo;
import com.talent.nio.communicate.monitor.vo.StatVo;
import com.talent.nio.debug.DebugUtils;
import com.talent.nio.utils.SystemTimer;
import com.talent.platform.threadpool.AbstractQueueRunnable;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class SendRunnable extends AbstractQueueRunnable<Packet>
{
    /**
     * 
     */
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 4228217817295225588L;

    private static final Logger log = LoggerFactory.getLogger(SendRunnable.class);

    /**
     * @param args
     */
    public static void main(String[] args)
    {
    }

    private ConcurrentLinkedQueue<PacketVo> sendFailQueue = null;

    private ChannelContext channelContext = null;

    /**
     * 
     * @param socketChannelId
     */
    public SendRunnable(ChannelContext channelContext)
    {
        this.channelContext = channelContext;
        super.setRunnableName(SendRunnable.class.getSimpleName() + " [" + channelContext.getId() + "]");
    }

    /**
     * 添加要处理的消息
     * 
     * @param packet
     */
    public void addMsg(Packet packet)
    {
        getMsgQueue().add(packet);
    }

    /**
     * 清空消息队列
     */
    public void clearMsgQueue()
    {
        getMsgQueue().clear();
    }

    @Override
    public String getCurrentProcessor()
    {
        return this.getClass().getName();
    }

    /**
     * 
     * @param isForceCreate
     *            true:有条件地强制创建对象,false:直接返回
     * @return
     */
    public ConcurrentLinkedQueue<PacketVo> getSendFailQueue(boolean isForceCreate)
    {
        if (!isForceCreate)
        {
            return sendFailQueue;
        }

        if (!this.channelContext.isNeedRecordSendFailMsg())
        {
            return null;
        } else if (sendFailQueue == null)
        {
            sendFailQueue = new ConcurrentLinkedQueue<PacketVo>();
        }
        return sendFailQueue;
    }

    public void recordFailMsg(Packet packet, String failReason)
    {
        if (channelContext.isNeedRecordSendFailMsg())
        {
            ConcurrentLinkedQueue<PacketVo> sendFailQueue = getSendFailQueue(true);
            if (sendFailQueue != null)
            {
                PacketVo packetPojo = PacketVo.createPacketVo(packet, SystemTimer.currentTimeMillis(), failReason);
                if (sendFailQueue.size() >= channelContext.getCountOfRecordSendFail())
                {
                    sendFailQueue.poll();
                }
                sendFailQueue.add(packetPojo);
            }
        }

    }

    /**
     * 
     */
    @Override
    public void run()
    {
        Packet packet = null;
        try
        {
            while ((packet = getMsgQueue().poll()) != null)
            {
                sendPacket(packet);
            }
        } catch (IOException e)
        {
            recordFailMsg(packet, e.getMessage());
            channelContext.getWriteIOErrorHandler().handle(channelContext.getSocketChannel(), e, channelContext,
                    "IOException occured when writing");
        }
    }

    private int send(SocketChannel socketChannel, ByteBuffer dataBuffer) throws IOException
    {
        int sendSize = socketChannel.write(dataBuffer);

        if (sendSize > 0)
        {
            channelContext.getStatVo().setCurrentSendTime(SystemTimer.currentTimeMillis());
            channelContext.getStatVo().setSentBytes(sendSize + channelContext.getStatVo().getSentBytes());
            StatVo.getAllSentBytes().addAndGet(sendSize);

            getProcessedMsgByteCount().addAndGet(sendSize);
        }

        return sendSize;
    }

    /**
     * 
     * @param data
     *            要发送的数据
     * @param socketChannel
     *            发往的通道
     * @return 数据发送成功：true；失败：false
     * @throws IOException
     */
    private boolean sendData(byte[] data, SocketChannel socketChannel) throws IOException
    {
        try
        {
            ByteBuffer dataBuffer = ByteBuffer.wrap(data);

            long sendSize = send(socketChannel, dataBuffer);
            long allSendSize = sendSize;
            long starttime = SystemTimer.currentTimeMillis();

            while (dataBuffer.hasRemaining() && socketChannel.isOpen()) // 还有数据没写干净，一般是网络不好导致，也可能是发送和接收量太大。
            {
                sendSize = send(socketChannel, dataBuffer);

                if (sendSize == 0)
                {
                    Thread.sleep(5);
                } else
                {
                    allSendSize += sendSize;
                }

                if (DebugUtils.isNeedDebug(channelContext))
                {
                    try
                    {
                        long endtime = SystemTimer.currentTimeMillis();
                        long costtime = (endtime - starttime);
                        double speed = -1;
                        if (costtime > 0)
                        {
                            speed = allSendSize / costtime;
                        }

                        log.error("{} bytes was sent, {}/{}({}%), cost time: {} ms, {}bytes/ms,{}", sendSize, allSendSize, data.length,
                                (allSendSize * 100) / data.length, costtime, speed, channelContext);
                    } catch (Exception e)
                    {
                        log.error(e.getLocalizedMessage(), e);
                    }
                }
            }
            StatVo.getAllSentMsgCount().incrementAndGet();
            this.getProcessedMsgCount().incrementAndGet();
            long endtime = SystemTimer.currentTimeMillis();

            if (DebugUtils.isNeedDebug(channelContext))
            {
                try
                {
                    long costtime = (endtime - starttime);
                    double speed = -1;
                    if (costtime > 0)
                    {
                        speed = allSendSize / costtime;
                    }
                    log.error("cost time: {} ms, {}bytes/ms, {}", costtime, speed, channelContext);

                    log.error("ok sent to " + channelContext + ",total num[" + StatVo.getAllSentMsgCount().get() + "],num to this ["
                            + getProcessedMsgCount().get() + "],waiting for send to this [" + getMsgQueue().size() + "]");
                } catch (Exception e)
                {
                    log.error(e.getLocalizedMessage(), e);
                }
            }

            return true;
        } catch (InterruptedException e)
        {
            return false;
        }
    }

    public void sendPacket(Packet packet) throws IOException
    {
        if (packet == null)
        {
            log.error("Packet is null，please check synchronize");
            return;
        }

        if (channelContext.getId() == null)
        {
            log.error("target socketChannelId is null!");
            return;
        }

        byte[] bytes = null;
        try
        {
            bytes = channelContext.getPacketHandler().onSend(packet, channelContext);
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
            return;
        }

        if (log.isDebugEnabled())
        {
            log.debug("send:{}{}", Arrays.toString(bytes), packet);
        }

        if (bytes != null)
        {
            sendData(bytes, channelContext.getSocketChannel());

        } else
        {
            log.error("bytes is null");
        }
    }

    public void setRouteInfo(ChannelContext channelContext)
    {
        this.channelContext = channelContext;
    }

    public void setSendFailQueue(ConcurrentLinkedQueue<PacketVo> sendFailQueue)
    {
        this.sendFailQueue = sendFailQueue;
    }
}