/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.HttpHeaderDateFormat;
import io.netty.util.internal.StringUtil;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class CookieDecoder {
    private static final char COMMA = ',';

    public static Set<Cookie> decode(String header) {
        int i;
        ArrayList<String> names = new ArrayList<String>(8);
        ArrayList<String> values = new ArrayList<String>(8);
        CookieDecoder.extractKeyValuePairs(header, names, values);
        if (names.isEmpty()) {
            return Collections.emptySet();
        }
        int version = 0;
        if (((String)names.get(0)).equalsIgnoreCase("Version")) {
            try {
                version = Integer.parseInt((String)values.get(0));
            }
            catch (NumberFormatException e) {
                // empty catch block
            }
            i = 1;
        } else {
            i = 0;
        }
        if (names.size() <= i) {
            return Collections.emptySet();
        }
        TreeSet<Cookie> cookies = new TreeSet<Cookie>();
        while (i < names.size()) {
            String name = (String)names.get(i);
            String value = (String)values.get(i);
            if (value == null) {
                value = "";
            }
            DefaultCookie c = new DefaultCookie(name, value);
            boolean discard = false;
            boolean secure = false;
            boolean httpOnly = false;
            String comment = null;
            String commentURL = null;
            String domain = null;
            String path = null;
            long maxAge = Long.MIN_VALUE;
            ArrayList<Integer> ports = new ArrayList<Integer>(2);
            int j = i + 1;
            while (j < names.size()) {
                name = (String)names.get(j);
                value = (String)values.get(j);
                if ("Discard".equalsIgnoreCase(name)) {
                    discard = true;
                } else if ("Secure".equalsIgnoreCase(name)) {
                    secure = true;
                } else if ("HTTPOnly".equalsIgnoreCase(name)) {
                    httpOnly = true;
                } else if ("Comment".equalsIgnoreCase(name)) {
                    comment = value;
                } else if ("CommentURL".equalsIgnoreCase(name)) {
                    commentURL = value;
                } else if ("Domain".equalsIgnoreCase(name)) {
                    domain = value;
                } else if ("Path".equalsIgnoreCase(name)) {
                    path = value;
                } else if ("Expires".equalsIgnoreCase(name)) {
                    try {
                        long maxAgeMillis = HttpHeaderDateFormat.get().parse(value).getTime() - System.currentTimeMillis();
                        maxAge = maxAgeMillis / 1000L + (long)(maxAgeMillis % 1000L != 0L ? 1 : 0);
                    }
                    catch (ParseException e) {}
                } else if ("Max-Age".equalsIgnoreCase(name)) {
                    maxAge = Integer.parseInt(value);
                } else if ("Version".equalsIgnoreCase(name)) {
                    version = Integer.parseInt(value);
                } else {
                    String[] portList;
                    if (!"Port".equalsIgnoreCase(name)) break;
                    for (String s1 : portList = StringUtil.split(value, ',')) {
                        try {
                            ports.add(Integer.valueOf(s1));
                        }
                        catch (NumberFormatException e) {
                            // empty catch block
                        }
                    }
                }
                ++j;
                ++i;
            }
            c.setVersion(version);
            c.setMaxAge(maxAge);
            c.setPath(path);
            c.setDomain(domain);
            c.setSecure(secure);
            c.setHttpOnly(httpOnly);
            if (version > 0) {
                c.setComment(comment);
            }
            if (version > 1) {
                c.setCommentUrl(commentURL);
                c.setPorts(ports);
                c.setDiscard(discard);
            }
            cookies.add(c);
            ++i;
        }
        return cookies;
    }

    private static void extractKeyValuePairs(String header, List<String> names, List<String> values) {
        int headerLen = header.length();
        int i = 0;
        block10: while (i != headerLen) {
            switch (header.charAt(i)) {
                case '\t': 
                case '\n': 
                case '\u000b': 
                case '\f': 
                case '\r': 
                case ' ': 
                case ',': 
                case ';': {
                    ++i;
                    continue block10;
                }
            }
            while (i != headerLen) {
                String value;
                String name;
                if (header.charAt(i) == '$') {
                    ++i;
                    continue;
                }
                if (i == headerLen) {
                    name = null;
                    value = null;
                } else {
                    int newNameStart = i;
                    block12: while (true) {
                        switch (header.charAt(i)) {
                            case ';': {
                                name = header.substring(newNameStart, i);
                                value = null;
                                break block12;
                            }
                            case '=': {
                                name = header.substring(newNameStart, i);
                                if (++i == headerLen) {
                                    value = "";
                                    break block12;
                                }
                                int newValueStart = i;
                                char c = header.charAt(i);
                                if (c == '\"' || c == '\'') {
                                    StringBuilder newValueBuf = new StringBuilder(header.length() - i);
                                    char q = c;
                                    boolean hadBackslash = false;
                                    ++i;
                                    block13: while (true) {
                                        if (i == headerLen) {
                                            value = newValueBuf.toString();
                                            break block12;
                                        }
                                        if (hadBackslash) {
                                            hadBackslash = false;
                                            c = header.charAt(i++);
                                            switch (c) {
                                                case '\"': 
                                                case '\'': 
                                                case '\\': {
                                                    newValueBuf.setCharAt(newValueBuf.length() - 1, c);
                                                    continue block13;
                                                }
                                            }
                                            newValueBuf.append(c);
                                            continue;
                                        }
                                        if ((c = header.charAt(i++)) == q) {
                                            value = newValueBuf.toString();
                                            break block12;
                                        }
                                        newValueBuf.append(c);
                                        if (c != '\\') continue;
                                        hadBackslash = true;
                                    }
                                }
                                int semiPos = header.indexOf(59, i);
                                if (semiPos > 0) {
                                    value = header.substring(newValueStart, semiPos);
                                    i = semiPos;
                                    break block12;
                                }
                                value = header.substring(newValueStart);
                                i = headerLen;
                                break block12;
                            }
                            default: {
                                if (++i != headerLen) continue block12;
                                name = header.substring(newNameStart);
                                value = null;
                                break block12;
                            }
                        }
                        break;
                    }
                }
                names.add(name);
                values.add(value);
                continue block10;
            }
            break block10;
        }
    }

    private CookieDecoder() {
    }
}

