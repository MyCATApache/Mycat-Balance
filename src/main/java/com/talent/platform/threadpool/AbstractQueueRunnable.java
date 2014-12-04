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
package com.talent.platform.threadpool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.talent.platform.threadpool.intf.QueueRunnableIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-1-4
 *
 * @param <T> 队列中存的数据类型
 */
public abstract class AbstractQueueRunnable<T> extends AbstractSynRunnable implements QueueRunnableIntf<T>
{
    public AbstractQueueRunnable()
    {
        runnableName = this.getClass().getSimpleName();
    }

    /**
     * 本任务已经被提交的执行次数
     */
    private AtomicLong submitCount = new AtomicLong();

    /**
     * 本任务处理过的消息条数(单位：条)
     */
    private AtomicLong processedMsgCount = new AtomicLong();

    /**
     * 本任务处理的消息量(单位：字节)
     */
    private AtomicLong processedMsgByteCount = new AtomicLong();

    private ConcurrentLinkedQueue<T> msgQueue = new ConcurrentLinkedQueue<T>();

    /**
     * 本任务名字
     */
    private String runnableName = null;

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    @Override
    public AtomicLong getSubmitCount()
    {
        return submitCount;
    }

    public void setRunnableName(String runnableName)
    {
        this.runnableName = runnableName;
    }

    @Override
    public String getRunnableName()
    {
        return runnableName;
    }

    @Override
    public AtomicLong getProcessedMsgCount()
    {
        return processedMsgCount;
    }

    public void setSubmitCount(AtomicLong submitCount)
    {
        this.submitCount = submitCount;
    }

    public void setProcessedMsgCount(AtomicLong executeCount)
    {
        this.processedMsgCount = executeCount;
    }

    public AtomicLong getProcessedMsgByteCount()
    {
        return processedMsgByteCount;
    }

    public void setProcessedMsgByteCount(AtomicLong processedMsgByteCount)
    {
        this.processedMsgByteCount = processedMsgByteCount;
    }

    @Override
    public String getCurrentProcessor()
    {
        return this.getClass().getSimpleName();
    }

    @Override
    public ConcurrentLinkedQueue<T> getMsgQueue()
    {
        return msgQueue;
    }

    public void setMsgQueue(ConcurrentLinkedQueue<T> msgQueue)
    {
        this.msgQueue = msgQueue;
    }

}