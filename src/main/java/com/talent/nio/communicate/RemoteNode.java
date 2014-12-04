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
package com.talent.nio.communicate;

/**
 * 远程节点的信息
 * 
 * @author 谭耀武
 * @date 2011-12-23
 * 
 */
public class RemoteNode
{
    private String ip;
    private int port;

    public RemoteNode(String ip, int port)
    {
        super();

        this.setIp(ip);
        this.setPort(port);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(ip).append(":").append(port);
        return builder.toString();
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException
    {

        java.lang.Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                // 在退出JVM时要做的事
                System.out.println("在退出JVM时要做的事");

            }
        });
        System.exit(0);
        Thread.sleep(1000000);

    }

}