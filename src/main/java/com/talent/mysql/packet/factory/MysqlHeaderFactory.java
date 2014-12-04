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
package com.talent.mysql.packet.factory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteOrder;
import java.util.NoSuchElementException;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @filename:	 com.talent.mysql.packet.factory.MysqlHeaderFactory
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月27日 上午9:16:56
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月27日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class MysqlHeaderFactory
{
	private static Logger log = LoggerFactory.getLogger(MysqlHeaderFactory.class);

	/**
	 * 
	 */
	private MysqlHeaderFactory()
	{

	}

	private static ObjectPool<MysqlHeader> objectPool = new GenericObjectPool<MysqlHeader>(new MysqlHeaderPoolFactory());

	public static MysqlHeader borrow()
	{
		try
		{
			MysqlHeader mysqlHeader = objectPool.borrowObject();
			log.warn("borrow obj {}", mysqlHeader);
			return mysqlHeader;
		} catch (Exception e)
		{
			log.error("borrow obj fail", e);
			return new MysqlHeader();
		}
	}

	public static MysqlHeader borrow(int bodyLength, byte serialNum)
	{
		try
		{
			MysqlHeader mysqlHeader = borrow();
			log.warn("borrow obj {}", mysqlHeader);
			mysqlHeader.setBodyLength(bodyLength);
			mysqlHeader.setSerialNum(serialNum);
			return mysqlHeader;
		} catch (Exception e)
		{
			log.error("borrow obj fail", e);
			return new MysqlHeader(bodyLength, serialNum);
		}
	}

	public static void returnObj(MysqlHeader mysqlHeader)
	{
		try
		{
			log.warn("return obj {}", mysqlHeader);
			objectPool.returnObject(mysqlHeader);
		} catch (Exception e)
		{
			log.error("return obj fail", e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		int bodyLength = 9;
		byte serialNum = 2;
		MysqlHeader mysqlHeader = new MysqlHeader(bodyLength, serialNum);
		ByteBuf byteBuf = mysqlHeader.encode();
		byteBuf.readerIndex(0);
		mysqlHeader.decode(byteBuf);
	}

	public static class MysqlHeader
	{
		private static Logger log = LoggerFactory.getLogger(MysqlHeader.class);

		public static final int HEADER_LEN = 4;

		private MysqlHeader(int bodyLength, byte serialNum)
		{
			super();
			this.bodyLength = bodyLength;
			this.serialNum = serialNum;
		}

		/**
		 * 
		 */
		MysqlHeader()
		{

		}

		private int bodyLength = 0;

		private byte serialNum = 0;

		public ByteBuf encode()
		{
			ByteBuf _byteBuf = Unpooled.buffer(bodyLength + HEADER_LEN);
			ByteBuf byteBuf = _byteBuf.order(ByteOrder.LITTLE_ENDIAN);

			boolean xx = _byteBuf == byteBuf;

			byteBuf.setInt(0, bodyLength);
			byteBuf.setByte(3, serialNum);
			byteBuf.writerIndex(byteBuf.capacity());
			byteBuf.readerIndex(4);
			return byteBuf;
		}

		public void decode(ByteBuf byteBuf)
		{
			byteBuf = byteBuf.order(ByteOrder.LITTLE_ENDIAN);

			ByteBuf lengthBuf = Unpooled.buffer(4);
			lengthBuf = lengthBuf.order(ByteOrder.LITTLE_ENDIAN);

			byteBuf.getBytes(byteBuf.readerIndex(), lengthBuf, 3);
			bodyLength = lengthBuf.getInt(0);
			serialNum = byteBuf.getByte(byteBuf.readerIndex() + 3);
			byteBuf.writerIndex(byteBuf.capacity());
			byteBuf.readerIndex(byteBuf.readerIndex() + 3);
		}

		public int getBodyLength()
		{
			return bodyLength;
		}

		public void setBodyLength(int bodyLength)
		{
			this.bodyLength = bodyLength;
		}

		public byte getSerialNum()
		{
			return serialNum;
		}

		public void setSerialNum(byte serialNum)
		{
			this.serialNum = serialNum;
		}
	}
}