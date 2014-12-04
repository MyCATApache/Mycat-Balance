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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.intf.DecoderIntf.DecodeException;
import com.talent.nio.communicate.intf.DecoderIntf.PacketWithMeta;
import com.talent.nio.debug.DebugUtils;
import com.talent.nio.utils.SystemTimer;
import com.talent.platform.threadpool.AbstractQueueRunnable;
import com.talent.platform.threadpool.SynThreadPoolExecutor;
import com.talent.platform.threadpool.intf.SynRunnableIntf;
import com.talent.platform.threadpool.monitor.ThreadPoolMonitor;

/**
 * 接收socket消息时，需要将消息组包。本类就是处理组包操作的。
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class DecodeRunnable extends AbstractQueueRunnable<ByteBuf>
{
    private static final Logger log = LoggerFactory.getLogger(DecodeRunnable.class);

    private ChannelContext channelContext = null;

    /**
     * 当前的字节数据
     */
    private ByteBuf lastDatas = null;

    /**
     * 计数器，用来统计总共有多少条消息被处理掉了
     */
    private static AtomicLong allProcessedMsgCount = new AtomicLong();

    /**
     * 
     //
     */
    // private static BlockingQueue<Runnable> runnableQueue = null;

    private static SynThreadPoolExecutor<SynRunnableIntf> threadExecutor = null;

    /**
     * 初始化业务处理线程池
     */
    public static void init(SynThreadPoolExecutor<SynRunnableIntf> synThreadPoolExecutor)
    {
        threadExecutor = synThreadPoolExecutor;

        ThreadPoolMonitor.getInstance().register(threadExecutor);
    }

    /**
     * 
     */
    public DecodeRunnable(ChannelContext channelContext)
    {
        this.channelContext = channelContext;
        setRunnableName(DecodeRunnable.class.getSimpleName() + "[" + channelContext.getId() + "]");
    }

    /**
     * 添加要组包的消息
     * 
     * @param datas
     */
    public void addMsg(ByteBuf datas)
    {
        if (DebugUtils.isNeedDebug(channelContext))
        {
            log.error("com.talent.nio.communicate.receive.DecodeRunnable.addMsg(byte[]):" + ArrayUtils.toString(datas));
            try
            {
                log.error("com.talent.nio.communicate.receive.DecodeRunnable.addMsg(byte[]):" + new String(datas.array(), "utf-8"));
            } catch (UnsupportedEncodingException e)
            {
                log.error(e.getMessage(), e);
            }
        }

        getMsgQueue().add(datas);
    }

    /**
     * 清空处理的队列消息
     */
    public void clearMsgQueue()
    {
        getMsgQueue().clear();
        lastDatas = null;
    }

    private int needLength = -1;

    @Override
    public void run()
    {
        while (getMsgQueue().size() > 0)
        {
            ByteBuf queuedatas = null;
            CompositeByteBuf datas = Unpooled.compositeBuffer();

            if (lastDatas != null)
            {
                channelContext.getStatVo().setCurrentOgnzTimestamp(SystemTimer.currentTimeMillis());
                lastDatas.readerIndex(0);
                datas.addComponents(lastDatas);
                lastDatas = null;
            }

            int count = 0;

            label_2:
            while ((queuedatas = getMsgQueue().poll()) != null)
            {
            	queuedatas = queuedatas.order(channelContext.getByteOrder());
            	
            	if (DebugUtils.isNeedDebug(channelContext))
                {
                    // long xx = 999999999999999999L;
                    log.error("queuedatas:" + ArrayUtils.toString(queuedatas));
                }
                datas.addComponents(queuedatas);
                channelContext.getStatVo().setCurrentOgnzTimestamp(SystemTimer.currentTimeMillis());
                count++;

                if (needLength != -1) // 已经解析出消息所需要的总长度
                {
                    if (datas.capacity() < needLength) // 收到的数据还不够长
                    {
//                        log.error("数据还不够长----capacity:{}, needLength:{}", datas.capacity(), needLength);
                        continue;
                    } else
                    {
//                        log.error("数据够了----capacity:{}, needLength:{}", datas.capacity(), needLength);
                        break label_2;
                    }

                } else
                // 还没有解析出消息所需要的总长度
                {
                    if (count == 50)
                    {
                        log.warn("等待组包的消息挺多的，马上提交{}个，提交后还有{}个等待组包", count, getMsgQueue().size());
                        break label_2;
                    }
                }
            }
            channelContext.getStatVo().setCurrentOgnzTimestamp(SystemTimer.currentTimeMillis());

            PacketWithMeta packetWithMeta = null;
            try
            {
                // ByteBuffer buffer = ByteBuffer.wrap(datas);
                datas.writerIndex(datas.capacity());
                datas.readerIndex(0);
                packetWithMeta = channelContext.getDecoder().decode(datas, channelContext);
                needLength = -1;
                if (packetWithMeta == null)
                { // 数据不够，组不了包，
                    lastDatas = datas;
                    lastDatas.readerIndex(0);

                    if (DebugUtils.isNeedDebug(channelContext))
                    {
                        log.error("数据不够，组不了包:{}", lastDatas);
                    }
                } else if (packetWithMeta.getPackets() == null || packetWithMeta.getPackets().size() == 0)
                {
                    // 数据不够，组不了包，
                    lastDatas = datas;
                    lastDatas.readerIndex(0);
                    needLength = packetWithMeta.getNeedLength();
                    if (DebugUtils.isNeedDebug(channelContext))
                    {
                        log.error("数据不够，组不了包，需要的长度为:{}", needLength);
                    }
                } else
                {
                    int len = packetWithMeta.getPacketLenght();
                    // lastDatas = new byte[datas.capacity() - len];
                    // System.arraycopy(datas, len, lastDatas, 0,
                    // lastDatas.length);

                    if (datas.capacity() - len > 0)
                    {

                        lastDatas = datas.copy(len, datas.capacity() - len);
                        if (DebugUtils.isNeedDebug(channelContext))
                        {
                            log.error("组包后，还剩一点数据:{}, {}", datas.capacity() - len, lastDatas);
                        }
                    } else
                    {
                        lastDatas = null;
                        if (DebugUtils.isNeedDebug(channelContext))
                        {
                            log.error("组包后，数据刚好用完:{}", lastDatas);
                        }
                    }
                    processMsgAndStat(packetWithMeta.getPackets(), len, false);
                }

            } catch (DecodeException e)
            {
                log.error(e.getMessage(), e);
                channelContext.getErrorPackageHandler().handle(channelContext.getSocketChannel(), channelContext, e.getMessage());
            }
        }

    }

    /**
     * 处理消息并且统计消息数量
     * 
     * @param packet
     * @param msgLenght
     * @param isInvalid
     *            false:正常
     */
    private void processMsgAndStat(List<Packet> packet, int msgLenght, boolean isInvalid)
    {
        try
        {
            if (DebugUtils.isNeedDebug(channelContext))
            {
                log.error("com.talent.nio.communicate.receive.DecodeRunnable.processMsgAndStat(List<Packet>, int, boolean) 来了!");
            }

            if (isInvalid)
            {
                return;
            }

            int num = processMsg(packet);
            getAllProcessedMsgCount().addAndGet(num);
            getProcessedMsgCount().addAndGet(num);
            getProcessedMsgByteCount().addAndGet(msgLenght);

            if (log.isDebugEnabled())
            {
                long allReadCount = ChannelReader.getReadCount().get();
                long myReadCount = channelContext.getStatVo().getReadCount().get();

                log.debug("all processed [" + allProcessedMsgCount.get() + "];" + channelContext.getId() + " processed ["
                        + this.getProcessedMsgCount().get() + "],waiting for process [" + this.getMsgQueue().size() + "];allReadCount["
                        + allReadCount + "];[" + channelContext.getId() + "] readCount[" + myReadCount + "]");
            }

        } catch (Exception e)
        {
            log.error("", e);
        }
    }

    /**
     * 处理消息
     * 
     * @param msgHead
     * @param bodyData
     * @return 处理的消息条数
     */
    private int processMsg(List<Packet> packets)
    {
        try
        {
            if (DebugUtils.isNeedDebug(channelContext))
            {
                log.error("packets:{}", packets);
            }

            submitTask(packets);
            return packets.size();

        } catch (Exception e)
        {
            log.error("", e);
        }

        return 0;
    }

    /**
     * 提交任务到调度池
     * 
     * @param packets
     */
    private void submitTask(List<Packet> packets)
    {

        HandlerRunnable packetHandlerRunnable = channelContext.getHandlerRunnable();// PacketHandlerRunnableFactory.getPacketHandlerRunnable(packet,

        if (DebugUtils.isNeedDebug(channelContext))
        {
            log.error("packets:{}", packets);
        }

        packetHandlerRunnable.addMsg(packets);

        threadExecutor.execute(packetHandlerRunnable);
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        byte[] bs1 = new byte[]
        { 1, 10, 11, 12 };
        byte[] bs2 = new byte[]
        { 2, 2, 2, 2 };
        byte[] bs3 = new byte[]
        { 3, 3, 3, 3 };
        byte[] bs4 = new byte[]
        { 4, 4, 4, 4 };
        byte[] bs5 = new byte[]
        { 5, 5, 5, 5 };
        byte[] bs6 = new byte[]
        { 6, 6, 6, 6 };

        ByteBuffer buffer1 = ByteBuffer.allocate(1024);
        buffer1.put(bs1);
        buffer1.flip();

        ByteBuf buf1 = Unpooled.copiedBuffer(buffer1);// .copiedBuffer(bs1);

        buffer1.put(bs3);

        ByteBuf buf2 = Unpooled.copiedBuffer(bs2);
        ByteBuf buf3 = Unpooled.copiedBuffer(bs3);
        ByteBuf buf4 = Unpooled.copiedBuffer(bs4);
        ByteBuf buf5 = Unpooled.copiedBuffer(bs5);
        ByteBuf buf6 = Unpooled.copiedBuffer(bs6);

        CompositeByteBuf cb = Unpooled.compositeBuffer();
        cb.addComponents(buf1, buf2, buf3);

        byte dd = cb.getByte(0);

        CompositeByteBuf cb2 = Unpooled.compositeBuffer();
        cb.addComponents(buf4, buf5, buf6);

        // cb.c
        // cb2.writerIndex(128 * 1024);

        cb.addComponent(cb2);

        Long number = cb2.readLong(); // causes IllegalBufferAccessException
                                      // here!

    }

    @Override
    public String getCurrentProcessor()
    {
        return this.getClass().getName();
    }

    public static AtomicLong getAllProcessedMsgCount()
    {
        return allProcessedMsgCount;
    }

    public static SynThreadPoolExecutor<SynRunnableIntf> getThreadExecutor()
    {
        return threadExecutor;
    }

    public ChannelContext getSocketChannelContext()
    {
        return channelContext;
    }

    public void setSocketChannelContext(ChannelContext channelContext)
    {
        this.channelContext = channelContext;
    }
}