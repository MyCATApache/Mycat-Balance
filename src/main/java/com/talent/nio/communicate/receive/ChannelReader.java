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
package com.talent.nio.communicate.receive;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.utils.SystemTimer;

/**
 * 
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public abstract class ChannelReader
{
	private static final Logger log = LoggerFactory.getLogger(ChannelReader.class);
	private static AtomicLong receivedByteCount = new AtomicLong();
	private static final int CAPACITY_OF_BYTEBUFFER = 1048576;//1048576;

	/**
	 * 存放接收到的数据
	 */
	private static ByteBuffer byteBuffer = ByteBuffer.allocate(CAPACITY_OF_BYTEBUFFER); // allocateDirect(CAPACITY_OF_BYTEBUFFER)

	/**
	 * 所有通道读数据的次数
	 */
	private static AtomicLong readCount = new AtomicLong();

	/**
	 * 
	 */
	public ChannelReader(ChannelContext channelContext)
	{

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	public static ByteBuf read(SocketChannel socketChannel, ChannelContext channelContext) throws IOException
	{
		byteBuffer.clear(); // 复位buffer，准备接收数据

		if (log.isDebugEnabled())
		{
			log.debug("{},buffer before read:{}", channelContext, byteBuffer);
		}

		int countOfRead = socketChannel.read(byteBuffer);

		long receivedBytes = channelContext.getStatVo().getReceivedBytes() + countOfRead;
		channelContext.getStatVo().setReceivedBytes(receivedBytes);
		if (log.isDebugEnabled())
		{
			log.debug("{} has received {} bytes", channelContext.getId(), receivedBytes);

			log.debug("{},buffer after read:{}", channelContext, byteBuffer);
		}

		if (countOfRead == -1)
		{
			String string = "count of read is -1, " + channelContext.getId();
			log.error(string);
			throw new EOFException(string);
			//            NioUtils.remove(channelContext, string);
			//            channelContext.getReadIOErrorHandler().handle(socketChannel, (IOException) null, channelContext, "count of read is -1");
			//            return null;
		}
		if (countOfRead == 0)
		{
			log.warn("0 byte read {}", channelContext.getId());
			return null;
		}

		channelContext.getStatVo().setCurrentReceivedTime(SystemTimer.currentTimeMillis());

		receivedByteCount.addAndGet(countOfRead);
		if (log.isDebugEnabled())
		{
			log.debug(("received {} bytes, all received {} bytes"), countOfRead, receivedByteCount.get());
		}

		// byte[] datas = new byte[byteBuffer.position()];
		byteBuffer.flip(); // 准备读数据
		// byteBuffer.get(datas); // 将数据从buffer读到字节数组中

		ByteBuf buf1 = Unpooled.copiedBuffer(byteBuffer);

		readCount.incrementAndGet();
		channelContext.getStatVo().getReadCount().incrementAndGet();

		return buf1;

	}

	public static AtomicLong getReadCount()
	{
		return readCount;
	}

	public static void setReadCount(AtomicLong readCount)
	{
		ChannelReader.readCount = readCount;
	}

	public static AtomicLong getReceivedByteCount()
	{
		return receivedByteCount;
	}

	public static void setReceivedByteCount(AtomicLong receivedByteCount)
	{
		ChannelReader.receivedByteCount = receivedByteCount;
	}
}