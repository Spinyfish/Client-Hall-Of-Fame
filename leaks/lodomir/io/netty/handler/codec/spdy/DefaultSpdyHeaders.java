/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaders;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public class DefaultSpdyHeaders
extends SpdyHeaders {
    private static final int BUCKET_SIZE = 17;
    private final HeaderEntry[] entries = new HeaderEntry[17];
    private final HeaderEntry head;

    private static int hash(String name) {
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

    private static boolean eq(String name1, String name2) {
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

    private static int index(int hash) {
        return hash % 17;
    }

    DefaultSpdyHeaders() {
        this.head.before = this.head.after = (this.head = new HeaderEntry(-1, null, null));
    }

    @Override
    public SpdyHeaders add(String name, Object value) {
        String lowerCaseName = name.toLowerCase();
        SpdyCodecUtil.validateHeaderName(lowerCaseName);
        String strVal = DefaultSpdyHeaders.toString(value);
        SpdyCodecUtil.validateHeaderValue(strVal);
        int h = DefaultSpdyHeaders.hash(lowerCaseName);
        int i = DefaultSpdyHeaders.index(h);
        this.add0(h, i, lowerCaseName, strVal);
        return this;
    }

    private void add0(int h, int i, String name, String value) {
        HeaderEntry newEntry;
        HeaderEntry e = this.entries[i];
        this.entries[i] = newEntry = new HeaderEntry(h, name, value);
        newEntry.next = e;
        newEntry.addBefore(this.head);
    }

    @Override
    public SpdyHeaders remove(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        String lowerCaseName = name.toLowerCase();
        int h = DefaultSpdyHeaders.hash(lowerCaseName);
        int i = DefaultSpdyHeaders.index(h);
        this.remove0(h, i, lowerCaseName);
        return this;
    }

    private void remove0(int h, int i, String name) {
        HeaderEntry next;
        HeaderEntry e = this.entries[i];
        if (e == null) {
            return;
        }
        while (e.hash == h && DefaultSpdyHeaders.eq(name, e.key)) {
            e.remove();
            next = e.next;
            if (next != null) {
                this.entries[i] = next;
                e = next;
                continue;
            }
            this.entries[i] = null;
            return;
        }
        while ((next = e.next) != null) {
            if (next.hash == h && DefaultSpdyHeaders.eq(name, next.key)) {
                e.next = next.next;
                next.remove();
                continue;
            }
            e = next;
        }
    }

    @Override
    public SpdyHeaders set(String name, Object value) {
        String lowerCaseName = name.toLowerCase();
        SpdyCodecUtil.validateHeaderName(lowerCaseName);
        String strVal = DefaultSpdyHeaders.toString(value);
        SpdyCodecUtil.validateHeaderValue(strVal);
        int h = DefaultSpdyHeaders.hash(lowerCaseName);
        int i = DefaultSpdyHeaders.index(h);
        this.remove0(h, i, lowerCaseName);
        this.add0(h, i, lowerCaseName, strVal);
        return this;
    }

    @Override
    public SpdyHeaders set(String name, Iterable<?> values) {
        if (values == null) {
            throw new NullPointerException("values");
        }
        String lowerCaseName = name.toLowerCase();
        SpdyCodecUtil.validateHeaderName(lowerCaseName);
        int h = DefaultSpdyHeaders.hash(lowerCaseName);
        int i = DefaultSpdyHeaders.index(h);
        this.remove0(h, i, lowerCaseName);
        for (Object v : values) {
            if (v == null) break;
            String strVal = DefaultSpdyHeaders.toString(v);
            SpdyCodecUtil.validateHeaderValue(strVal);
            this.add0(h, i, lowerCaseName, strVal);
        }
        return this;
    }

    @Override
    public SpdyHeaders clear() {
        for (int i = 0; i < this.entries.length; ++i) {
            this.entries[i] = null;
        }
        this.head.before = this.head.after = this.head;
        return this;
    }

    @Override
    public String get(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        int h = DefaultSpdyHeaders.hash(name);
        int i = DefaultSpdyHeaders.index(h);
        HeaderEntry e = this.entries[i];
        while (e != null) {
            if (e.hash == h && DefaultSpdyHeaders.eq(name, e.key)) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    @Override
    public List<String> getAll(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        LinkedList<String> values = new LinkedList<String>();
        int h = DefaultSpdyHeaders.hash(name);
        int i = DefaultSpdyHeaders.index(h);
        HeaderEntry e = this.entries[i];
        while (e != null) {
            if (e.hash == h && DefaultSpdyHeaders.eq(name, e.key)) {
                values.addFirst(e.value);
            }
            e = e.next;
        }
        return values;
    }

    @Override
    public List<Map.Entry<String, String>> entries() {
        LinkedList<Map.Entry<String, String>> all = new LinkedList<Map.Entry<String, String>>();
        HeaderEntry e = this.head.after;
        while (e != this.head) {
            all.add(e);
            e = e.after;
        }
        return all;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return new HeaderIterator();
    }

    @Override
    public boolean contains(String name) {
        return this.get(name) != null;
    }

    @Override
    public Set<String> names() {
        TreeSet<String> names = new TreeSet<String>();
        HeaderEntry e = this.head.after;
        while (e != this.head) {
            names.add(e.key);
            e = e.after;
        }
        return names;
    }

    @Override
    public SpdyHeaders add(String name, Iterable<?> values) {
        SpdyCodecUtil.validateHeaderValue(name);
        int h = DefaultSpdyHeaders.hash(name);
        int i = DefaultSpdyHeaders.index(h);
        for (Object v : values) {
            String vstr = DefaultSpdyHeaders.toString(v);
            SpdyCodecUtil.validateHeaderValue(vstr);
            this.add0(h, i, name, vstr);
        }
        return this;
    }

    @Override
    public boolean isEmpty() {
        return this.head == this.head.after;
    }

    private static String toString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    private static final class HeaderEntry
    implements Map.Entry<String, String> {
        final int hash;
        final String key;
        String value;
        HeaderEntry next;
        HeaderEntry before;
        HeaderEntry after;

        HeaderEntry(int hash, String key, String value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        void remove() {
            this.before.after = this.after;
            this.after.before = this.before;
        }

        void addBefore(HeaderEntry e) {
            this.after = e;
            this.before = e.before;
            this.before.after = this;
            this.after.before = this;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public String setValue(String value) {
            if (value == null) {
                throw new NullPointerException("value");
            }
            SpdyCodecUtil.validateHeaderValue(value);
            String oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public String toString() {
            return this.key + '=' + this.value;
        }
    }

    private final class HeaderIterator
    implements Iterator<Map.Entry<String, String>> {
        private HeaderEntry current;

        private HeaderIterator() {
            this.current = DefaultSpdyHeaders.this.head;
        }

        @Override
        public boolean hasNext() {
            return this.current.after != DefaultSpdyHeaders.this.head;
        }

        @Override
        public Map.Entry<String, String> next() {
            this.current = this.current.after;
            if (this.current == DefaultSpdyHeaders.this.head) {
                throw new NoSuchElementException();
            }
            return this.current;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

