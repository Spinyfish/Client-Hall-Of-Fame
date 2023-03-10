/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpConstants;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryStringDecoder {
    private static final int DEFAULT_MAX_PARAMS = 1024;
    private final Charset charset;
    private final String uri;
    private final boolean hasPath;
    private final int maxParams;
    private String path;
    private Map<String, List<String>> params;
    private int nParams;

    public QueryStringDecoder(String uri) {
        this(uri, HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringDecoder(String uri, boolean hasPath) {
        this(uri, HttpConstants.DEFAULT_CHARSET, hasPath);
    }

    public QueryStringDecoder(String uri, Charset charset) {
        this(uri, charset, true);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath) {
        this(uri, charset, hasPath, 1024);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams) {
        if (uri == null) {
            throw new NullPointerException("getUri");
        }
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        if (maxParams <= 0) {
            throw new IllegalArgumentException("maxParams: " + maxParams + " (expected: a positive integer)");
        }
        this.uri = uri;
        this.charset = charset;
        this.maxParams = maxParams;
        this.hasPath = hasPath;
    }

    public QueryStringDecoder(URI uri) {
        this(uri, HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringDecoder(URI uri, Charset charset) {
        this(uri, charset, 1024);
    }

    public QueryStringDecoder(URI uri, Charset charset, int maxParams) {
        if (uri == null) {
            throw new NullPointerException("getUri");
        }
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        if (maxParams <= 0) {
            throw new IllegalArgumentException("maxParams: " + maxParams + " (expected: a positive integer)");
        }
        String rawPath = uri.getRawPath();
        if (rawPath != null) {
            this.hasPath = true;
        } else {
            rawPath = "";
            this.hasPath = false;
        }
        this.uri = rawPath + '?' + uri.getRawQuery();
        this.charset = charset;
        this.maxParams = maxParams;
    }

    public String path() {
        if (this.path == null) {
            if (!this.hasPath) {
                this.path = "";
                return "";
            }
            int pathEndPos = this.uri.indexOf(63);
            if (pathEndPos < 0) {
                this.path = this.uri;
            } else {
                this.path = this.uri.substring(0, pathEndPos);
                return this.path;
            }
        }
        return this.path;
    }

    public Map<String, List<String>> parameters() {
        if (this.params == null) {
            if (this.hasPath) {
                int pathLength = this.path().length();
                if (this.uri.length() == pathLength) {
                    return Collections.emptyMap();
                }
                this.decodeParams(this.uri.substring(pathLength + 1));
            } else {
                if (this.uri.isEmpty()) {
                    return Collections.emptyMap();
                }
                this.decodeParams(this.uri);
            }
        }
        return this.params;
    }

    private void decodeParams(String s) {
        int i;
        this.params = new LinkedHashMap<String, List<String>>();
        LinkedHashMap<String, List<String>> params = this.params;
        this.nParams = 0;
        String name = null;
        int pos = 0;
        for (i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '=' && name == null) {
                if (pos != i) {
                    name = QueryStringDecoder.decodeComponent(s.substring(pos, i), this.charset);
                }
                pos = i + 1;
                continue;
            }
            if (c != '&' && c != ';') continue;
            if (name == null && pos != i) {
                if (!this.addParam(params, QueryStringDecoder.decodeComponent(s.substring(pos, i), this.charset), "")) {
                    return;
                }
            } else if (name != null) {
                if (!this.addParam(params, name, QueryStringDecoder.decodeComponent(s.substring(pos, i), this.charset))) {
                    return;
                }
                name = null;
            }
            pos = i + 1;
        }
        if (pos != i) {
            if (name == null) {
                this.addParam(params, QueryStringDecoder.decodeComponent(s.substring(pos, i), this.charset), "");
            } else {
                this.addParam(params, name, QueryStringDecoder.decodeComponent(s.substring(pos, i), this.charset));
            }
        } else if (name != null) {
            this.addParam(params, name, "");
        }
    }

    private boolean addParam(Map<String, List<String>> params, String name, String value) {
        if (this.nParams >= this.maxParams) {
            return false;
        }
        List<String> values = params.get(name);
        if (values == null) {
            values = new ArrayList<String>(1);
            params.put(name, values);
        }
        values.add(value);
        ++this.nParams;
        return true;
    }

    public static String decodeComponent(String s) {
        return QueryStringDecoder.decodeComponent(s, HttpConstants.DEFAULT_CHARSET);
    }

    public static String decodeComponent(String s, Charset charset) {
        if (s == null) {
            return "";
        }
        int size = s.length();
        boolean modified = false;
        for (int i = 0; i < size; ++i) {
            char c = s.charAt(i);
            if (c != '%' && c != '+') continue;
            modified = true;
            break;
        }
        if (!modified) {
            return s;
        }
        byte[] buf = new byte[size];
        int pos = 0;
        block5: for (int i = 0; i < size; ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '+': {
                    buf[pos++] = 32;
                    continue block5;
                }
                case '%': {
                    if (i == size - 1) {
                        throw new IllegalArgumentException("unterminated escape sequence at end of string: " + s);
                    }
                    if ((c = s.charAt(++i)) == '%') {
                        buf[pos++] = 37;
                        continue block5;
                    }
                    if (i == size - 1) {
                        throw new IllegalArgumentException("partial escape sequence at end of string: " + s);
                    }
                    c = QueryStringDecoder.decodeHexNibble(c);
                    char c2 = QueryStringDecoder.decodeHexNibble(s.charAt(++i));
                    if (c == '\uffff' || c2 == '\uffff') {
                        throw new IllegalArgumentException("invalid escape sequence `%" + s.charAt(i - 1) + s.charAt(i) + "' at index " + (i - 2) + " of: " + s);
                    }
                    c = (char)(c * 16 + c2);
                }
                default: {
                    buf[pos++] = (byte)c;
                }
            }
        }
        return new String(buf, 0, pos, charset);
    }

    private static char decodeHexNibble(char c) {
        if ('0' <= c && c <= '9') {
            return (char)(c - 48);
        }
        if ('a' <= c && c <= 'f') {
            return (char)(c - 97 + 10);
        }
        if ('A' <= c && c <= 'F') {
            return (char)(c - 65 + 10);
        }
        return '\uffff';
    }
}

