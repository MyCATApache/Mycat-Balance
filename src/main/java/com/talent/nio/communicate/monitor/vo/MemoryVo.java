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
package com.talent.nio.communicate.monitor.vo;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class MemoryVo implements java.io.Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 2967217145577974773L;

    /**
     * 已经使用的堆区内存
     */
    private long heapMemory = 0;

    /**
     * 已经使用的非堆内存
     */
    private long nonHeapMemory = 0;
    /**
     * 初始内存数量
     */
    private long initMemory = 0;
    /**
     * 已经使用的内存数量
     */
    private long usedMemory = 0;
    /**
     * 可用的内存数量
     */
    private long usableMemory = 0;
    /**
     * 最大可用内存数量
     */
    private long maxMemory = 0;
    /**
     * JVM中空闲的内存
     */
    private long freeMemory = 0;

    /**
     * @return the freeMemory
     */
    public long getFreeMemory()
    {
        return freeMemory;
    }

    /**
     * @param freeMemory
     *            the freeMemory to set
     */
    public void setFreeMemory(long freeMemory)
    {
        this.freeMemory = freeMemory;
    }

    /**
     * @return the heapMemory
     */
    public long getHeapMemory()
    {
        return heapMemory;
    }

    /**
     * @param heapMemory
     *            the heapMemory to set
     */
    public void setHeapMemory(long heapMemory)
    {
        this.heapMemory = heapMemory;
    }

    /**
     * @return the initMemory
     */
    public long getInitMemory()
    {
        return initMemory;
    }

    /**
     * @param initMemory
     *            the initMemory to set
     */
    public void setInitMemory(long initMemory)
    {
        this.initMemory = initMemory;
    }

    /**
     * @return the maxMemory
     */
    public long getMaxMemory()
    {
        return maxMemory;
    }

    /**
     * @param maxMemory
     *            the maxMemory to set
     */
    public void setMaxMemory(long maxMemory)
    {
        this.maxMemory = maxMemory;
    }

    /**
     * @return the nonHeapMemory
     */
    public long getNonHeapMemory()
    {
        return nonHeapMemory;
    }

    /**
     * @param nonHeapMemory
     *            the nonHeapMemory to set
     */
    public void setNonHeapMemory(long nonHeapMemory)
    {
        this.nonHeapMemory = nonHeapMemory;
    }

    /**
     * @return the usableMemory
     */
    public long getUsableMemory()
    {
        return usableMemory;
    }

    /**
     * @param usableMemory
     *            the usableMemory to set
     */
    public void setUsableMemory(long usableMemory)
    {
        this.usableMemory = usableMemory;
    }

    /**
     * @return the usedMemory
     */
    public long getUsedMemory()
    {
        return usedMemory;
    }

    /**
     * @param usedMemory
     *            the usedMemory to set
     */
    public void setUsedMemory(long usedMemory)
    {
        this.usedMemory = usedMemory;
    }

}