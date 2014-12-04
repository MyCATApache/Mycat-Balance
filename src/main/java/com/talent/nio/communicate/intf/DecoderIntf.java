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
package com.talent.nio.communicate.intf;

import io.netty.buffer.ByteBuf;

import java.util.List;

import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;

/**
 * packet组包者
 * 
 * @filename: com.talent.nio.communicate.intf.PacketOgnzer
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-9-16 上午10:47:04
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
 *         <td>2013-9-16</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public interface DecoderIntf <T extends Packet>
{
    /**
     * 
     * @param data
     * @return
     */
    PacketWithMeta<T> decode(ByteBuf buffer, ChannelContext channelContext) throws DecodeException;

    public static class PacketWithMeta <T extends Packet>
    {
        private List<T> packets = null;
        private int packetLenght = -1;
        private int needLength = -1;

        public int getPacketLenght()
        {
            return packetLenght;
        }

        public void setPacketLenght(int packetLenght)
        {
            this.packetLenght = packetLenght;
        }

        public List<T> getPackets()
        {
            return packets;
        }

        public void setPackets(List<T> packets)
        {
            this.packets = packets;
        }

        public int getNeedLength()
        {
            return needLength;
        }

        public void setNeedLength(int needLength)
        {
            this.needLength = needLength;
        }
    }

    public static class DecodeException extends Exception
    {

        /**
         * 
         */
        private static final long serialVersionUID = 2168258709026948181L;

        public DecodeException()
        {

        }

        public DecodeException(String msg)
        {
            super(msg);
        }

        public DecodeException(Exception e)
        {
            super(e);
        }

    }

}