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
package com.talent.nio.connmgr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.ChannelContext;

/**
 * 
 * @author 谭耀武
 * @date 2011-12-28
 * 
 */
public class ConnectionManager
{
	private static final Logger log = LoggerFactory.getLogger(ConnectionManager.class);

    private static Map<String, List<ChannelContext>> mapOfProtocolAndChannelContexts = new ConcurrentHashMap<String, List<ChannelContext>>();

    private static ConnectionManager instance = null;

    public static ConnectionManager getInstance()
    {
        if (instance == null)
        {
            synchronized (ConnectionManager.class)
            {
                if (instance == null)
                {
                    instance = new ConnectionManager();
                }
            }
        }
        return instance;
    }

    /**
     * 
     */
    private ConnectionManager()
    {

    }

    /**
     * 
     */
    public void addConnection(ChannelContext channelContext)
    {
        List<ChannelContext> set = mapOfProtocolAndChannelContexts.get(channelContext.getProtocol());
        if (set == null)
        {
            synchronized (mapOfProtocolAndChannelContexts)
            {
                if (set == null)
                {
                    set = Collections.synchronizedList(new ArrayList<ChannelContext>());
                    mapOfProtocolAndChannelContexts.put(channelContext.getProtocol(), set);
                }
            }
        }
        set.add(channelContext);
    }

    public void addConnections(ChannelContext[] channelContexts)
    {
        for (ChannelContext channelContext : channelContexts)
        {
            addConnection(channelContext);
        }
    }

    public void addConnections(Set<ChannelContext> channelContexts)
    {
        for (ChannelContext channelContext : channelContexts)
        {
            addConnection(channelContext);
        }
    }

    public void removeConnection(ChannelContext channelContext)
    {
        List<ChannelContext> set = mapOfProtocolAndChannelContexts.get(channelContext.getProtocol());

        if(set != null) {
        	log.warn("remove channelContext:{}", channelContext.getId());
        	set.remove(channelContext);
        }

    }

//    public void removeConnections(ChannelContext[] channelContexts)
//    {
//        for (ChannelContext channelContext : channelContexts)
//        {
//            removeConnection(channelContext);
//        }
//
//    }
//
//    public void removeConnections(Set<ChannelContext> channelContexts)
//    {
//        for (ChannelContext channelContext : channelContexts)
//        {
//            removeConnection(channelContext);
//        }
//
//    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    public List<ChannelContext> getConnectionsByProtocol(String protocol)
    {
        List<ChannelContext> set = mapOfProtocolAndChannelContexts.get(protocol);
        return set;
    }

    public Collection<ChannelContext> getConnections()
    {
        Set<Entry<String, List<ChannelContext>>> set = mapOfProtocolAndChannelContexts.entrySet();
        Set<ChannelContext> ret = new HashSet<ChannelContext>();

        for (Entry<String, List<ChannelContext>> entry : set)
        {
            ret.addAll(entry.getValue());
        }
        return ret;
    }
}