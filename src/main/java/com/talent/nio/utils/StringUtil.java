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

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * 
 * 
 * @filename: com.talent.nio.utils.StringUtil
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-9-20 下午6:53:35
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
public final class StringUtil
{

    private StringUtil()
    {
        // Unused.
    }

    public static final String NEWLINE;

    static
    {
        String newLine;

        try
        {
            newLine = new Formatter().format("%n").toString();
        } catch (Exception e)
        {
            newLine = "\n";
        }

        NEWLINE = newLine;
    }

    /**
     * Strip an Object of it's ISO control characters.
     * 
     * @param value
     *            The Object that should be stripped. This objects toString
     *            method will called and the result passed to
     *            {@link #stripControlCharacters(String)}.
     * @return {@code String} A new String instance with its hexadecimal control
     *         characters replaced by a space. Or the unmodified String if it
     *         does not contain any ISO control characters.
     */
    public static String stripControlCharacters(Object value)
    {
        if (value == null)
        {
            return null;
        }

        return stripControlCharacters(value.toString());
    }

    /**
     * Strip a String of it's ISO control characters.
     * 
     * @param value
     *            The String that should be stripped.
     * @return {@code String} A new String instance with its hexadecimal control
     *         characters replaced by a space. Or the unmodified String if it
     *         does not contain any ISO control characters.
     */
    public static String stripControlCharacters(String value)
    {
        if (value == null)
        {
            return null;
        }

        boolean hasControlChars = false;
        for (int i = value.length() - 1; i >= 0; i--)
        {
            if (Character.isISOControl(value.charAt(i)))
            {
                hasControlChars = true;
                break;
            }
        }

        if (!hasControlChars)
        {
            return value;
        }

        StringBuilder buf = new StringBuilder(value.length());
        int i = 0;

        // Skip initial control characters (i.e. left trim)
        for (; i < value.length(); i++)
        {
            if (!Character.isISOControl(value.charAt(i)))
            {
                break;
            }
        }

        // Copy non control characters and substitute control characters with
        // a space. The last control characters are trimmed.
        boolean suppressingControlChars = false;
        for (; i < value.length(); i++)
        {
            if (Character.isISOControl(value.charAt(i)))
            {
                suppressingControlChars = true;
                continue;
            } else
            {
                if (suppressingControlChars)
                {
                    suppressingControlChars = false;
                    buf.append(' ');
                }
                buf.append(value.charAt(i));
            }
        }

        return buf.toString();
    }

    private static final String EMPTY_STRING = "";

    /**
     * Splits the specified {@link String} with the specified delimiter. This
     * operation is a simplified and optimized version of
     * {@link String#split(String)}.
     */
    public static String[] split(String value, char delim)
    {
        final int end = value.length();
        final List<String> res = new ArrayList<String>();

        int start = 0;
        for (int i = 0; i < end; i++)
        {
            if (value.charAt(i) == delim)
            {
                if (start == i)
                {
                    res.add(EMPTY_STRING);
                } else
                {
                    res.add(value.substring(start, i));
                }
                start = i + 1;
            }
        }

        if (start == 0)
        { // If no delimiter was found in the value
            res.add(value);
        } else
        {
            if (start != end)
            {
                // Add the last element if it's not empty.
                res.add(value.substring(start, end));
            } else
            {
                // Truncate trailing empty elements.
                for (int i = res.size() - 1; i >= 0; i--)
                {
                    if (res.get(i).length() == 0)
                    {
                        res.remove(i);
                    } else
                    {
                        break;
                    }
                }
            }
        }

        return res.toArray(new String[res.size()]);
    }

    public static void main(String[] args)
    {
        Formatter formatter =  new Formatter();
        String xx = formatter.format("%n").toString();
        formatter.close();
    }
}