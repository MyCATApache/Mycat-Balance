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
package com.talent.platform.threadpool.monitor.vo;

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
public class MonitorableThreadVo implements MonitorableThreadIntf, java.io.Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -148535362234350284L;

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(MonitorableThreadVo.class);

    private MonitorableThreadPoolExecutorVo executor = null;

    private MonitorableRunnableVo task = null;

    private long cpuTime = 0;

    private String name = null;

    /**
     * 
     */
    public MonitorableThreadVo()
    {
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    @Override
    public MonitorableThreadPoolExecutorVo getExecutor()
    {
        return this.executor;
    }

    public void setExecutor(MonitorableThreadPoolExecutorVo executor)
    {
        this.executor = executor;
    }

    @Override
    public void setRunnable(Runnable task)
    {
        this.task = (MonitorableRunnableVo) task;
    }

    @Override
    public MonitorableRunnableVo getRunnable()
    {
        return task;
    }

    public long getCpuTime()
    {
        return cpuTime;
    }

    public void setCpuTime(long cpuTime)
    {
        this.cpuTime = cpuTime;
    }

    //    public MonitorableThreadVo fillValueWithOther(MonitorableThreadIntf other)
    //    {
    //        this.setCpuTime(other.getCpuTime());
    //        this.setName(other.getName());
    //        MonitorableThreadPoolExecutorVo threadPoolExecutorVo = MonitorableThreadPoolExecutorVo.getInstance(other.getExecutor());
    //        this.setExecutor(threadPoolExecutorVo.setValueFromOther(other.getExecutor()));
    //        this.setRunnable(new MonitorableRunnableVo().setValueFromOther(other.getRunnable()));
    //        return this;
    //    }

    @Override
    public void setExecutor(MonitorableThreadPoolExecutorIntf executor)
    {
        this.executor = (MonitorableThreadPoolExecutorVo) executor;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }
}