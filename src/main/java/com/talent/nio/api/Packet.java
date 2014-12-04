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
package com.talent.nio.api;

import java.io.Serializable;

/**
 * 
 * 
 * @filename: com.talent.nio.common.Packet
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-9-16 下午1:54:31
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
 *         <td>2013-9-20</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public abstract class Packet implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 587807168164800128L;

    public Packet()
    {

    }

    /**
     * 是否是同步发送。封装在框架中，应用不用设置此值
     */
    private boolean isSyn = false; // 是否是同步发送

    /**
     * 封装在框架中，应用不用设置此值
     * 
     * @author tanyaowu
     * @param isSyn
     */
    public void setSyn(boolean isSyn)
    {
        this.isSyn = isSyn;
    }

    public boolean isSyn()
    {
        return isSyn;
    }
    
    /**
     * 获取同步序列号
     * 
     * @return
     */
    public abstract String getSeqNo();

}