package org.crystalpvp.anticheat.api.util;

import java.util.Date;
import java.text.SimpleDateFormat;

public class TimeUtils
{


    public static long CurrentMS() {
        return System.currentTimeMillis();
    }

    public static boolean Passed(final long from, final long required) {
        return System.currentTimeMillis() - from > required;
    }

    public static boolean elapsed(final long time, final long needed) {
        return Math.abs(System.currentTimeMillis() - time) >= needed;
    }

    public static String getSystemTime() {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        final Date date = new Date(System.currentTimeMillis());
        final String out = formatter.format(date);
        return out;
    }

    public static long secondsFromLong(final long time) {
        final Long now = System.currentTimeMillis();
        final Long date = now - time;
        final long seconds = date / 1000L % 60L;
        return seconds;
    }

    public static String GetDate() {
        final Date now = new Date();
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return format.format(now);
    }

    public static long Remainder(final long start, final long required) {
        return required + start - System.currentTimeMillis();
    }

    public static long elapsed(final long time) {
        return System.currentTimeMillis() - time;
    }

}