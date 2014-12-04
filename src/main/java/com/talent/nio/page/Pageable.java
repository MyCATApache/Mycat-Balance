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
package com.talent.nio.page;

/**
 * @filename: Pageable.java
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2010-4-15 上午08:42:11
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
 *         <td>2010-4-15</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public interface Pageable
{

    /**
     * 默认的pagesize--10行
     */
    final int DEFAULT_PAGESIZE = 10;

    /**
     * 默认的pageindex--第1页
     */
    final int DEFAULT_PAGEINDEX = 1;

    // /**
    // * 返回总页数
    // */
    // long getPageCount();

    /**
     * 返回每页有多少条记录
     */
    int getPageSize();

    /**
     * 返回总记录行数
     */
    long getRecordCount();

    /**
     * 设置每页记录数
     * 
     * @param pageSize
     */
    void setPageSize(int pageSize);

    /**
     * 设置当前页码
     * 
     * @param pageIndex
     */
    void setPageIndex(int pageIndex);

    /**
     * 返回当前页号，从1开始
     * 
     * @return
     */
    long getPageIndex();

    /**
     * 获取当前的数据
     * 
     * @return
     */
    Object getData();

    /**
     * 是否分页。true:分页，false:不分页，默认分页
     * 
     * @return
     */
    boolean isPagination();

    /**
     * 
     * @param isPagination
     */
    void setPagination(boolean isPagination);

    /**
     * 加载/重新加载数据
     */
    void reload();

    // previous
}