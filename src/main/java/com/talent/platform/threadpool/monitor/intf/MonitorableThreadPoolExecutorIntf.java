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
package com.talent.platform.threadpool.monitor.intf;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * 可监控的线程池调度器
 * 
 * @author 谭耀武
 * @date 2011-12-26
 * 
 */
public interface MonitorableThreadPoolExecutorIntf extends Executor, Comparable<Object>
{
    /**
     * 获取常活线程数
     * 
     * @return
     */
    public int getCorePoolSize();

    /**
     * 获取允许最大活动线程数
     * 
     * @return
     */
    public int getMaximumPoolSize();

    /**
     * 获取当前存活的线程数
     * 
     * @return
     */
    public int getPoolSize();

    /**
     * 当前正在执行任务的线程数
     * 
     * @return
     */
    public int getActiveCount();

    /**
     * 程序运行过程中，池中存活线程数的最大值
     * 
     * @return
     */
    public int getLargestPoolSize();

    /**
     * 已经完成的任务数
     * 
     * @return
     */
    public long getCompletedTaskCount();

    /**
     * 获取存放任务对象的队列
     * 
     * @return
     */
    public BlockingQueue<Runnable> getQueue();

    /**
     * 线程池的名字
     * 
     * @return
     */
    public String getName();

}