/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SpdyHeaders
implements Iterable<Map.Entry<String, String>> {
    public static final SpdyHeaders EMPTY_HEADERS = new SpdyHeaders(){

        @Override
        public List<String> getAll(String name) {
            return Collections.emptyList();
        }

        @Override
        public List<Map.Entry<String, String>> entries() {
            return Collections.emptyList();
        }

        @Override
        public boolean contains(String name) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Set<String> names() {
            return Collections.emptySet();
        }

        @Override
        public SpdyHeaders add(String name, Object value) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public SpdyHeaders add(String name, Iterable<?> values) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public SpdyHeaders set(String name, Object value) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public SpdyHeaders set(String name, Iterable<?> values) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public SpdyHeaders remove(String name) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public SpdyHeaders clear() {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public Iterator<Map.Entry<String, String>> iterator() {
            return this.entries().iterator();
        }

        @Override
        public String get(String name) {
            return null;
        }
    };

    public static String getHeader(SpdyHeadersFrame frame, String name) {
        return frame.headers().get(name);
    }

    public static String getHeader(SpdyHeadersFrame frame, String name, String defaultValue) {
        String value = frame.headers().get(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static void setHeader(SpdyHeadersFrame frame, String name, Object value) {
        frame.headers().set(name, value);
    }

    public static void setHeader(SpdyHeadersFrame frame, String name, Iterable<?> values) {
        frame.headers().set(name, values);
    }

    public static void addHeader(SpdyHeadersFrame frame, String name, Object value) {
        frame.headers().add(name, value);
    }

    public static void removeHost(SpdyHeadersFrame frame) {
        frame.headers().remove(":host");
    }

    public static String getHost(SpdyHeadersFrame frame) {
        return frame.headers().get(":host");
    }

    public static void setHost(SpdyHeadersFrame frame, String host) {
        frame.headers().set(":host", host);
    }

    public static void removeMethod(int spdyVersion, SpdyHeadersFrame frame) {
        frame.headers().remove(":method");
    }

    public static HttpMethod getMethod(int spdyVersion, SpdyHeadersFrame frame) {
        try {
            return HttpMethod.valueOf(frame.headers().get(":method"));
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void setMethod(int spdyVersion, SpdyHeadersFrame frame, HttpMethod method) {
        frame.headers().set(":method", method.name());
    }

    public static void removeScheme(int spdyVersion, SpdyHeadersFrame frame) {
        frame.headers().remove(":scheme");
    }

    public static String getScheme(int spdyVersion, SpdyHeadersFrame frame) {
        return frame.headers().get(":scheme");
    }

    public static void setScheme(int spdyVersion, SpdyHeadersFrame frame, String scheme) {
        frame.headers().set(":scheme", scheme);
    }

    public static void removeStatus(int spdyVersion, SpdyHeadersFrame frame) {
        frame.headers().remove(":status");
    }

    public static HttpResponseStatus getStatus(int spdyVersion, SpdyHeadersFrame frame) {
        try {
            String status = frame.headers().get(":status");
            int space = status.indexOf(32);
            if (space == -1) {
                return HttpResponseStatus.valueOf(Integer.parseInt(status));
            }
            int code = Integer.parseInt(status.substring(0, space));
            String reasonPhrase = status.substring(space + 1);
            HttpResponseStatus responseStatus = HttpResponseStatus.valueOf(code);
            if (responseStatus.reasonPhrase().equals(reasonPhrase)) {
                return responseStatus;
            }
            return new HttpResponseStatus(code, reasonPhrase);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void setStatus(int spdyVersion, SpdyHeadersFrame frame, HttpResponseStatus status) {
        frame.headers().set(":status", status.toString());
    }

    public static void removeUrl(int spdyVersion, SpdyHeadersFrame frame) {
        frame.headers().remove(":path");
    }

    public static String getUrl(int spdyVersion, SpdyHeadersFrame frame) {
        return frame.headers().get(":path");
    }

    public static void setUrl(int spdyVersion, SpdyHeadersFrame frame, String path) {
        frame.headers().set(":path", path);
    }

    public static void removeVersion(int spdyVersion, SpdyHeadersFrame frame) {
        frame.headers().remove(":version");
    }

    public static HttpVersion getVersion(int spdyVersion, SpdyHeadersFrame frame) {
        try {
            return HttpVersion.valueOf(frame.headers().get(":version"));
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void setVersion(int spdyVersion, SpdyHeadersFrame frame, HttpVersion httpVersion) {
        frame.headers().set(":version", httpVersion.text());
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return this.entries().iterator();
    }

    public abstract String get(String var1);

    public abstract List<String> getAll(String var1);

    public abstract List<Map.Entry<String, String>> entries();

    public abstract boolean contains(String var1);

    public abstract Set<String> names();

    public abstract SpdyHeaders add(String var1, Object var2);

    public abstract SpdyHeaders add(String var1, Iterable<?> var2);

    public abstract SpdyHeaders set(String var1, Object var2);

    public abstract SpdyHeaders set(String var1, Iterable<?> var2);

    public abstract SpdyHeaders remove(String var1);

    public abstract SpdyHeaders clear();

    public abstract boolean isEmpty();

    public static final class HttpNames {
        public static final String HOST = ":host";
        public static final String METHOD = ":method";
        public static final String PATH = ":path";
        public static final String SCHEME = ":scheme";
        public static final String STATUS = ":status";
        public static final String VERSION = ":version";

        private HttpNames() {
        }
    }
}

