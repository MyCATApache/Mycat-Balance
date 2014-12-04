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

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.platform.threadpool.intf.SynRunnableIntf;
import com.talent.platform.threadpool.monitor.intf.MonitorableRunnableIntf;
import com.talent.platform.threadpool.monitor.intf.MonitorableThreadIntf;
import com.talent.platform.threadpool.monitor.intf.MonitorableThreadPoolExecutorIntf;

/**
 * 同步任务调度器:<br>
 * 用来调度那些实现了SynRunnableIntf的任务类对象
 * 
 * @author 谭耀武 2012-1-2 下午06:40:54
 * 
 * @param <T>
 *            可执行的任务类，必须继承自SynRunnableIntf<E>
 */
public class SynThreadPoolExecutor<T extends SynRunnableIntf> extends ThreadPoolExecutor implements
        MonitorableThreadPoolExecutorIntf
{

    public final static int CORE_POOL_NUM = 5;
    public final static int MAX_POOL_NUM = 40;
    public final static int KEEP_ALIVE_TIME = 90;
    public final static TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    public final static SynchronousQueue<Runnable> RUNNABLE_QUEUE = new SynchronousQueue<Runnable>(); // 存放runnable的队列

    private static final Logger log = LoggerFactory.getLogger(SynThreadPoolExecutor.class);

    private String name = null;

    /**
     * 
     */
    public SynThreadPoolExecutor(String name)
    {
        this(CORE_POOL_NUM, MAX_POOL_NUM, KEEP_ALIVE_TIME, TIME_UNIT, (BlockingQueue<Runnable>) RUNNABLE_QUEUE,
                DefaultThreadFactory.getInstance(name), name);
    }

    /**
     * 
     */
    public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> runnableQueue, String name)
    {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, (BlockingQueue<Runnable>) runnableQueue, DefaultThreadFactory
                .getInstance(name), name);
    }

    public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> runnableQueue, RejectedExecutionHandler handler, String name)
    {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, (BlockingQueue<Runnable>) runnableQueue, DefaultThreadFactory
                .getInstance(name), handler, name);
    }

    public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, String name)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, (BlockingQueue<Runnable>) runnableQueue, threadFactory, handler);
        this.name = name;
    }

    public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory, String name)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, (BlockingQueue<Runnable>) runnableQueue, threadFactory);

        RejectedExecutionHandler handler = new DefaultRejectedExecutionHandler(this);
        this.setRejectedExecutionHandler(handler);
        this.name = name;
    }

    @Override
    public void shutdown()
    {
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow()
    {
		List<Runnable> ret = super.shutdownNow();
        return ret;
    }



    @Override
    protected void beforeExecute(Thread thread, Runnable runnable)
    {
        super.beforeExecute(thread, runnable);
        @SuppressWarnings("unchecked")
        T runnableTask = (T) runnable;
        runnableTask.setRunning(true);
        runnableTask.setInSchedule(false);

        if (thread instanceof MonitorableThreadIntf)
        {
            MonitorableThreadIntf t1 = (MonitorableThreadIntf) thread;
            t1.setRunnable(runnableTask);
        }
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable)
    {
        super.afterExecute(runnable, throwable);
        @SuppressWarnings("unchecked")
        T runnableTask = (T) runnable;
        runnableTask.setRunning(false);
    }

    /**
     * 提交前作些检查，看是否有必要提交
     * 
     * @param runnable
     * @return true:可以提交，false:不需要提交
     */
    private boolean checkBeforeExecute(T runnable)
    {
        if (log.isDebugEnabled())
        {
            log.debug(
                    "poolSize:{},largestPoolSize:{},completedTaskCount:{},activeCount:{},corePoolSize:{},maximumPoolSize:{},queue:{}",
                    new Object[]
                    { getPoolSize(), getLargestPoolSize(), getCompletedTaskCount(), getActiveCount(), getCorePoolSize(),
                            getMaximumPoolSize(), getQueue() });
        }

        if (runnable.isRunning() || runnable.isInSchedule())
        {
            return false;
        } else
        {
            if (!runnable.isRunning() && !runnable.isInSchedule())
            {
                return true;
            } else
            {
                return false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Runnable _runnable)
    {
        T runnable = (T) _runnable;
        if (checkBeforeExecute(runnable))
        {
            runnable.setInSchedule(true);
            super.execute(runnable);

            if (runnable instanceof MonitorableRunnableIntf)
            {
                MonitorableRunnableIntf monitorableRunnable = (MonitorableRunnableIntf) runnable;
                monitorableRunnable.getSubmitCount().incrementAndGet();

                if (log.isDebugEnabled())
                {
                    log.debug("{} has been submitted, this task have been submitted {} times.",
                            monitorableRunnable.getRunnableName(), monitorableRunnable.getSubmitCount().get());
                }
            }

        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> Future<R> submit(Runnable _runnable, R result)
    {
        T runnable = (T) _runnable;
        if (checkBeforeExecute(runnable))
        {
            runnable.setInSchedule(true);
            Future<R> ret = super.submit(runnable, result);
            if (runnable instanceof MonitorableRunnableIntf)
            {
                MonitorableRunnableIntf monitorableRunnable = (MonitorableRunnableIntf) runnable;
                monitorableRunnable.getSubmitCount().incrementAndGet();
                if (log.isDebugEnabled())
                {
                    log.debug("{} has been submitted, this task have been submitted {} times.",
                            monitorableRunnable.getRunnableName(), monitorableRunnable.getSubmitCount().get());
                }
            }

            return ret;
        } else
        {
            return null;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    @Override
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public int compareTo(Object o)
    {
        @SuppressWarnings("rawtypes")
        SynThreadPoolExecutor other = (SynThreadPoolExecutor) o;
        return this.getName().compareTo(other.getName());
    }
}