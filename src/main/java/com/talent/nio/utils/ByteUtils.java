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
package com.talent.nio.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

/**
 * 
 * 
 * @filename: com.talent.nio.utils.EncodeUtils
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-5-17 上午9:50:23
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
 *         <td>2012-5-17</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class ByteUtils
{
    /**
     * 
     * @author tanyaowu
     * @param bytes1
     * @param bytes2
     * @return
     */
    public static byte[] joinBytes(byte[] bytes1, byte[] bytes2)
    {
        if (bytes1 == null && bytes2 == null)
        {
            return null;
        }

        if (bytes1 == null || bytes1.length == 0)
        {
            return bytes2 == null ? bytes1 : bytes2;
        }
        if (bytes2 == null || bytes2.length == 0)
        {
            return bytes1 == null ? bytes2 : bytes1;
        }

        byte[] ret = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, ret, 0, bytes1.length);
        System.arraycopy(bytes2, 0, ret, bytes1.length, bytes2.length);
        return ret;
    }

    /**
     * 
     * @param buffer
     * @return
     * @throws IOException
     */
    public static List<String> toLinesList(ByteBuf buffer) throws IOException
    {
        List<String> retList = new ArrayList<String>(20);

        int lastPosition = 0;
        int byteCountInOneLine = 0; // 记录一行的字节数

        byte lastByte = 0; // 上一个字节
        int length = buffer.capacity();
        for (int i = 0; i < length; i++)
        {
            byte b = buffer.getByte(i);// .get();
            boolean isLastByte = (length - 1 == i); // 是否是最后一个字节
            byteCountInOneLine++;

            if (b == '\n')
            {
                if ((i > 0 && lastByte == '\r'))
                {
                    if (byteCountInOneLine == 2) // 这个判断用来节约性能的，逻辑层面可以不要
                    {
                        retList.add("\r\n"); // 空行
                    } else
                    {
                        byte[] bs1 = new byte[byteCountInOneLine];
                        buffer.getBytes(lastPosition, bs1);
                        String line1 = new String(bs1, "utf-8");
                        retList.add(line1);
                    }
                } else {
                    if (byteCountInOneLine == 1) // 这个判断用来节约性能的，逻辑层面可以不要
                    {
                        retList.add("\n"); // 空行
                    } else
                    {
                        byte[] bs1 = new byte[byteCountInOneLine];
                        buffer.getBytes(lastPosition, bs1);
                        String line1 = new String(bs1, "utf-8");
                        retList.add(line1);
                    }
                }
               
                byteCountInOneLine = 0;
                lastPosition = i + 1;
            } else if (isLastByte)
            {
                byte[] bs1 = new byte[byteCountInOneLine];
                buffer.getBytes(lastPosition, bs1);
                String line1 = new String(bs1, "utf-8");
                retList.add(line1);
            }

            lastByte = b;
        }
        return retList;
    }

    /**
     * 
     * @param source
     * @param srcBegin
     * @return
     */
    public static byte[] subbytes(byte[] source, int srcBegin)
    {
        return subbytes(source, srcBegin, source.length);
    }

    /**
     * 
     * @param source
     * @param srcBegin
     * @param length
     * @return
     */
    public static byte[] subbytes(byte[] source, int srcBegin, int length)
    {
        byte[] destination = new byte[length];
        getBytes(source, srcBegin, length, destination, 0);

        return destination;
    }

    /**
     * 
     * @param source
     * @param srcBegin
     * @param length
     * @param destination
     * @param dstBegin
     */
    public static void getBytes(byte[] source, int srcBegin, int length, byte[] destination, int dstBegin)
    {
        System.arraycopy(source, srcBegin, destination, dstBegin, length);
    }

    public static void main(String[] args) throws IOException
    {
        byte[] bs = "1hello world\r\nhehe".getBytes();
        System.out.println(ArrayUtils.toString(bs));
        ByteBuf buf = Unpooled.copiedBuffer(bs);
        String xString = buf.toString();
        ByteUtils.toLinesList(buf);

        bs = "2hello world hehe\r\n".getBytes();
        buf = Unpooled.copiedBuffer(bs);
        ByteUtils.toLinesList(buf);

        bs = "3hello world\r\nhehe\r\n\r\n".getBytes();
        buf = Unpooled.copiedBuffer(bs);
        ByteUtils.toLinesList(buf);

        bs = "4\rhe\nllo world\r\nhehe".getBytes();
        buf = Unpooled.copiedBuffer(bs);
        ByteUtils.toLinesList(buf);
    }

}