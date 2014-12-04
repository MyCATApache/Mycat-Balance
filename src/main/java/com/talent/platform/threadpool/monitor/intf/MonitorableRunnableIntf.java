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

import java.util.concurrent.atomic.AtomicLong;

import com.talent.platform.threadpool.intf.SynRunnableIntf;

/**
 * 可监控的任务(Runnable or Callable)
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public interface MonitorableRunnableIntf extends SynRunnableIntf
{

    /**
     * 计数器,用于获取被提交到线程池的次数
     * 
     * @return
     */
    AtomicLong getSubmitCount();

    /**
     * 执行的次数，与getSubmitCount()不同，此方法返回的数是该Runnable的有效执行次数，譬如消息处理类，该方法返回的是处理的消息总数
     * 
     * @return
     */
    AtomicLong getProcessedMsgCount();

    /**
     * 获取本任务的名字
     * 
     * @return
     */
    String getRunnableName();

    /**
     * 获取当前正在执行任务的Object
     * 
     * @return
     */
    String getCurrentProcessor();
}