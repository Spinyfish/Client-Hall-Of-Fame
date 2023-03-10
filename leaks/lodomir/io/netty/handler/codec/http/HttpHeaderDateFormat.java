/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.util.concurrent.FastThreadLocal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

final class HttpHeaderDateFormat
extends SimpleDateFormat {
    private static final long serialVersionUID = -925286159755905325L;
    private final SimpleDateFormat format1 = new HttpHeaderDateFormatObsolete1();
    private final SimpleDateFormat format2 = new HttpHeaderDateFormatObsolete2();
    private static final FastThreadLocal<HttpHeaderDateFormat> dateFormatThreadLocal = new FastThreadLocal<HttpHeaderDateFormat>(){

        @Override
        protected HttpHeaderDateFormat initialValue() {
            return new HttpHeaderDateFormat();
        }
    };

    static HttpHeaderDateFormat get() {
        return dateFormatThreadLocal.get();
    }

    private HttpHeaderDateFormat() {
        super("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        this.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Override
    public Date parse(String text, ParsePosition pos) {
        Date date = super.parse(text, pos);
        if (date == null) {
            date = this.format1.parse(text, pos);
        }
        if (date == null) {
            date = this.format2.parse(text, pos);
        }
        return date;
    }

    private static final class HttpHeaderDateFormatObsolete2
    extends SimpleDateFormat {
        private static final long serialVersionUID = 3010674519968303714L;

        HttpHeaderDateFormatObsolete2() {
            super("E MMM d HH:mm:ss yyyy", Locale.ENGLISH);
            this.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    }

    private static final class HttpHeaderDateFormatObsolete1
    extends SimpleDateFormat {
        private static final long serialVersionUID = -3178072504225114298L;

        HttpHeaderDateFormatObsolete1() {
            super("E, dd-MMM-yy HH:mm:ss z", Locale.ENGLISH);
            this.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    }
}

