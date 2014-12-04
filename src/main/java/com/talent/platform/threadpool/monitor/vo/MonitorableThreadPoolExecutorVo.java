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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.talent.platform.threadpool.monitor.intf.MonitorableThreadPoolExecutorIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public class MonitorableThreadPoolExecutorVo implements MonitorableThreadPoolExecutorIntf
{

    private String name = null;
    private int corePoolSize = 0;
    private int maximumPoolSize = 0;
    private int poolSize = 0;
    private int activeCount = 0;
    private int largestPoolSize = 0;
    private long completedTaskCount = 0;
    private BlockingQueue<Runnable> queue = null;

    private static Map<MonitorableThreadPoolExecutorIntf, MonitorableThreadPoolExecutorVo> mapOfClassNameAndExecutor = new HashMap<MonitorableThreadPoolExecutorIntf, MonitorableThreadPoolExecutorVo>();

    /**
     * 
     */
    private MonitorableThreadPoolExecutorVo()
    {

    }

    public static MonitorableThreadPoolExecutorVo getInstance(MonitorableThreadPoolExecutorIntf monitorableThreadPoolExecutor)
    {
        MonitorableThreadPoolExecutorVo executor = mapOfClassNameAndExecutor.get(monitorableThreadPoolExecutor);
        if (executor == null)
        {
            executor = new MonitorableThreadPoolExecutorVo();
            mapOfClassNameAndExecutor.put(monitorableThreadPoolExecutor, executor);
        }
        return executor;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    public MonitorableThreadPoolExecutorVo setValueFromOther(MonitorableThreadPoolExecutorIntf monitorableThreadPoolExecutor)
    {
        this.setActiveCount(monitorableThreadPoolExecutor.getActiveCount());
        this.setCompletedTaskCount(monitorableThreadPoolExecutor.getCompletedTaskCount());
        this.setCorePoolSize(monitorableThreadPoolExecutor.getCorePoolSize());
        this.setLargestPoolSize(monitorableThreadPoolExecutor.getLargestPoolSize());
        this.setMaximumPoolSize(monitorableThreadPoolExecutor.getMaximumPoolSize());
        this.setName(monitorableThreadPoolExecutor.getName());
        this.setPoolSize(monitorableThreadPoolExecutor.getPoolSize());
        // this.setQueue(monitorableThreadPoolExecutor.getQueue());
        return this;
    }

    @Override
    public void execute(Runnable command)
    {
        throw new RuntimeException(MonitorableThreadPoolExecutorVo.class.getName() + " is not implement this method");
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getCorePoolSize()
    {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize)
    {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize()
    {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize)
    {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getPoolSize()
    {
        return poolSize;
    }

    public void setPoolSize(int poolSize)
    {
        this.poolSize = poolSize;
    }

    public int getActiveCount()
    {
        return activeCount;
    }

    public void setActiveCount(int activeCount)
    {
        this.activeCount = activeCount;
    }

    public int getLargestPoolSize()
    {
        return largestPoolSize;
    }

    public void setLargestPoolSize(int largestPoolSize)
    {
        this.largestPoolSize = largestPoolSize;
    }

    public long getCompletedTaskCount()
    {
        return completedTaskCount;
    }

    public void setCompletedTaskCount(long completedTaskCount)
    {
        this.completedTaskCount = completedTaskCount;
    }

    public BlockingQueue<Runnable> getQueue()
    {
        return queue;
    }

    public void setQueue(BlockingQueue<Runnable> queue)
    {
        this.queue = queue;
    }

    @Override
    public int compareTo(Object o)
    {
        MonitorableThreadPoolExecutorVo other = (MonitorableThreadPoolExecutorVo) o;
        return this.getName().compareTo(other.getName());
    }
}