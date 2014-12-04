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
package com.talent.mysql.packet;

import io.netty.buffer.ByteBuf;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.common.BalancePacket;
import com.talent.balance.common.ParseUtils;
import com.talent.mysql.packet.factory.MysqlHeaderFactory;
import com.talent.mysql.packet.factory.MysqlHeaderFactory.MysqlHeader;
import com.talent.nio.api.Packet;
import com.talent.nio.communicate.intf.DecoderIntf.DecodeException;
import com.talent.nio.communicate.intf.DecoderIntf.PacketWithMeta;

/**
 * 
 * @filename:	 com.talent.mysql.packet.MysqlResponsePacket
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月25日 下午6:28:48
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月25日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public abstract class MysqlResponsePacket<T extends MysqlResponsePacket<?>> extends BalancePacket
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7952416644049477509L;
	private static Logger log = LoggerFactory.getLogger(MysqlResponsePacket.class);

	private MysqlHeader mysqlHeader = null;

	/**
	 * 
	 */
	public MysqlResponsePacket(MysqlHeader mysqlHeader)
	{
		this.setMysqlHeader(mysqlHeader);
	}

	public MysqlResponsePacket()
	{

	}

	public PacketWithMeta<Packet> decode(ByteBuf byteBuf) throws DecodeException
	{
		int capacity = byteBuf.capacity();
		PacketWithMeta<Packet> packetWithMeta = null;
		while (true)
		{
			if ((capacity - byteBuf.readerIndex()) < MysqlHeader.HEADER_LEN) //不够头的长度
			{
				if (packetWithMeta != null)
				{
					packetWithMeta.setPacketLenght(byteBuf.readerIndex());
				}

				return packetWithMeta;
			}

			if (byteBuf.order() == ByteOrder.BIG_ENDIAN)
			{
				byteBuf = byteBuf.order(ByteOrder.LITTLE_ENDIAN);
			}

			MysqlHeader header = MysqlHeaderFactory.borrow();
			header.decode(byteBuf);

			ParseUtils.processReadIndex(byteBuf);

			//长度不够
			if (capacity - byteBuf.readerIndex() < header.getBodyLength()) //不够体的长度
			{
				if (packetWithMeta != null)
				{
					packetWithMeta.setPacketLenght(byteBuf.readerIndex());
				}
				return packetWithMeta;
			} else
			{
				T t = decodeBody(byteBuf, header);
				if (packetWithMeta == null)
				{
					packetWithMeta = new PacketWithMeta<Packet>();

					List<Packet> packets = new ArrayList<Packet>();
					packets.add(t);

					packetWithMeta.setPackets(packets);
					packetWithMeta.setPacketLenght(byteBuf.readerIndex());
					return packetWithMeta;
					
//					com.talent.mysql.packet.factory.MysqlHeaderFactory.returnObj(t.getMysqlHeader());
				} else
				{
					packetWithMeta.getPackets().add(t);
				}
			}
		}
		
		
	}

	/**
	 * 
	 * @param byteBuf
	 * @return
	 */
	public void decodeHeader(ByteBuf byteBuf)
	{
		MysqlHeader mysqlHeader = MysqlHeaderFactory.borrow();
		mysqlHeader.decode(byteBuf);
	}

	public abstract T decodeBody(ByteBuf byteBuf, MysqlHeader mysqlHeader) throws DecodeException;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	@Override
	public String getSeqNo()
	{
		return null;
	}

	public MysqlHeader getMysqlHeader()
	{
		return mysqlHeader;
	}

	public void setMysqlHeader(MysqlHeader mysqlHeader)
	{
		this.mysqlHeader = mysqlHeader;
	}
}