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
package com.talent.nio.startup;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.api.Config;
import com.talent.nio.api.Nio;
import com.talent.nio.communicate.receive.DecodeRunnable;
import com.talent.nio.communicate.receive.TcpListener;
import com.talent.nio.communicate.send.PacketSender;
import com.talent.nio.communicate.server.ServerContext;
import com.talent.platform.threadpool.SynThreadPoolExecutor;
import com.talent.platform.threadpool.intf.SynRunnableIntf;

/**
 * 
 * 
 * @filename: com.talent.nio.startup.Startup
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-9-20 下午6:52:41
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
public class Startup
{
    private static final Logger log = LoggerFactory.getLogger(Startup.class);

    private static Selector selector = null;

    private static Startup instance = null;

    private static boolean isInited = false;

    /**
     * 
     */
    private Startup()
    {

    }

    /**
     * 
     * @param applicationContext
     * @return
     */
    public static Startup getInstance()
    {
        if (instance == null)
        {
            synchronized (Startup.class)
            {
                if (instance == null)
                {
                    instance = new Startup();
                }
            }
        }

        return instance;
    }

    /**
     * 网络消息监听者
     */
    private static TcpListener tcpListener = null;

    private void init()
    {
        if (isInited)
        {
            return;
        }
        synchronized (Startup.class)
        {
            if (isInited)
            {
                return;
            }

            int threadPoolInitNum = (int) Config.getInstance().getThreadPoolInitNum();

            SynchronousQueue<Runnable> runnableQueue = new SynchronousQueue<Runnable>();
            SynThreadPoolExecutor<SynRunnableIntf> synThreadPoolExecutor = new SynThreadPoolExecutor<SynRunnableIntf>(threadPoolInitNum, (int) Config
                    .getInstance().getThreadPoolMaxNum(), Config.getInstance().getThreadPoolKeepAliveTime(), TimeUnit.SECONDS, runnableQueue,
                    "talent-nio");
            synThreadPoolExecutor.prestartAllCoreThreads();

            PacketSender.init(synThreadPoolExecutor);
            DecodeRunnable.init(synThreadPoolExecutor);

            // 启动监听线程
            try
            {
                startListenThread(synThreadPoolExecutor);
            } catch (IOException e)
            {
                log.error("", e);
                throw new RuntimeException(e);
            }
            isInited = true;
        }

    }

    public void startClient()
    {
        init();
    }

    public void startServer(ServerContext serverContext) throws IOException
    {
        init();
        try
        {
        	Nio.getInstance().acceptAt(serverContext);
        } catch (IOException e)
        {
            throw e;
        }
    }

    /**
     * 启动线程来监听消息
     * 
     * @author tanyaowu
     * @throws IOException
     */
    private static void startListenThread(SynThreadPoolExecutor<SynRunnableIntf> synThreadPoolExecutor) throws IOException
    {
        if (selector == null)
        {
            try
            {
                selector = Selector.open();
            } catch (IOException e)
            {
                throw e;
            }
        }

        tcpListener = new TcpListener(selector, synThreadPoolExecutor);
        tcpListener.listen();
    }

    public static TcpListener getTcpListener()
    {
        return tcpListener;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println((int)'\r');
        System.out.println((int)'\n');

    }

    public static Selector getSelector()
    {
        return selector;
    }

}