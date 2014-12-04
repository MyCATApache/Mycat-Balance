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
package com.talent.nio.utils.clone;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * ByteArrayOutputStream implementation that doesn't synchronize methods and
 * doesn't copy the data on toByteArray().
 */
public class FastByteArrayOutputStream extends OutputStream
{
    /**
     * Buffer and size
     */
    protected byte[] buf = null;
    protected int size = 0;

    /**
     * Constructs a stream with buffer capacity size 5K
     */
    public FastByteArrayOutputStream()
    {
        this(5 * 1024);
    }

    /**
     * Constructs a stream with the given initial size
     */
    public FastByteArrayOutputStream(int initSize)
    {
        this.size = 0;
        this.buf = new byte[initSize];
    }

    /**
     * Ensures that we have a large enough buffer for the given size.
     */
    private void verifyBufferSize(int sz)
    {
        if (sz > buf.length)
        {
            byte[] old = buf;
            buf = new byte[Math.max(sz, 2 * buf.length)];
            System.arraycopy(old, 0, buf, 0, old.length);
            old = null;
        }
    }

    public int getSize()
    {
        return size;
    }

    /**
     * Returns the byte array containing the written data. Note that this array
     * will almost always be larger than the amount of data actually written.
     */
    public byte[] getByteArray()
    {
        return buf;
    }

    @Override
    public final void write(byte b[])
    {
        verifyBufferSize(size + b.length);
        System.arraycopy(b, 0, buf, size, b.length);
        size += b.length;
    }

    @Override
    public final void write(byte b[], int off, int len)
    {
        verifyBufferSize(size + len);
        System.arraycopy(b, off, buf, size, len);
        size += len;
    }

    @Override
    public final void write(int b)
    {
        verifyBufferSize(size + 1);
        buf[size++] = (byte) b;
    }

    public void reset()
    {
        size = 0;
    }

    /**
     * Returns a ByteArrayInputStream for reading back the written data
     */
    public InputStream getInputStream()
    {
        return new FastByteArrayInputStream(buf, size);
    }

}