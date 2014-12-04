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
package com.talent.platform.threadpool.quickstart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.platform.threadpool.SynThreadPoolExecutor;

/**
 * 
 * @filename:	 com.talent.platform.threadpool.quickstart.QuickStartMain
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2012-5-15 上午10:54:50
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2012-5-15</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class QuickStartMain
{
    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(QuickStartMain.class);

    /**
     * 
     */
    public QuickStartMain()
    {

    }

    @SuppressWarnings("rawtypes")
    static final SynThreadPoolExecutor threadExecutor = new SynThreadPoolExecutor("quickstart-thread-pool");

    static final QuickStartRunnable<String> quickStartRunnable = new QuickStartRunnable<String>();

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        /**
         * 下面虽然会启动很多个线程，但是线程池只有一个线程用来执行同一个任务类(注意是同一个任务类，如果有多个任务类，仍然会启动多个线程来执行的)。
         * 这就是本框架所要达到的目的----同步安全。
         */

        int index = 0;
        new TestThread(index++).start();
        new TestThread(index++).start();
        new TestThread(index++).start();
        new TestThread(index++).start();
        new TestThread(index++).start();
        new TestThread(index++).start();
    }

    /**
     * 在这里只是演示一下多线程对线程池是否会有影响
     */
    static class TestThread extends Thread
    {
        int c = 0;

        public TestThread(int c)
        {
            this.c = c;
        }

        public void run()
        {
            for (int n = 0; n < 10000; n++)
            { // 往队列里加对象，相当于生产者
                quickStartRunnable.getMsgQueue().add(c + "--" + String.valueOf(n));
            }
            threadExecutor.execute(quickStartRunnable); // 提交到线程池
        }
    }
}