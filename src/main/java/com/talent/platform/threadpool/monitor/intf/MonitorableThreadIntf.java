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

/**
 * 可监控的线程。如果一个线程想被监控，一定要实现此接口
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public interface MonitorableThreadIntf
{
    /**
     * 获取线程调度器
     * 
     * @return
     */
    MonitorableThreadPoolExecutorIntf getExecutor();

    /**
     * 设置线程调度器
     * 
     * @param executor
     */
    void setExecutor(MonitorableThreadPoolExecutorIntf executor);

    /**
     * 获取当前的任务对象
     * 
     * @return
     */
    Runnable getRunnable();

    /**
     * 设置当前线程运行的任务对象
     * 
     * @param task
     */
    void setRunnable(Runnable task);

    /**
     * 获取线程所用的cpu时间,单位毫秒
     * 
     * @return
     */
    long getCpuTime();

    /**
     * 设置线程所用的cpu时间，单位毫秒
     * 
     * @param cpuTime
     */
    void setCpuTime(long cpuTime);

    /**
     * 线程名字
     * 
     * @return
     */
    String getName();

}