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
package com.talent.platform.threadpool.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.platform.threadpool.monitor.intf.MonitorableThreadIntf;
import com.talent.platform.threadpool.monitor.intf.MonitorableThreadPoolExecutorIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public class MonitorableThread extends Thread implements MonitorableThreadIntf
{

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(MonitorableThread.class);

    private MonitorableThreadPoolExecutorIntf executor = null;

    private Runnable runnable = null;

    private long cpuTime = 0;

    private String groupName = null;

    public MonitorableThread(Runnable target, String name)
    {
        super(target, name);
    }

    public MonitorableThread(Runnable target)
    {
        super(target);
    }

    public MonitorableThread(String name)
    {
        super(name);
    }

    public MonitorableThread(ThreadGroup group, Runnable target, String name, long stackSize)
    {
        super(group, target, name, stackSize);
    }

    public MonitorableThread(ThreadGroup group, Runnable target, String name)
    {
        super(group, target, name);
    }

    public MonitorableThread(ThreadGroup group, Runnable target)
    {
        super(group, target);
    }

    public MonitorableThread(ThreadGroup group, String name)
    {
        super(group, name);
    }

    /**
     * 
     */
    public MonitorableThread()
    {
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    @Override
    public MonitorableThreadPoolExecutorIntf getExecutor()
    {
        return this.executor;
    }

    public void setExecutor(MonitorableThreadPoolExecutorIntf executor)
    {
        this.executor = executor;
    }

    @Override
    public void setRunnable(Runnable runnable)
    {
        this.runnable = runnable;
    }

    @Override
    public Runnable getRunnable()
    {
        return runnable;
    }

    public long getCpuTime()
    {
        return cpuTime;
    }

    public void setCpuTime(long cpuTime)
    {
        this.cpuTime = cpuTime;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }
}