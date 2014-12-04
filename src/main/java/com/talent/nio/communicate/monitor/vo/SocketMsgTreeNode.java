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
package com.talent.nio.communicate.monitor.vo;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
/**
 * 
 * @filename:	 com.talent.nio.communicate.monitor.vo.SocketMsgTreeNode
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年9月30日 下午1:50:00
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年9月30日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class SocketMsgTreeNode implements java.io.Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 455121651L;
    public static final int NODE_TYPE = 0;
    public static final int CONNECTION_TYPE = 1;
    /**
     * 
     */
    public static final int HANDLER_TYPE = 2;

    private String name; // 名字
    private int type; // 节点类型
    private long processedMsgCount = -1; // 分别针对节点，协议，HANDLER
    private long sentMsgCount = -1; // 分别针对节点，协议
    private long waitingForSentMsgCount = -1; // 分别针对协议
    private long waitingForProcessMsgCount = -1; // 分别针对协议， HANDLER
    private long receivedSize = -1; // 已经接收到的消息量(单位：字节)
    private long sentSize = -1; // 已经发送的消息量(单位：字节)
    private String time = null; // 统计时间
    private boolean isNeedRecordSendFailMsg = false;

    private SocketMsgTreeNode parent = null;
    private ConcurrentLinkedQueue<PacketVo> sendFailQueue = null;

    private SocketMsgTreeNode[] children;

    public static SocketMsgTreeNode createNodeNode(String nodeName, SocketMsgTreeNode[] socketChannelIdChildren)
    {
        SocketMsgTreeNode ret = new SocketMsgTreeNode();
        ret.name = nodeName;
        ret.setChildren(socketChannelIdChildren);
        ret.type = NODE_TYPE;
        return ret;
    }

    public static SocketMsgTreeNode createNodeNode(String nodeName, SocketMsgTreeNode parent)
    {
        SocketMsgTreeNode ret = new SocketMsgTreeNode();
        ret.name = nodeName;
        ret.setParent(parent);
        ret.type = NODE_TYPE;
        return ret;
    }

    public static SocketMsgTreeNode createNodeNode(String socketChannelId, Set<SocketMsgTreeNode> children)
    {
        SocketMsgTreeNode[] childArray = new SocketMsgTreeNode[children.size()];
        return createNodeNode(socketChannelId, childArray);
    }

    public static SocketMsgTreeNode createConnectionNode(String socketChannelId, SocketMsgTreeNode parent)
    {
        SocketMsgTreeNode ret = new SocketMsgTreeNode();
        ret.name = socketChannelId;
        ret.setParent(parent);
        ret.type = CONNECTION_TYPE;
        return ret;
    }

    public static SocketMsgTreeNode createConnectionNode(String socketChannelId, SocketMsgTreeNode[] handlerChildren)
    {
        SocketMsgTreeNode ret = new SocketMsgTreeNode();
        ret.name = socketChannelId;
        ret.setChildren(handlerChildren);
        ret.type = CONNECTION_TYPE;
        return ret;
    }

    public static SocketMsgTreeNode createConnectionNode(String socketChannelId, Set<SocketMsgTreeNode> children)
    {
        SocketMsgTreeNode[] childArray = new SocketMsgTreeNode[children.size()];
        return createConnectionNode(socketChannelId, childArray);
    }

    public static SocketMsgTreeNode createHandlerNode(String handler, SocketMsgTreeNode parent)
    {
        SocketMsgTreeNode ret = new SocketMsgTreeNode();
        ret.name = handler;
        ret.parent = parent;
        ret.type = HANDLER_TYPE;
        return ret;
    }

    // Method descriptor #4 (I)Ljava/lang/Object;
    public java.lang.Object getChild(int index)
    {
        return children == null ? null : children[index];
    }

    // Method descriptor #6 ()I
    public int getChildCount()
    {
        return children == null ? 0 : children.length;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public long getProcessedMsgCount()
    {
        return processedMsgCount;
    }

    public void setProcessedMsgCount(long processedMsgCount)
    {
        this.processedMsgCount = processedMsgCount;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public SocketMsgTreeNode getParent()
    {
        return parent;
    }

    public void setParent(SocketMsgTreeNode parent)
    {
        this.parent = parent;
    }

    public SocketMsgTreeNode[] getChildren()
    {
        return children;
    }

    public void setChildren(SocketMsgTreeNode[] children)
    {
        this.children = children;
        if (children != null)
        {
            for (SocketMsgTreeNode socketMsgTreeNode : children)
            {
                if (socketMsgTreeNode != null)
                {
                    socketMsgTreeNode.parent = this;
                }
            }
        }
    }

    public void setWaitingForProcessMsgCount(long waitingForProcessMsgCount)
    {
        this.waitingForProcessMsgCount = waitingForProcessMsgCount;
    }

    public long getWaitingForProcessMsgCount()
    {
        return waitingForProcessMsgCount;
    }

    public void setSentMsgCount(long sentMsgCount)
    {
        this.sentMsgCount = sentMsgCount;
    }

    public long getSentMsgCount()
    {
        return sentMsgCount;
    }

    public void setWaitingForSentMsgCount(long waitingForSentMsgCount)
    {
        this.waitingForSentMsgCount = waitingForSentMsgCount;
    }

    public long getWaitingForSentMsgCount()
    {
        return waitingForSentMsgCount;
    }

    @Override
    public String toString()
    {
        return this.getName();
    }

    public long getReceivedSize()
    {
        return receivedSize;
    }

    public void setReceivedSize(long receivedSize)
    {
        this.receivedSize = receivedSize;
    }

    public void setSentSize(long sentSize)
    {
        this.sentSize = sentSize;
    }

    public long getSentSize()
    {
        return sentSize;
    }

    public void setSendFailQueue(ConcurrentLinkedQueue<PacketVo> sendFailQueue)
    {
        this.sendFailQueue = sendFailQueue;
    }

    public ConcurrentLinkedQueue<PacketVo> getSendFailQueue()
    {
        return sendFailQueue;
    }

    public void setNeedRecordSendFailMsg(boolean isNeedRecordSendFailMsg)
    {
        this.isNeedRecordSendFailMsg = isNeedRecordSendFailMsg;
    }

    public boolean isNeedRecordSendFailMsg()
    {
        return isNeedRecordSendFailMsg;
    }

}