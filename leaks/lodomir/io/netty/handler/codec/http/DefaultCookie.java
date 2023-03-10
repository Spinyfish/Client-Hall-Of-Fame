/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.Cookie;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class DefaultCookie
implements Cookie {
    private final String name;
    private String value;
    private String domain;
    private String path;
    private String comment;
    private String commentUrl;
    private boolean discard;
    private Set<Integer> ports = Collections.emptySet();
    private Set<Integer> unmodifiablePorts = this.ports;
    private long maxAge = Long.MIN_VALUE;
    private int version;
    private boolean secure;
    private boolean httpOnly;

    public DefaultCookie(String name, String value) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if ((name = name.trim()).isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (c > '\u007f') {
                throw new IllegalArgumentException("name contains non-ascii character: " + name);
            }
            switch (c) {
                case '\t': 
                case '\n': 
                case '\u000b': 
                case '\f': 
                case '\r': 
                case ' ': 
                case ',': 
                case ';': 
                case '=': {
                    throw new IllegalArgumentException("name contains one of the following prohibited characters: =,; \\t\\r\\n\\v\\f: " + name);
                }
            }
        }
        if (name.charAt(0) == '$') {
            throw new IllegalArgumentException("name starting with '$' not allowed: " + name);
        }
        this.name = name;
        this.setValue(value);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = DefaultCookie.validateValue("domain", domain);
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setPath(String path) {
        this.path = DefaultCookie.validateValue("path", path);
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = DefaultCookie.validateValue("comment", comment);
    }

    @Override
    public String getCommentUrl() {
        return this.commentUrl;
    }

    @Override
    public void setCommentUrl(String commentUrl) {
        this.commentUrl = DefaultCookie.validateValue("commentUrl", commentUrl);
    }

    @Override
    public boolean isDiscard() {
        return this.discard;
    }

    @Override
    public void setDiscard(boolean discard) {
        this.discard = discard;
    }

    @Override
    public Set<Integer> getPorts() {
        if (this.unmodifiablePorts == null) {
            this.unmodifiablePorts = Collections.unmodifiableSet(this.ports);
        }
        return this.unmodifiablePorts;
    }

    @Override
    public void setPorts(int ... ports) {
        if (ports == null) {
            throw new NullPointerException("ports");
        }
        int[] portsCopy = (int[])ports.clone();
        if (portsCopy.length == 0) {
            this.ports = Collections.emptySet();
            this.unmodifiablePorts = this.ports;
        } else {
            TreeSet<Integer> newPorts = new TreeSet<Integer>();
            for (int p : portsCopy) {
                if (p <= 0 || p > 65535) {
                    throw new IllegalArgumentException("port out of range: " + p);
                }
                newPorts.add(p);
            }
            this.ports = newPorts;
            this.unmodifiablePorts = null;
        }
    }

    @Override
    public void setPorts(Iterable<Integer> ports) {
        TreeSet<Integer> newPorts = new TreeSet<Integer>();
        for (int p : ports) {
            if (p <= 0 || p > 65535) {
                throw new IllegalArgumentException("port out of range: " + p);
            }
            newPorts.add(p);
        }
        if (newPorts.isEmpty()) {
            this.ports = Collections.emptySet();
            this.unmodifiablePorts = this.ports;
        } else {
            this.ports = newPorts;
            this.unmodifiablePorts = null;
        }
    }

    @Override
    public long getMaxAge() {
        return this.maxAge;
    }

    @Override
    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Cookie)) {
            return false;
        }
        Cookie that = (Cookie)o;
        if (!this.getName().equalsIgnoreCase(that.getName())) {
            return false;
        }
        if (this.getPath() == null) {
            if (that.getPath() != null) {
                return false;
            }
        } else {
            if (that.getPath() == null) {
                return false;
            }
            if (!this.getPath().equals(that.getPath())) {
                return false;
            }
        }
        if (this.getDomain() == null) {
            return that.getDomain() == null;
        }
        if (that.getDomain() == null) {
            return false;
        }
        return this.getDomain().equalsIgnoreCase(that.getDomain());
    }

    @Override
    public int compareTo(Cookie c) {
        int v = this.getName().compareToIgnoreCase(c.getName());
        if (v != 0) {
            return v;
        }
        if (this.getPath() == null) {
            if (c.getPath() != null) {
                return -1;
            }
        } else {
            if (c.getPath() == null) {
                return 1;
            }
            v = this.getPath().compareTo(c.getPath());
            if (v != 0) {
                return v;
            }
        }
        if (this.getDomain() == null) {
            if (c.getDomain() != null) {
                return -1;
            }
        } else {
            if (c.getDomain() == null) {
                return 1;
            }
            v = this.getDomain().compareToIgnoreCase(c.getDomain());
            return v;
        }
        return 0;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.getName());
        buf.append('=');
        buf.append(this.getValue());
        if (this.getDomain() != null) {
            buf.append(", domain=");
            buf.append(this.getDomain());
        }
        if (this.getPath() != null) {
            buf.append(", path=");
            buf.append(this.getPath());
        }
        if (this.getComment() != null) {
            buf.append(", comment=");
            buf.append(this.getComment());
        }
        if (this.getMaxAge() >= 0L) {
            buf.append(", maxAge=");
            buf.append(this.getMaxAge());
            buf.append('s');
        }
        if (this.isSecure()) {
            buf.append(", secure");
        }
        if (this.isHttpOnly()) {
            buf.append(", HTTPOnly");
        }
        return buf.toString();
    }

    private static String validateValue(String name, String value) {
        if (value == null) {
            return null;
        }
        if ((value = value.trim()).isEmpty()) {
            return null;
        }
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            switch (c) {
                case '\n': 
                case '\u000b': 
                case '\f': 
                case '\r': 
                case ';': {
                    throw new IllegalArgumentException(name + " contains one of the following prohibited characters: " + ";\\r\\n\\f\\v (" + value + ')');
                }
            }
        }
        return value;
    }
}

