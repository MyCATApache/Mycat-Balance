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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.platform.threadpool.monitor.MonitorableThread;

/**
 * 默认的ThreadFactory，在生成Thread对象时，会使用应用提供的名字+序号作为线程的名字，这样方便大家辨认线程。<br>
 * 生成的Thread对象，其名字形如：myname-1, myname-2等
 * 
 * @filename: com.talent.platform.threadpool.DefaultThreadFactory
 * @copyright: Copyright (c)2012
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2012-5-2 下午5:38:35
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
 *         <td>2012-5-2</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class DefaultThreadFactory implements ThreadFactory
{
    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(DefaultThreadFactory.class);

    private String threadPoolName = null;

    private static Map<String, DefaultThreadFactory> mapOfNameAndThreadFactory = new HashMap<String, DefaultThreadFactory>();
    private static Map<String, AtomicInteger> mapOfNameAndAtomicInteger = new HashMap<String, AtomicInteger>();

    public static DefaultThreadFactory getInstance(String threadName)
    {
        DefaultThreadFactory defaultThreadFactory = mapOfNameAndThreadFactory.get(threadName);
        if (defaultThreadFactory == null)
        {
            defaultThreadFactory = new DefaultThreadFactory();
            defaultThreadFactory.setThreadName(threadName);
            mapOfNameAndThreadFactory.put(threadName, defaultThreadFactory);
            mapOfNameAndAtomicInteger.put(threadName, new AtomicInteger());
        }
        return defaultThreadFactory;
    }

    /**
     * 
     */
    private DefaultThreadFactory()
    {

    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    @Override
    public Thread newThread(Runnable r)
    {
        MonitorableThread thread = new MonitorableThread(r);
        thread.setName(this.getThreadPoolName() + "-" + mapOfNameAndAtomicInteger.get(this.getThreadPoolName()).incrementAndGet());
        return thread;
    }

    public String getThreadPoolName()
    {
        return threadPoolName;
    }

    public void setThreadName(String threadName)
    {
        this.threadPoolName = threadName;
    }
}