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
package com.talent.nio.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @filename: com.talent.platform.core.AbstractPageable
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2010-4-13 上午10:25:14
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
 *         <td>2010-4-13</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public abstract class AbstractPageable implements Pageable
{
    private static Logger log = LoggerFactory.getLogger(AbstractPageable.class);
    protected Object data = null;
    // protected long pageCount = 0;
    protected long recordCount = 0L;
    // protected long sufPageCount = 0;
    // protected long prePageCount = 0;
    protected long pageIndex = DEFAULT_PAGEINDEX;
    protected int pageSize = DEFAULT_PAGESIZE;
    protected boolean isPagination = true;

    /*
     * (non-Javadoc)
     * 
     * @see com.talent.platform.core.Pageable#getPageIndex()
     */
    @Override
    public long getPageIndex()
    {
        return this.pageIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.talent.platform.core.Pageable#getPageSize()
     */
    @Override
    public int getPageSize()
    {
        return this.pageSize;
    }

    // /**
    // * calculate page count in previous
    // * @param pageIndex
    // * @return
    // */
    // protected static long calculatePrePageCount(long pageIndex) {
    // long ret = pageIndex - 1L;
    // ret = ret < 0 ? 0 : ret;
    // return ret;
    // }

    // /**
    // * calculate page count in rear(suffix) (计算后面还有多少页)
    // * @param pageCount
    // * @param pageIndex
    // * @return
    // */
    // protected static long calculateSufPageCount(long pageCount, long
    // pageIndex) {
    // long ret = pageCount - pageIndex;
    // ret = ret < 0 ? 0 : ret;
    // return ret;
    // }

    /**
     * calculate page count(计算总页数)
     * 
     * @param pageSize
     * @param recoreCount
     * @return
     */
    protected static long calculatePageCount(int pageSize, long recoreCount)
    {
        if (pageSize == Integer.MAX_VALUE)
        {
            log.debug("pagesize = Integer.MAX_VALUE");
            if (recoreCount >= 1)
            {
                return 1;
            } else
            {
                return 0;
            }
        } else
        {
            return (recoreCount + pageSize - 1) / pageSize;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.talent.platform.core.Pageable#setPageSize(int)
     */
    @Override
    public void setPageSize(int pageSize)
    {
        if (!isPagination)
        {
            log.warn("isPagination has been set to false, so pageSize is not effective.");
            this.pageSize = Integer.MAX_VALUE;
        } else
        {
            this.pageSize = processPageSize(pageSize);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.talent.platform.core.Pageable#setPageIndex(int)
     */
    @Override
    public void setPageIndex(int pageIndex)
    {
        if (!isPagination)
        {
            log.warn("isPagination has been set to false, so pageIndex is not effective.");
            this.pageIndex = 0;
        } else
        {
            this.pageIndex = processPageIndex(pageIndex);
        }

    }

    protected static int processPageSize(int pageSize)
    {
        return pageSize <= 0 ? Integer.MAX_VALUE : pageSize;
    }

    protected static int processPageIndex(int pageIndex)
    {
        return pageIndex <= 0 ? 1 : pageIndex;
    }

    /**
     * @return the data
     */
    @Override
    public Object getData()
    {
        return data;
    }

    // /**
    // * @return the pageCount
    // */
    // public long getPageCount() {
    // return pageCount;
    // }

    /**
     * @return the recordCount
     */
    @Override
    public long getRecordCount()
    {
        return recordCount;
    }

    //
    // /**
    // * @return the sufPageCount
    // */
    // public long getSufPageCount() {
    // return sufPageCount;
    // }
    //
    // /**
    // * @return the prePageCount
    // */
    // public long getPrePageCount() {
    // return prePageCount;
    // }

    /**
     * @return the isPagination
     */
    @Override
    public boolean isPagination()
    {
        return isPagination;
    }

    /**
     * @param isPagination
     *            the isPagination to set
     */
    @Override
    public void setPagination(boolean isPagination)
    {
        this.isPagination = isPagination;
        if (!isPagination)
        {
            this.pageIndex = 0;
            this.pageSize = Integer.MAX_VALUE;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }
}