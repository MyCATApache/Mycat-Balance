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
package com.talent.mysql.packet.response;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.mysql.packet.MysqlResponsePacket;
import com.talent.mysql.packet.factory.MysqlHeaderFactory.MysqlHeader;
import com.talent.nio.communicate.intf.DecoderIntf.DecodeException;

/**
 * <pre>
	42 00 00                    //数据长度，3字节，0x42=66字节<br>
	00                          //序号，1字节<br>
	0A                          //协议，1字节，0x0A=10，表示第10版协议<br>
	35 2E 31 2E 34 39 2D 63 6F 6D 6D 75 6E 69 74 79 2D 6C 6F 67 00  //版本信息，字符串，以\0结尾，内容为5.1.49-community-log<br>
	14 00 00 00                 //连接ID，4字节，0x14=20<br>
	5e 63 59 72 54 2c 7b 4a     //加密串的前半部分，定长8字节<br>
	00                          //固定填充0<br>
	FF F7                       //服务端属性的低16位，2字节，枚举参见网站<br>
	1C                          //字符集，1字节，0x1c=28=gbk_chinese_ci<br>
	02 00                       //服务端状态，2字节，枚举参见网站<br>
	00 00                       //服务端属性的高16位，2字节<br>
	00                          //固定填充0<br>
	00 00 00 00 00 00 00 00 00 00   //固定填充0，10字节<br>
	5A 7C 24 39 32 2E 2F 43 40 5A 25 46 00      //加密串的后半部分，以\0结尾，加密串总共8+12=20字节<br>
	string[NUL]                 //auth-plugin name
	</pre>
 * @see http://dev.mysql.com/doc/internals/en/initial-handshake.html
 * 
 * @filename:	 com.talent.mysql.packet.response.HandshakePacket
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月25日 下午5:16:51
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
public class HandshakePacket extends MysqlResponsePacket<HandshakePacket>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4738459630754351291L;
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HandshakePacket.class);

	public byte protocolVersion;
	public byte[] versionInfo = null; //版本信息，字符串，以\0结尾
	public long threadId = 0; //连接ID，4字节，0x14=20
	public byte[] encrypt1 = null; //加密串的前半部分，定长8字节
	public byte fix1 = 0; //固定填充0
	public byte[] serverProp1 = null; //服务端属性的低16位，2字节，枚举参见网站
	public byte charset = 0; //字符集，1字节，0x1c=28=gbk_chinese_ci
	public byte[] serverStatus = null; //服务端状态，2字节，枚举参见网站
	public byte[] serverProp2 = null; //服务端属性的高16位，2字节
	public byte fix2 = 0; //固定填充0
	public byte[] byte10 = new byte[10]; //固定填充0，10字节
	public byte[] encrypt2 = null; //加密串的后半部分，以\0结尾，加密串总共8+12=20字节
	public byte[] authPluginName = null;

	/**
	 * 
	 */
	public HandshakePacket()
	{

	}

	@Override
	public HandshakePacket decodeBody(ByteBuf byteBuf, MysqlHeader mysqlHeader) throws DecodeException
	{
		this.setMysqlHeader(mysqlHeader);
		int _index = byteBuf.readerIndex();
		int index = _index;

		protocolVersion = byteBuf.getByte(index++);

		int len = 0;
		while (byteBuf.getByte(index+len) != 0)
		{
			len++;
		}
		versionInfo = new byte[len];
		byteBuf.getBytes(index, versionInfo, 0, len);
		index += len;
		index++;

		threadId = byteBuf.getInt(index);
		index+=4;

		encrypt1 = new byte[8];
		byteBuf.getBytes(index, encrypt1, 0, 8);
		index += 8;

		fix1 = byteBuf.getByte(index++);

		serverProp1 = new byte[2];
		byteBuf.getBytes(index, serverProp1, 0, 2);
		index += 2;

		charset = byteBuf.getByte(index++);

		serverStatus = new byte[2];
		byteBuf.getBytes(index, serverStatus, 0, 2);
		index += 2;

		serverProp2 = new byte[2];
		byteBuf.getBytes(index, serverProp2, 0, 2);
		index += 2;

		fix2 = byteBuf.getByte(index++);

//		byte10 = new byte[10];
//		byteBuf.getBytes(index, byte10, 0, 10);
		index += 10;

		len = 0;
		while (byteBuf.getByte(index + len) != 0)
		{
			len++;
		}
		encrypt2 = new byte[len];
		byteBuf.getBytes(index, encrypt2, 0, len);
		index += len;
		index++;
		
		
		len = 0;
		while (byteBuf.getByte(index + len) != 0)
		{
			len++;
		}
		authPluginName = new byte[len];
		byteBuf.getBytes(index, authPluginName, 0, len);
		index += len;
		index++;
		
		byteBuf.readerIndex(index);
		return this;
	}

	/**
	 * @param args
	 * @throws DecodeException
	 */
	public static void main(String[] args)
	{
		byte[] bs = new byte[] { 82, 0, 0, 0, 10, 49, 48, 46, 48, 46, 49, 45, 77, 97, 114, 105, 97, 68, 66, 0, -98, 1,
				0, 0, 110, 104, 61, 56, 64, 122, 101, 107, 0, -1, -9, 8, 2, 0, 15, -96, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 105, 78, 41, 35, 111, 43, 39, 124, 98, 82, 87, 60, 0, 109, 121, 115, 113, 108, 95, 110, 97, 116,
				105, 118, 101, 95, 112, 97, 115, 115, 119, 111, 114, 100, 0 };
		ByteBuf byteBuf = Unpooled.buffer(bs.length);
		byteBuf = byteBuf.order(ByteOrder.LITTLE_ENDIAN);

		byteBuf.setBytes(0, bs);

		HandshakePacket handshakePacket = new HandshakePacket();
		try
		{
			handshakePacket.decode(byteBuf);
			byteBuf.readerIndex(0);
			
			handshakePacket.decode(byteBuf);
			byteBuf.readerIndex(0);
			
			handshakePacket.decode(byteBuf);
			byteBuf.readerIndex(0);
			
			handshakePacket.decode(byteBuf);
			byteBuf.readerIndex(0);
		} catch (DecodeException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public String getSeqNo()
	{
		return null;
	}

	public byte getFix1()
	{
		return fix1;
	}

	public void setFix1(byte fix1)
	{
		this.fix1 = fix1;
	}

	public byte getCharset()
	{
		return charset;
	}

	public void setCharset(byte charset)
	{
		this.charset = charset;
	}

	public byte getFix2()
	{
		return fix2;
	}

	public void setFix2(byte fix2)
	{
		this.fix2 = fix2;
	}

	public byte[] getByte10()
	{
		return byte10;
	}

	public void setByte10(byte[] byte10)
	{
		this.byte10 = byte10;
	}

	public byte getProtocolVersion()
	{
		return protocolVersion;
	}

	public void setProtocolVersion(byte protocolVersion)
	{
		this.protocolVersion = protocolVersion;
	}

	public long getThreadId()
	{
		return threadId;
	}

	public void setThreadId(long threadId)
	{
		this.threadId = threadId;
	}

	public byte[] getVersionInfo()
	{
		return versionInfo;
	}

	public void setVersionInfo(byte[] versionInfo)
	{
		this.versionInfo = versionInfo;
	}

	public byte[] getServerProp1()
	{
		return serverProp1;
	}

	public void setServerProp1(byte[] serverProp1)
	{
		this.serverProp1 = serverProp1;
	}

}