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
package com.talent.platform.threadpool.demo;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.platform.threadpool.SynThreadPoolExecutor;

/**
 * 
 * @filename: com.talent.platform.threadpool.demo.TalentThreadPoolDemo
 * @copyright: Copyright (c)2012
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2012-4-25 下午3:45:00
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
 *         <td>2012-4-25</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class PerformanceTest
{
    private static Logger log = LoggerFactory.getLogger(PerformanceTest.class);

    public static final int runnableCount = 20;

    final static int testThreadCount = 10;
    final static int submitCount = 1000; // 提交到线程池的次数
    final static int recordCountPerSubmit = 10; // 每提交一次，往队列中添加的记录数
    final static int countPerRunnable = testThreadCount * submitCount * recordCountPerSubmit;
    final static int allCount = runnableCount * countPerRunnable;

    // QueueThreadPoolExecutor所需要的一些参数
    static int initnum = 1;
    static int maxnum = 100;
    static int keepAliveTime = 90;
    static TimeUnit timeUnit = TimeUnit.SECONDS;
    static SynchronousQueue<Runnable> runnableQueue = new SynchronousQueue<Runnable>(); // 存放runnable的队列
    static String threadName = "demo-thread-pool";

    @SuppressWarnings("rawtypes")
    static final SynThreadPoolExecutor<DemoRunnable> threadExecutor = new SynThreadPoolExecutor<DemoRunnable>(
            initnum, maxnum, keepAliveTime, timeUnit, runnableQueue, threadName);

    static final long start = System.currentTimeMillis();
    static final CyclicBarrier barrier = new CyclicBarrier(runnableCount, new Runnable()
    {
        public void run()
        {
            long end = System.currentTimeMillis();
            long result = (allCount * 1000) / (end - start);
            String logStr = "consume " + (end - start) + "ms, perfermance: " + result + "/s";
            log.info(logStr);
            System.out.println(logStr);
            threadExecutor.shutdown();
        }
    });

    @SuppressWarnings("unchecked")
    final static DemoRunnable<String>[] myRunnables = new DemoRunnable[runnableCount];
    static
    {
        for (int i = 0; i < runnableCount; i++)
        {
            myRunnables[i] = new DemoRunnable<String>(countPerRunnable, barrier);
            myRunnables[i].setRunnableName(DemoRunnable.class.getSimpleName() + "[" + i + "]");
        }
    }

    /**
     * 
     */
    public PerformanceTest()
    {

    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

        // TestThread testThread = new TestThread(threadExecutor);
        // testThread.start();
        for (int i = 0; i < testThreadCount; i++)
        {
            TestThread testThread = new TestThread(threadExecutor);
            testThread.start();
        }

        // threadExecutor.shutdown();
    }

    static class TestThread extends Thread
    {
        @SuppressWarnings("rawtypes")
        SynThreadPoolExecutor<DemoRunnable> threadExecutor;

        @SuppressWarnings("unchecked")
        public TestThread(@SuppressWarnings("rawtypes") SynThreadPoolExecutor threadExecutor)
        {
            this.threadExecutor = threadExecutor;
        }

        public void run()
        {
            for (int j = 0; j < runnableCount; j++)
            {
                final int x = j;
                int i = 0;
                while (i++ < submitCount)
                {
                    for (int n = 0; n < recordCountPerSubmit; n++)
                    { // 往队列里加对象，相当于生产者
                        myRunnables[x].getMsgQueue().add(x + "-" + i + "-" + n);
                    }
                    threadExecutor.execute(myRunnables[x]); // 提交到线程池
                    try
                    {
                        Thread.sleep(1);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}