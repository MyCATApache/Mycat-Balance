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
package com.talent.nio.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.utils.clone.FastByteArrayOutputStream;

/**
 * 
 * @author 谭耀武
 * @date 2013-1-23
 * 
 */
public class CloneUtils
{
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CloneUtils.class);

    /**
	 * 
	 */
    public CloneUtils()
    {
    }

    /**
     * 
     * @author tanyaowu
     * @param srcObject
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static final Object deepClone(java.io.Serializable srcObject) throws IOException, ClassNotFoundException
    {
        FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(fbos);
        out.writeObject(srcObject);
        out.close();

        ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
        Object ret = in.readObject();
        in.close();
        return ret;

        // ByteArrayOutputStream bytearrayoutputstream = new
        // ByteArrayOutputStream(100);
        // ObjectOutputStream objectoutputstream = new
        // ObjectOutputStream(bytearrayoutputstream);
        // objectoutputstream.writeObject(srcObject);
        // byte abyte0[] = bytearrayoutputstream.toByteArray();
        // objectoutputstream.close();
        // ByteArrayInputStream bytearrayinputstream = new
        // ByteArrayInputStream(abyte0);
        // ObjectInputStream objectinputstream = new
        // ObjectInputStream(bytearrayinputstream);
        // Object clone = objectinputstream.readObject();
        // objectinputstream.close();
        // return clone;

    }

    /**
     * @author tanyaowu
     * @param args
     */
    public static void main(String[] args)
    {

    }
}