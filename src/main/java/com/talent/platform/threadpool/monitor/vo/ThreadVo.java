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

/**
 * 
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public class ThreadVo implements java.io.Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 84686040098906523L;
    /**
     * 线程名字
     */
    private String name = null;
    /**
     * 线程标识
     */
    private long id = 0;
    /**
     * 线程所耗CPU时间
     */
    private long cpuTime = 0;

    /**
     * @return the cpuTime
     */
    public long getCpuTime()
    {
        return cpuTime;
    }

    /**
     * @param cpuTime
     *            the cpuTime to set
     */
    public void setCpuTime(long cpuTime)
    {
        this.cpuTime = cpuTime;
    }

    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
}