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
package com.talent.mysql.packet.request;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.conf.BackendConf;
import com.talent.balance.conf.BackendServerConf;
import com.talent.mysql.packet.MysqlRequestPacket;
import com.talent.mysql.packet.factory.MysqlHeaderFactory;
import com.talent.mysql.packet.factory.MysqlHeaderFactory.MysqlHeader;
import com.talent.mysql.packet.response.HandshakePacket;
import com.talent.mysql.utils.Capabilities;
import com.talent.mysql.utils.SecurityUtil;
import com.talent.nio.communicate.intf.DecoderIntf.DecodeException;

/**
 * 
	40 00 00                    //数据长度，3字节，0x40=64字节<br>
	01                          //序号，1字节，同一个动作的所有请求与响应会递增此值<br>
	
	8D A6 03 00                 //客户端支持的属性，4字节，枚举参见网站<br>
	FF FF FF 00                 //最大数据包长度，4字节，0xffffff=16777215=约16MB<br>
	21                          //字符集，1字节，0x21=33=utf8<br>
	00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00   //固定填充0，23字节<br>
	74 65 73 74 00              //用户名，\0结尾的字符串，内容为test<br>
	14                          //密码串长度，1字节，0x14=20字节<br>
	B4 2F BB 65 7A D4 55 BA 9E E4 4B 34 A3 2C F6 58 92 7A A7 A2   //密码的加密串，20字节(算法见后)<br>
	76 6D 6E 70 6E 00           //初始数据库，\0结尾的字符串，内容为vmnpn<br>
 * 
 * @see  http://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::HandshakeResponse
 * @filename:	 com.talent.mysql.packet.request.AuthPacket
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月26日 下午5:19:13
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月26日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class AuthPacket extends MysqlRequestPacket
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -474477733084620969L;

	private static final long CLIENT_FLAGS = initClientFlags();

	private static Logger log = LoggerFactory.getLogger(AuthPacket.class);

	public long clientFlags = CLIENT_FLAGS; //172939;//
	public long maxPacketSize = 15 * 1024 * 1024;  //1073741824;//
	public byte charsetIndex;
	public byte[] extra = new byte[23]; //固定填充0，23字节<br>
	public byte[] user;
	public byte passwordLen;
	public byte[] password;
	public byte[] database;

	private static long initClientFlags()
	{
		long flag = 0;
		flag |= Capabilities.CLIENT_LONG_PASSWORD;
		flag |= Capabilities.CLIENT_FOUND_ROWS;
		flag |= Capabilities.CLIENT_LONG_FLAG;
		flag |= Capabilities.CLIENT_CONNECT_WITH_DB;
		// flag |= Capabilities.CLIENT_NO_SCHEMA;
		// flag |= Capabilities.CLIENT_COMPRESS;
		flag |= Capabilities.CLIENT_ODBC;
		// flag |= Capabilities.CLIENT_LOCAL_FILES;
		flag |= Capabilities.CLIENT_IGNORE_SPACE;
		flag |= Capabilities.CLIENT_PROTOCOL_41;
		flag |= Capabilities.CLIENT_INTERACTIVE;
		// flag |= Capabilities.CLIENT_SSL;
		flag |= Capabilities.CLIENT_IGNORE_SIGPIPE;
		flag |= Capabilities.CLIENT_TRANSACTIONS;
		// flag |= Capabilities.CLIENT_RESERVED;
		flag |= Capabilities.CLIENT_SECURE_CONNECTION;
		// client extension
		// flag |= Capabilities.CLIENT_MULTI_STATEMENTS;
		// flag |= Capabilities.CLIENT_MULTI_RESULTS;
		return flag;
	}

	public int bodyLenth()
	{
		//		return 4 + 4 + 1 + 23 + user.length + 1 + 1 + 20 + database.length + 1;
		return 55 + user.length + database.length;
	}

	/**
	 * 
	 */
	public AuthPacket()
	{

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		AuthPacket authPacket1 = new AuthPacket();
		authPacket1.decodeBody(null);

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
		} catch (DecodeException e)
		{
			e.printStackTrace();
		}

		BackendConf backendConf = BackendConf.getInstance();
		BackendServerConf backendServerConf = backendConf.getServers()[0];

		AuthPacket authPacket = new AuthPacket();
		authPacket.charsetIndex = (byte) (handshakePacket.charset & 0xff);
		authPacket.user = backendServerConf.getProps().get("user").getBytes();
		try
		{
			authPacket.password = getPass(backendServerConf.getProps().get("pwd"), handshakePacket);
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		authPacket.passwordLen = (byte) authPacket.password.length;
		authPacket.database = backendServerConf.getProps().get("db").getBytes();
		ByteBuf byteBuf1 = authPacket.encode();
		System.out.println(Arrays.toString(byteBuf1.array()));

	}

	public static final byte[] getPass(String pass, HandshakePacket hs) throws NoSuchAlgorithmException
	{
		if (pass == null || pass.length() == 0)
		{
			return null;
		}
		byte[] passwd = pass.getBytes();
		int sl1 = hs.encrypt1.length;
		int sl2 = hs.encrypt2.length;
		byte[] seed = new byte[sl1 + sl2];
		System.arraycopy(hs.encrypt1, 0, seed, 0, sl1);
		System.arraycopy(hs.encrypt2, 0, seed, sl1, sl2);
		return SecurityUtil.scramble411(passwd, seed);

		//		if (src == null || src.length() == 0)
		//		{
		//			return null;
		//		}
		//		byte[] passwd = src.getBytes();
		//		int sl1 = handshakePacket.encrypt1.length;
		//		int sl2 = handshakePacket.authPluginName.length;
		//		byte[] seed = new byte[sl1 + sl2];
		//		System.arraycopy(handshakePacket.encrypt1, 0, seed, 0, sl1);
		//		System.arraycopy(handshakePacket.authPluginName, 0, seed, sl1, sl2);
		//		return SecurityUtil.scramble411(passwd, seed);
	}

	@Override
	public void encodeBody(ByteBuf byteBuf)
	{
		int index = byteBuf.readerIndex();
		String xx = Long.toBinaryString(clientFlags);
		byteBuf.setLong(index, clientFlags);
		index += 4;

		byteBuf.setLong(index, maxPacketSize);
		index += 4;

		byteBuf.setByte(index, charsetIndex);
		index++;

		byteBuf.setBytes(index, extra);
		index += extra.length;

		byteBuf.setBytes(index, user);
		index += user.length;

		byteBuf.setByte(index, 0);
		index++;

		byteBuf.setByte(index, passwordLen);
		index++;

		byteBuf.setBytes(index, password);
		index += password.length;

		byteBuf.setBytes(index, database);
		index += database.length;

		byteBuf.setByte(index, 0);
		index++;
	}

	public void decodeBody(ByteBuf _byteBuf)
	{
		byte[] bs = new byte[] { -117, -93, 2, 0, 0, 0, 0, 64, 33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 114, 111, 111, 116, 0, 20, -19, -111, -3, 39, -46, -116, -128, -44, -112,
				-26, -48, 42, 70, -85, 8, 83, 83, 100, 103, 68, 116, 97, 108, 101, 110, 116, 95, 98, 97, 115, 101, 119,
				101, 98, 50, 48, 49, 0 };
		ByteBuf byteBuf = Unpooled.buffer(bs.length);
		byteBuf = byteBuf.order(ByteOrder.LITTLE_ENDIAN);
		byteBuf.setBytes(0, bs, 0, bs.length);
		
		int _index = byteBuf.readerIndex();
		int index = _index;

		clientFlags = byteBuf.getInt(index);  //172939
		index += 4;
		
		maxPacketSize = byteBuf.getInt(index);  //1073741824
		index += 4;
		
		charsetIndex = byteBuf.getByte(index);  //33
		index += 1;
		
		index += extra.length;
		
		
		int len = 0;
		while (byteBuf.getByte(index+len) != 0)
		{
			len++;
		}
		user = new byte[len];
		byteBuf.getBytes(index, user, 0, len);
		index += len;
		index++;
		
		passwordLen = byteBuf.getByte(index);
		index += 1;
		
		password = new byte[passwordLen];
		byteBuf.getBytes(index, password, 0, passwordLen);
		
		
		len = 0;
		while (byteBuf.getByte(index+len) != 0)
		{
			len++;
		}
		database = new byte[len];
		byteBuf.getBytes(index, database, 0, len);
		index += len;
		index++;
		
	}

	@Override
	public MysqlHeader createHeader()
	{
		MysqlHeader mysqlHeader = MysqlHeaderFactory.borrow(bodyLenth(), (byte) 1);
		return mysqlHeader;
	}

}