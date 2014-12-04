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
package com.talent.nio.communicate.util;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

/**
 * 
 * 用于nio的代理
 * 
 * @filename: com.talent.platform.nio.communicate.util.NioProxy
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2012-4-25 下午2:32:00
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
 *         <td>2012-4-25</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class NioProxy
{
    static final int PROTO_VERS = 5;
    static final int NO_AUTH = 0;
    static final int USER_PASSW = 2;
    static final int CONNECT = 1;
    static final int UDP_ASSOC = 3;
    static final int IPV4 = 1;
    static final int DOMAIN_NAME = 3;
    static final int IPV6 = 4;
    static final int REQUEST_OK = 0;
    static final int GENERAL_FAILURE = 1;
    static final int NOT_ALLOWED = 2;
    static final int NET_UNREACHABLE = 3;
    static final int HOST_UNREACHABLE = 4;
    static final int CONN_REFUSED = 5;
    static final int TTL_EXPIRED = 6;
    static final int CMD_NOT_SUPPORTED = 7;
    static final int ADDR_TYPE_NOT_SUP = 8;

    public static void proxyImpl(SocketChannel socketChannel, InetSocketAddress epoint) throws IOException, SocketException, ClosedChannelException
    {
        sendData(new byte[]
        { PROTO_VERS, 2, NO_AUTH, USER_PASSW }, socketChannel);

        byte[] data = new byte[2];
        int i = readSocksReply(socketChannel, data);
        sendData(new byte[]
        { (byte) PROTO_VERS, CONNECT, 0 }, socketChannel);
        if (epoint.isUnresolved())
        {
            sendData(new byte[]
            { DOMAIN_NAME, (byte) epoint.getHostName().length(), NO_AUTH, USER_PASSW }, socketChannel);
            try
            {
                sendData(epoint.getHostName().getBytes("ISO-8859-1"), socketChannel);
            } catch (java.io.UnsupportedEncodingException uee)
            {
                assert false;
            }
            sendData(new byte[]
            { (byte) ((epoint.getPort() >> 8) & 0xff), (byte) ((epoint.getPort() >> 0) & 0xff) }, socketChannel);
        } else if (epoint.getAddress() instanceof Inet6Address)
        {
            sendData(new byte[]
            { IPV6 }, socketChannel);
            sendData(epoint.getAddress().getAddress(), socketChannel);
            sendData(new byte[]
            { (byte) ((epoint.getPort() >> 8) & 0xff), (byte) ((epoint.getPort() >> 0) & 0xff) }, socketChannel);
        } else
        {
            sendData(new byte[]
            { IPV4 }, socketChannel);
            sendData(epoint.getAddress().getAddress(), socketChannel);
            sendData(new byte[]
            { (byte) ((epoint.getPort() >> 8) & 0xff), (byte) ((epoint.getPort() >> 0) & 0xff) }, socketChannel);
        }
        data = new byte[4];
        i = readSocksReply(socketChannel, data);
        if (i != 4)
            throw new SocketException("Reply from SOCKS server has bad length");
        SocketException ex = null;
        @SuppressWarnings("unused")
        int port = 0;
        int len;
        byte[] addr;
        switch (data[1])
        {
        case REQUEST_OK:
            // success!
            switch (data[3])
            {
            case IPV4:
                addr = new byte[4];
                i = readSocksReply(socketChannel, addr);
                if (i != 4)
                    throw new SocketException("Reply from SOCKS server badly formatted");
                data = new byte[2];
                i = readSocksReply(socketChannel, data);
                if (i != 2)
                    throw new SocketException("Reply from SOCKS server badly formatted");
                port = (data[0] & 0xff) << 8;
                port += (data[1] & 0xff);
                break;
            case DOMAIN_NAME:
                len = data[1];
                byte[] host = new byte[len];
                i = readSocksReply(socketChannel, host);
                if (i != len)
                    throw new SocketException("Reply from SOCKS server badly formatted");
                data = new byte[2];
                i = readSocksReply(socketChannel, data);
                if (i != 2)
                    throw new SocketException("Reply from SOCKS server badly formatted");
                port = (data[0] & 0xff) << 8;
                port += (data[1] & 0xff);
                break;
            case IPV6:
                len = data[1];
                addr = new byte[len];
                i = readSocksReply(socketChannel, addr);
                if (i != len)
                    throw new SocketException("Reply from SOCKS server badly formatted");
                data = new byte[2];
                i = readSocksReply(socketChannel, data);
                if (i != 2)
                    throw new SocketException("Reply from SOCKS server badly formatted");
                port = (data[0] & 0xff) << 8;
                port += (data[1] & 0xff);
                break;
            default:
                ex = new SocketException("Reply from SOCKS server contains wrong code");
                break;
            }
            break;
        case GENERAL_FAILURE:
            ex = new SocketException("SOCKS server general failure");
            break;
        case NOT_ALLOWED:
            ex = new SocketException("SOCKS: Connection not allowed by ruleset");
            break;
        case NET_UNREACHABLE:
            ex = new SocketException("SOCKS: Network unreachable");
            break;
        case HOST_UNREACHABLE:
            ex = new SocketException("SOCKS: Host unreachable");
            break;
        case CONN_REFUSED:
            ex = new SocketException("SOCKS: Connection refused");
            break;
        case TTL_EXPIRED:
            ex = new SocketException("SOCKS: TTL expired");
            break;
        case CMD_NOT_SUPPORTED:
            ex = new SocketException("SOCKS: Command not supported");
            break;
        case ADDR_TYPE_NOT_SUP:
            ex = new SocketException("SOCKS: address type not supported");
            break;
        }
        if (ex != null)
        {
            socketChannel.close();
            throw ex;
        }
    }

    private static boolean sendData(byte[] data, SocketChannel socketChannel) throws IOException
    {
        try
        {
            if (!socketChannel.isOpen())
            {
                throw new IOException("the socket channel is not open");
            }
            ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            socketChannel.write(dataBuffer);

            // 还有数据没写干净，一般是网络不好导致
            while (dataBuffer.hasRemaining() && socketChannel.isOpen())
            {
                Thread.sleep(100);
                socketChannel.write(dataBuffer);
            }

            return true;
        } catch (IOException e)
        {
            throw e;
        } catch (InterruptedException e)
        {
            return false;
        }
    }

    private static int readSocksReply(SocketChannel socketChannel, byte[] data) throws IOException
    {
        int len = data.length;
        int received = 0;
        ByteBuffer buf = ByteBuffer.allocate(data.length);
        for (int attempts = 0; received < len && attempts < 20; attempts++)
        {
            int count = socketChannel.read(buf);
            if (count < 0)
                throw new SocketException("Malformed reply from SOCKS server");
            received += count;
            try
            {
                if (received < len)
                    Thread.sleep(100);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        buf.flip();
        buf.get(data);
        return received;
    }
}