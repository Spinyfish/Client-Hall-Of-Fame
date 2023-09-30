/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.misc;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String date(boolean year, boolean month, boolean day, boolean hour, boolean minute, boolean second, String separator) {
        return new SimpleDateFormat((year ? "yyyy" + separator : "") + (month ? "MM" + separator : "") + (day ? "dd" + separator : "") + (hour ? "hh" + separator : "") + (minute ? "mm" + separator : "") + (second ? "ss" + separator : "")).format(new Date());
    }

    public static String date(boolean year, boolean month, boolean day, boolean hour, boolean minute, boolean second, String separator, long time) {
        return new SimpleDateFormat((year ? "yyyy" + separator : "") + (month ? "MM" + separator : "") + (day ? "dd" + separator : "") + (hour ? "hh" + separator : "") + (minute ? "mm" + separator : "") + (second ? "ss" + separator : "")).format(new Date(time));
    }
}

