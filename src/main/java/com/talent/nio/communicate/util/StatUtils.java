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
package com.talent.nio.communicate.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.ChannelContext;
import com.talent.platform.threadpool.AbstractQueueRunnable;

/**
 * 
 * @filename: com.talent.nio.communicate.util.StatUtils
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-6-6 上午10:17:27
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
 *         <td>2013-6-6</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class StatUtils
{
    private static Logger log = LoggerFactory.getLogger(StatUtils.class);

    /**
     * key:socketChannelId; value:消息量(条)
     */
    public static Map<ChannelContext, AtomicLong> mapOfsocketChannelIdAndMsgCount = new HashMap<ChannelContext, AtomicLong>();

    /**
     * key:socketChannelId; value:消息量(字节)
     */
    public static Map<ChannelContext, AtomicLong> mapOfsocketChannelIdAndMsgSize = new HashMap<ChannelContext, AtomicLong>();

    /**
     * key:socketChannelId; value:任务被提交的次数
     */
    public static Map<ChannelContext, AtomicLong> mapOfsocketChannelIdAndSubmitCount = new HashMap<ChannelContext, AtomicLong>();

    /**
     * 
     */
    public StatUtils()
    {

    }

    /**
     * 从缓存中删除socketChannelId对应的记录
     * 
     * @author tanyaowu
     * @param channelContext
     * @return
     */
    public static void removeStat(ChannelContext channelContext)
    {
        synchronized (mapOfsocketChannelIdAndMsgCount)
        {
            mapOfsocketChannelIdAndMsgCount.remove(channelContext);
            mapOfsocketChannelIdAndSubmitCount.remove(channelContext);
            mapOfsocketChannelIdAndMsgSize.remove(channelContext);
        }
    }

    /**
     * 记录统计量
     * 
     * @author tanyaowu
     * @param socketChannelId
     * @param msgCount
     * @param submitCount
     * @param msgSize
     */
    public static void recordStat(ChannelContext channelContext, AtomicLong msgCount, AtomicLong submitCount, AtomicLong msgSize)
    {
        synchronized (mapOfsocketChannelIdAndMsgCount)
        {
            mapOfsocketChannelIdAndMsgCount.put(channelContext, msgCount);
            mapOfsocketChannelIdAndSubmitCount.put(channelContext, submitCount);
            mapOfsocketChannelIdAndMsgSize.put(channelContext, msgSize);
        }
    }

    /**
     * 恢复消息统计数和提交次数统计数
     * 
     * @param queueRunnable
     * @param msgCount
     * @param submitCount
     * @param msgSize
     */
    public static void resumeCount(AbstractQueueRunnable<?> queueRunnable, AtomicLong msgCount, AtomicLong submitCount, AtomicLong msgSize)
    {
        if (msgCount != null)
        {
            queueRunnable.setProcessedMsgCount(msgCount);
        }
        if (submitCount != null)
        {
            queueRunnable.setSubmitCount(submitCount);
        }
        if (msgSize != null)
        {
            queueRunnable.setProcessedMsgByteCount(msgSize);
        }
    }

    public static AtomicLong getRecordMsgCount(String socketChannelId)
    {
        return mapOfsocketChannelIdAndMsgCount.get(socketChannelId);
    }

    public static AtomicLong getRecordSubmitCount(String socketChannelId)
    {
        return mapOfsocketChannelIdAndSubmitCount.get(socketChannelId);
    }

    public static AtomicLong getRecordMsgSize(String socketChannelId)
    {
        return mapOfsocketChannelIdAndMsgSize.get(socketChannelId);
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }
}