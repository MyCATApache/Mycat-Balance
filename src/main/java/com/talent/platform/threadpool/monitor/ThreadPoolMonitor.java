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

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.talent.platform.threadpool.monitor.intf.MonitorableThreadPoolExecutorIntf;
import com.talent.platform.threadpool.monitor.intf.ThreadPoolMonitorIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public class ThreadPoolMonitor implements ThreadPoolMonitorIntf
{

    private Set<MonitorableThreadPoolExecutorIntf> executorSet = Collections
            .synchronizedSet(new TreeSet<MonitorableThreadPoolExecutorIntf>());

    private static ThreadPoolMonitor instance = new ThreadPoolMonitor();

    /**
     * 暂时采用单态实例，以后有需要可以采用工厂模式
     * 
     * @return
     */
    public static ThreadPoolMonitor getInstance()
    {
        return instance;
    }

    /**
     * 
     */
    private ThreadPoolMonitor()
    {

    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    @Override
    public boolean register(MonitorableThreadPoolExecutorIntf executor)
    {
        return executorSet.add(executor);
    }

    @Override
    public Set<MonitorableThreadPoolExecutorIntf> getExecutors()
    {
        return executorSet;
    }
}