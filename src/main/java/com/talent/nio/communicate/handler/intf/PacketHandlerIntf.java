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
package com.talent.nio.communicate.handler.intf;

import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;

/**
 * 
 * @author 谭耀武
 * @date 2011-12-27
 * 
 */
public interface PacketHandlerIntf
{
    /**
     * 
     * 在发送消息前，将消息序列化（如果没有特殊需求，一般不需要实现此方法，直接return null即可。）
     * 
     * @param packet
     * @return
     * @throws Exception
     */
    byte[] onSend(Packet packet, ChannelContext channelContext) throws Exception;

    /**
     * 
     * 处理接收到的消息
     * 
     * @param packet
     * @throws Exception
     */
    void onReceived(Packet packet, ChannelContext channelContext) throws Exception;
}