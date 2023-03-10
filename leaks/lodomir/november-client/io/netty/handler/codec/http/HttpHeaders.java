/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderDateFormat;
import io.netty.handler.codec.http.HttpHeaderEntity;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class HttpHeaders
implements Iterable<Map.Entry<String, String>> {
    private static final byte[] HEADER_SEPERATOR = new byte[]{58, 32};
    private static final byte[] CRLF = new byte[]{13, 10};
    private static final CharSequence CONTENT_LENGTH_ENTITY = HttpHeaders.newEntity("Content-Length");
    private static final CharSequence CONNECTION_ENTITY = HttpHeaders.newEntity("Connection");
    private static final CharSequence CLOSE_ENTITY = HttpHeaders.newEntity("close");
    private static final CharSequence KEEP_ALIVE_ENTITY = HttpHeaders.newEntity("keep-alive");
    private static final CharSequence HOST_ENTITY = HttpHeaders.newEntity("Host");
    private static final CharSequence DATE_ENTITY = HttpHeaders.newEntity("Date");
    private static final CharSequence EXPECT_ENTITY = HttpHeaders.newEntity("Expect");
    private static final CharSequence CONTINUE_ENTITY = HttpHeaders.newEntity("100-continue");
    private static final CharSequence TRANSFER_ENCODING_ENTITY = HttpHeaders.newEntity("Transfer-Encoding");
    private static final CharSequence CHUNKED_ENTITY = HttpHeaders.newEntity("chunked");
    private static final CharSequence SEC_WEBSOCKET_KEY1_ENTITY = HttpHeaders.newEntity("Sec-WebSocket-Key1");
    private static final CharSequence SEC_WEBSOCKET_KEY2_ENTITY = HttpHeaders.newEntity("Sec-WebSocket-Key2");
    private static final CharSequence SEC_WEBSOCKET_ORIGIN_ENTITY = HttpHeaders.newEntity("Sec-WebSocket-Origin");
    private static final CharSequence SEC_WEBSOCKET_LOCATION_ENTITY = HttpHeaders.newEntity("Sec-WebSocket-Location");
    public static final HttpHeaders EMPTY_HEADERS = new HttpHeaders(){

        @Override
        public String get(String name) {
            return null;
        }

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
        public HttpHeaders add(String name, Object value) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public HttpHeaders add(String name, Iterable<?> values) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public HttpHeaders set(String name, Object value) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public HttpHeaders set(String name, Iterable<?> values) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public HttpHeaders remove(String name) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public HttpHeaders clear() {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public Iterator<Map.Entry<String, String>> iterator() {
            return this.entries().iterator();
        }
    };

    public static boolean isKeepAlive(HttpMessage message) {
        String connection = message.headers().get(CONNECTION_ENTITY);
        if (connection != null && HttpHeaders.equalsIgnoreCase(CLOSE_ENTITY, connection)) {
            return false;
        }
        if (message.getProtocolVersion().isKeepAliveDefault()) {
            return !HttpHeaders.equalsIgnoreCase(CLOSE_ENTITY, connection);
        }
        return HttpHeaders.equalsIgnoreCase(KEEP_ALIVE_ENTITY, connection);
    }

    public static void setKeepAlive(HttpMessage message, boolean keepAlive) {
        HttpHeaders h = message.headers();
        if (message.getProtocolVersion().isKeepAliveDefault()) {
            if (keepAlive) {
                h.remove(CONNECTION_ENTITY);
            } else {
                h.set(CONNECTION_ENTITY, (Object)CLOSE_ENTITY);
            }
        } else if (keepAlive) {
            h.set(CONNECTION_ENTITY, (Object)KEEP_ALIVE_ENTITY);
        } else {
            h.remove(CONNECTION_ENTITY);
        }
    }

    public static String getHeader(HttpMessage message, String name) {
        return message.headers().get(name);
    }

    public static String getHeader(HttpMessage message, CharSequence name) {
        return message.headers().get(name);
    }

    public static String getHeader(HttpMessage message, String name, String defaultValue) {
        return HttpHeaders.getHeader(message, (CharSequence)name, defaultValue);
    }

    public static String getHeader(HttpMessage message, CharSequence name, String defaultValue) {
        String value = message.headers().get(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static void setHeader(HttpMessage message, String name, Object value) {
        message.headers().set(name, value);
    }

    public static void setHeader(HttpMessage message, CharSequence name, Object value) {
        message.headers().set(name, value);
    }

    public static void setHeader(HttpMessage message, String name, Iterable<?> values) {
        message.headers().set(name, values);
    }

    public static void setHeader(HttpMessage message, CharSequence name, Iterable<?> values) {
        message.headers().set(name, values);
    }

    public static void addHeader(HttpMessage message, String name, Object value) {
        message.headers().add(name, value);
    }

    public static void addHeader(HttpMessage message, CharSequence name, Object value) {
        message.headers().add(name, value);
    }

    public static void removeHeader(HttpMessage message, String name) {
        message.headers().remove(name);
    }

    public static void removeHeader(HttpMessage message, CharSequence name) {
        message.headers().remove(name);
    }

    public static void clearHeaders(HttpMessage message) {
        message.headers().clear();
    }

    public static int getIntHeader(HttpMessage message, String name) {
        return HttpHeaders.getIntHeader(message, (CharSequence)name);
    }

    public static int getIntHeader(HttpMessage message, CharSequence name) {
        String value = HttpHeaders.getHeader(message, name);
        if (value == null) {
            throw new NumberFormatException("header not found: " + name);
        }
        return Integer.parseInt(value);
    }

    public static int getIntHeader(HttpMessage message, String name, int defaultValue) {
        return HttpHeaders.getIntHeader(message, (CharSequence)name, defaultValue);
    }

    public static int getIntHeader(HttpMessage message, CharSequence name, int defaultValue) {
        String value = HttpHeaders.getHeader(message, name);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    public static void setIntHeader(HttpMessage message, String name, int value) {
        message.headers().set(name, (Object)value);
    }

    public static void setIntHeader(HttpMessage message, CharSequence name, int value) {
        message.headers().set(name, (Object)value);
    }

    public static void setIntHeader(HttpMessage message, String name, Iterable<Integer> values) {
        message.headers().set(name, values);
    }

    public static void setIntHeader(HttpMessage message, CharSequence name, Iterable<Integer> values) {
        message.headers().set(name, values);
    }

    public static void addIntHeader(HttpMessage message, String name, int value) {
        message.headers().add(name, (Object)value);
    }

    public static void addIntHeader(HttpMessage message, CharSequence name, int value) {
        message.headers().add(name, (Object)value);
    }

    public static Date getDateHeader(HttpMessage message, String name) throws ParseException {
        return HttpHeaders.getDateHeader(message, (CharSequence)name);
    }

    public static Date getDateHeader(HttpMessage message, CharSequence name) throws ParseException {
        String value = HttpHeaders.getHeader(message, name);
        if (value == null) {
            throw new ParseException("header not found: " + name, 0);
        }
        return HttpHeaderDateFormat.get().parse(value);
    }

    public static Date getDateHeader(HttpMessage message, String name, Date defaultValue) {
        return HttpHeaders.getDateHeader(message, (CharSequence)name, defaultValue);
    }

    public static Date getDateHeader(HttpMessage message, CharSequence name, Date defaultValue) {
        String value = HttpHeaders.getHeader(message, name);
        if (value == null) {
            return defaultValue;
        }
        try {
            return HttpHeaderDateFormat.get().parse(value);
        }
        catch (ParseException ignored) {
            return defaultValue;
        }
    }

    public static void setDateHeader(HttpMessage message, String name, Date value) {
        HttpHeaders.setDateHeader(message, (CharSequence)name, value);
    }

    public static void setDateHeader(HttpMessage message, CharSequence name, Date value) {
        if (value != null) {
            message.headers().set(name, (Object)HttpHeaderDateFormat.get().format(value));
        } else {
            message.headers().set(name, null);
        }
    }

    public static void setDateHeader(HttpMessage message, String name, Iterable<Date> values) {
        message.headers().set(name, values);
    }

    public static void setDateHeader(HttpMessage message, CharSequence name, Iterable<Date> values) {
        message.headers().set(name, values);
    }

    public static void addDateHeader(HttpMessage message, String name, Date value) {
        message.headers().add(name, (Object)value);
    }

    public static void addDateHeader(HttpMessage message, CharSequence name, Date value) {
        message.headers().add(name, (Object)value);
    }

    public static long getContentLength(HttpMessage message) {
        String value = HttpHeaders.getHeader(message, CONTENT_LENGTH_ENTITY);
        if (value != null) {
            return Long.parseLong(value);
        }
        long webSocketContentLength = HttpHeaders.getWebSocketContentLength(message);
        if (webSocketContentLength >= 0L) {
            return webSocketContentLength;
        }
        throw new NumberFormatException("header not found: Content-Length");
    }

    public static long getContentLength(HttpMessage message, long defaultValue) {
        String contentLength = message.headers().get(CONTENT_LENGTH_ENTITY);
        if (contentLength != null) {
            try {
                return Long.parseLong(contentLength);
            }
            catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        long webSocketContentLength = HttpHeaders.getWebSocketContentLength(message);
        if (webSocketContentLength >= 0L) {
            return webSocketContentLength;
        }
        return defaultValue;
    }

    private static int getWebSocketContentLength(HttpMessage message) {
        HttpResponse res;
        HttpHeaders h = message.headers();
        if (message instanceof HttpRequest) {
            HttpRequest req = (HttpRequest)message;
            if (HttpMethod.GET.equals(req.getMethod()) && h.contains(SEC_WEBSOCKET_KEY1_ENTITY) && h.contains(SEC_WEBSOCKET_KEY2_ENTITY)) {
                return 8;
            }
        } else if (message instanceof HttpResponse && (res = (HttpResponse)message).getStatus().code() == 101 && h.contains(SEC_WEBSOCKET_ORIGIN_ENTITY) && h.contains(SEC_WEBSOCKET_LOCATION_ENTITY)) {
            return 16;
        }
        return -1;
    }

    public static void setContentLength(HttpMessage message, long length) {
        message.headers().set(CONTENT_LENGTH_ENTITY, (Object)length);
    }

    public static String getHost(HttpMessage message) {
        return message.headers().get(HOST_ENTITY);
    }

    public static String getHost(HttpMessage message, String defaultValue) {
        return HttpHeaders.getHeader(message, HOST_ENTITY, defaultValue);
    }

    public static void setHost(HttpMessage message, String value) {
        message.headers().set(HOST_ENTITY, (Object)value);
    }

    public static void setHost(HttpMessage message, CharSequence value) {
        message.headers().set(HOST_ENTITY, (Object)value);
    }

    public static Date getDate(HttpMessage message) throws ParseException {
        return HttpHeaders.getDateHeader(message, DATE_ENTITY);
    }

    public static Date getDate(HttpMessage message, Date defaultValue) {
        return HttpHeaders.getDateHeader(message, DATE_ENTITY, defaultValue);
    }

    public static void setDate(HttpMessage message, Date value) {
        if (value != null) {
            message.headers().set(DATE_ENTITY, (Object)HttpHeaderDateFormat.get().format(value));
        } else {
            message.headers().set(DATE_ENTITY, null);
        }
    }

    public static boolean is100ContinueExpected(HttpMessage message) {
        if (!(message instanceof HttpRequest)) {
            return false;
        }
        if (message.getProtocolVersion().compareTo(HttpVersion.HTTP_1_1) < 0) {
            return false;
        }
        String value = message.headers().get(EXPECT_ENTITY);
        if (value == null) {
            return false;
        }
        if (HttpHeaders.equalsIgnoreCase(CONTINUE_ENTITY, value)) {
            return true;
        }
        return message.headers().contains(EXPECT_ENTITY, CONTINUE_ENTITY, true);
    }

    public static void set100ContinueExpected(HttpMessage message) {
        HttpHeaders.set100ContinueExpected(message, true);
    }

    public static void set100ContinueExpected(HttpMessage message, boolean set) {
        if (set) {
            message.headers().set(EXPECT_ENTITY, (Object)CONTINUE_ENTITY);
        } else {
            message.headers().remove(EXPECT_ENTITY);
        }
    }

    static void validateHeaderName(CharSequence headerName) {
        if (headerName == null) {
            throw new NullPointerException("Header names cannot be null");
        }
        for (int index = 0; index < headerName.length(); ++index) {
            char character = headerName.charAt(index);
            if (character > '\u007f') {
                throw new IllegalArgumentException("Header name cannot contain non-ASCII characters: " + headerName);
            }
            switch (character) {
                case '\t': 
                case '\n': 
                case '\u000b': 
                case '\f': 
                case '\r': 
                case ' ': 
                case ',': 
                case ':': 
                case ';': 
                case '=': {
                    throw new IllegalArgumentException("Header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + headerName);
                }
            }
        }
    }

    static void validateHeaderValue(CharSequence headerValue) {
        if (headerValue == null) {
            throw new NullPointerException("Header values cannot be null");
        }
        int state = 0;
        block19: for (int index = 0; index < headerValue.length(); ++index) {
            char character = headerValue.charAt(index);
            switch (character) {
                case '\u000b': {
                    throw new IllegalArgumentException("Header value contains a prohibited character '\\v': " + headerValue);
                }
                case '\f': {
                    throw new IllegalArgumentException("Header value contains a prohibited character '\\f': " + headerValue);
                }
            }
            switch (state) {
                case 0: {
                    switch (character) {
                        case '\r': {
                            state = 1;
                            break;
                        }
                        case '\n': {
                            state = 2;
                        }
                    }
                    continue block19;
                }
                case 1: {
                    switch (character) {
                        case '\n': {
                            state = 2;
                            continue block19;
                        }
                    }
                    throw new IllegalArgumentException("Only '\\n' is allowed after '\\r': " + headerValue);
                }
                case 2: {
                    switch (character) {
                        case '\t': 
                        case ' ': {
                            state = 0;
                            continue block19;
                        }
                    }
                    throw new IllegalArgumentException("Only ' ' and '\\t' are allowed after '\\n': " + headerValue);
                }
            }
        }
        if (state != 0) {
            throw new IllegalArgumentException("Header value must not end with '\\r' or '\\n':" + headerValue);
        }
    }

    public static boolean isTransferEncodingChunked(HttpMessage message) {
        return message.headers().contains(TRANSFER_ENCODING_ENTITY, CHUNKED_ENTITY, true);
    }

    public static void removeTransferEncodingChunked(HttpMessage m) {
        List<String> values = m.headers().getAll(TRANSFER_ENCODING_ENTITY);
        if (values.isEmpty()) {
            return;
        }
        Iterator<String> valuesIt = values.iterator();
        while (valuesIt.hasNext()) {
            String value = valuesIt.next();
            if (!HttpHeaders.equalsIgnoreCase(value, CHUNKED_ENTITY)) continue;
            valuesIt.remove();
        }
        if (values.isEmpty()) {
            m.headers().remove(TRANSFER_ENCODING_ENTITY);
        } else {
            m.headers().set(TRANSFER_ENCODING_ENTITY, values);
        }
    }

    public static void setTransferEncodingChunked(HttpMessage m) {
        HttpHeaders.addHeader(m, TRANSFER_ENCODING_ENTITY, (Object)CHUNKED_ENTITY);
        HttpHeaders.removeHeader(m, CONTENT_LENGTH_ENTITY);
    }

    public static boolean isContentLengthSet(HttpMessage m) {
        return m.headers().contains(CONTENT_LENGTH_ENTITY);
    }

    public static boolean equalsIgnoreCase(CharSequence name1, CharSequence name2) {
        if (name1 == name2) {
            return true;
        }
        if (name1 == null || name2 == null) {
            return false;
        }
        int nameLen = name1.length();
        if (nameLen != name2.length()) {
            return false;
        }
        for (int i = nameLen - 1; i >= 0; --i) {
            char c2;
            char c1 = name1.charAt(i);
            if (c1 == (c2 = name2.charAt(i))) continue;
            if (c1 >= 'A' && c1 <= 'Z') {
                c1 = (char)(c1 + 32);
            }
            if (c2 >= 'A' && c2 <= 'Z') {
                c2 = (char)(c2 + 32);
            }
            if (c1 == c2) continue;
            return false;
        }
        return true;
    }

    static int hash(CharSequence name) {
        if (name instanceof HttpHeaderEntity) {
            return ((HttpHeaderEntity)name).hash();
        }
        int h = 0;
        for (int i = name.length() - 1; i >= 0; --i) {
            char c = name.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                c = (char)(c + 32);
            }
            h = 31 * h + c;
        }
        if (h > 0) {
            return h;
        }
        if (h == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        }
        return -h;
    }

    static void encode(HttpHeaders headers, ByteBuf buf) {
        if (headers instanceof DefaultHttpHeaders) {
            ((DefaultHttpHeaders)headers).encode(buf);
        } else {
            for (Map.Entry header : headers) {
                HttpHeaders.encode((CharSequence)header.getKey(), (CharSequence)header.getValue(), buf);
            }
        }
    }

    static void encode(CharSequence key, CharSequence value, ByteBuf buf) {
        if (!HttpHeaders.encodeAscii(key, buf)) {
            buf.writeBytes(HEADER_SEPERATOR);
        }
        if (!HttpHeaders.encodeAscii(value, buf)) {
            buf.writeBytes(CRLF);
        }
    }

    public static boolean encodeAscii(CharSequence seq, ByteBuf buf) {
        if (seq instanceof HttpHeaderEntity) {
            return ((HttpHeaderEntity)seq).encode(buf);
        }
        HttpHeaders.encodeAscii0(seq, buf);
        return false;
    }

    static void encodeAscii0(CharSequence seq, ByteBuf buf) {
        int length = seq.length();
        for (int i = 0; i < length; ++i) {
            buf.writeByte((byte)seq.charAt(i));
        }
    }

    public static CharSequence newEntity(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return new HttpHeaderEntity(name);
    }

    public static CharSequence newNameEntity(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return new HttpHeaderEntity(name, HEADER_SEPERATOR);
    }

    public static CharSequence newValueEntity(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return new HttpHeaderEntity(name, CRLF);
    }

    protected HttpHeaders() {
    }

    public abstract String get(String var1);

    public String get(CharSequence name) {
        return this.get(name.toString());
    }

    public abstract List<String> getAll(String var1);

    public List<String> getAll(CharSequence name) {
        return this.getAll(name.toString());
    }

    public abstract List<Map.Entry<String, String>> entries();

    public abstract boolean contains(String var1);

    public boolean contains(CharSequence name) {
        return this.contains(name.toString());
    }

    public abstract boolean isEmpty();

    public abstract Set<String> names();

    public abstract HttpHeaders add(String var1, Object var2);

    public HttpHeaders add(CharSequence name, Object value) {
        return this.add(name.toString(), value);
    }

    public abstract HttpHeaders add(String var1, Iterable<?> var2);

    public HttpHeaders add(CharSequence name, Iterable<?> values) {
        return this.add(name.toString(), values);
    }

    public HttpHeaders add(HttpHeaders headers) {
        if (headers == null) {
            throw new NullPointerException("headers");
        }
        for (Map.Entry e : headers) {
            this.add((String)e.getKey(), e.getValue());
        }
        return this;
    }

    public abstract HttpHeaders set(String var1, Object var2);

    public HttpHeaders set(CharSequence name, Object value) {
        return this.set(name.toString(), value);
    }

    public abstract HttpHeaders set(String var1, Iterable<?> var2);

    public HttpHeaders set(CharSequence name, Iterable<?> values) {
        return this.set(name.toString(), values);
    }

    public HttpHeaders set(HttpHeaders headers) {
        if (headers == null) {
            throw new NullPointerException("headers");
        }
        this.clear();
        for (Map.Entry e : headers) {
            this.add((String)e.getKey(), e.getValue());
        }
        return this;
    }

    public abstract HttpHeaders remove(String var1);

    public HttpHeaders remove(CharSequence name) {
        return this.remove(name.toString());
    }

    public abstract HttpHeaders clear();

    public boolean contains(String name, String value, boolean ignoreCaseValue) {
        List<String> values = this.getAll(name);
        if (values.isEmpty()) {
            return false;
        }
        for (String v : values) {
            if (!(ignoreCaseValue ? HttpHeaders.equalsIgnoreCase(v, value) : v.equals(value))) continue;
            return true;
        }
        return false;
    }

    public boolean contains(CharSequence name, CharSequence value, boolean ignoreCaseValue) {
        return this.contains(name.toString(), value.toString(), ignoreCaseValue);
    }

    public static final class Values {
        public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
        public static final String BASE64 = "base64";
        public static final String BINARY = "binary";
        public static final String BOUNDARY = "boundary";
        public static final String BYTES = "bytes";
        public static final String CHARSET = "charset";
        public static final String CHUNKED = "chunked";
        public static final String CLOSE = "close";
        public static final String COMPRESS = "compress";
        public static final String CONTINUE = "100-continue";
        public static final String DEFLATE = "deflate";
        public static final String GZIP = "gzip";
        public static final String IDENTITY = "identity";
        public static final String KEEP_ALIVE = "keep-alive";
        public static final String MAX_AGE = "max-age";
        public static final String MAX_STALE = "max-stale";
        public static final String MIN_FRESH = "min-fresh";
        public static final String MULTIPART_FORM_DATA = "multipart/form-data";
        public static final String MUST_REVALIDATE = "must-revalidate";
        public static final String NO_CACHE = "no-cache";
        public static final String NO_STORE = "no-store";
        public static final String NO_TRANSFORM = "no-transform";
        public static final String NONE = "none";
        public static final String ONLY_IF_CACHED = "only-if-cached";
        public static final String PRIVATE = "private";
        public static final String PROXY_REVALIDATE = "proxy-revalidate";
        public static final String PUBLIC = "public";
        public static final String QUOTED_PRINTABLE = "quoted-printable";
        public static final String S_MAXAGE = "s-maxage";
        public static final String TRAILERS = "trailers";
        public static final String UPGRADE = "Upgrade";
        public static final String WEBSOCKET = "WebSocket";

        private Values() {
        }
    }

    public static final class Names {
        public static final String ACCEPT = "Accept";
        public static final String ACCEPT_CHARSET = "Accept-Charset";
        public static final String ACCEPT_ENCODING = "Accept-Encoding";
        public static final String ACCEPT_LANGUAGE = "Accept-Language";
        public static final String ACCEPT_RANGES = "Accept-Ranges";
        public static final String ACCEPT_PATCH = "Accept-Patch";
        public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
        public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
        public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
        public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
        public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
        public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
        public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
        public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
        public static final String AGE = "Age";
        public static final String ALLOW = "Allow";
        public static final String AUTHORIZATION = "Authorization";
        public static final String CACHE_CONTROL = "Cache-Control";
        public static final String CONNECTION = "Connection";
        public static final String CONTENT_BASE = "Content-Base";
        public static final String CONTENT_ENCODING = "Content-Encoding";
        public static final String CONTENT_LANGUAGE = "Content-Language";
        public static final String CONTENT_LENGTH = "Content-Length";
        public static final String CONTENT_LOCATION = "Content-Location";
        public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
        public static final String CONTENT_MD5 = "Content-MD5";
        public static final String CONTENT_RANGE = "Content-Range";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String COOKIE = "Cookie";
        public static final String DATE = "Date";
        public static final String ETAG = "ETag";
        public static final String EXPECT = "Expect";
        public static final String EXPIRES = "Expires";
        public static final String FROM = "From";
        public static final String HOST = "Host";
        public static final String IF_MATCH = "If-Match";
        public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
        public static final String IF_NONE_MATCH = "If-None-Match";
        public static final String IF_RANGE = "If-Range";
        public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
        public static final String LAST_MODIFIED = "Last-Modified";
        public static final String LOCATION = "Location";
        public static final String MAX_FORWARDS = "Max-Forwards";
        public static final String ORIGIN = "Origin";
        public static final String PRAGMA = "Pragma";
        public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
        public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
        public static final String RANGE = "Range";
        public static final String REFERER = "Referer";
        public static final String RETRY_AFTER = "Retry-After";
        public static final String SEC_WEBSOCKET_KEY1 = "Sec-WebSocket-Key1";
        public static final String SEC_WEBSOCKET_KEY2 = "Sec-WebSocket-Key2";
        public static final String SEC_WEBSOCKET_LOCATION = "Sec-WebSocket-Location";
        public static final String SEC_WEBSOCKET_ORIGIN = "Sec-WebSocket-Origin";
        public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
        public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";
        public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
        public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";
        public static final String SERVER = "Server";
        public static final String SET_COOKIE = "Set-Cookie";
        public static final String SET_COOKIE2 = "Set-Cookie2";
        public static final String TE = "TE";
        public static final String TRAILER = "Trailer";
        public static final String TRANSFER_ENCODING = "Transfer-Encoding";
        public static final String UPGRADE = "Upgrade";
        public static final String USER_AGENT = "User-Agent";
        public static final String VARY = "Vary";
        public static final String VIA = "Via";
        public static final String WARNING = "Warning";
        public static final String WEBSOCKET_LOCATION = "WebSocket-Location";
        public static final String WEBSOCKET_ORIGIN = "WebSocket-Origin";
        public static final String WEBSOCKET_PROTOCOL = "WebSocket-Protocol";
        public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

        private Names() {
        }
    }
}

