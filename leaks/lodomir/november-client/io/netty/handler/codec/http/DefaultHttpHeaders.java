/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderDateFormat;
import io.netty.handler.codec.http.HttpHeaders;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class DefaultHttpHeaders
extends HttpHeaders {
    private static final int BUCKET_SIZE = 17;
    private final HeaderEntry[] entries = new HeaderEntry[17];
    private final HeaderEntry head = new HeaderEntry();
    protected final boolean validate;

    private static int index(int hash) {
        return hash % 17;
    }

    public DefaultHttpHeaders() {
        this(true);
    }

    public DefaultHttpHeaders(boolean validate) {
        this.validate = validate;
        this.head.before = this.head.after = this.head;
    }

    void validateHeaderName0(CharSequence headerName) {
        DefaultHttpHeaders.validateHeaderName(headerName);
    }

    @Override
    public HttpHeaders add(HttpHeaders headers) {
        if (headers instanceof DefaultHttpHeaders) {
            DefaultHttpHeaders defaultHttpHeaders = (DefaultHttpHeaders)headers;
            HeaderEntry e = defaultHttpHeaders.head.after;
            while (e != defaultHttpHeaders.head) {
                this.add(e.key, (Object)e.value);
                e = e.after;
            }
            return this;
        }
        return super.add(headers);
    }

    @Override
    public HttpHeaders set(HttpHeaders headers) {
        if (headers instanceof DefaultHttpHeaders) {
            this.clear();
            DefaultHttpHeaders defaultHttpHeaders = (DefaultHttpHeaders)headers;
            HeaderEntry e = defaultHttpHeaders.head.after;
            while (e != defaultHttpHeaders.head) {
                this.add(e.key, (Object)e.value);
                e = e.after;
            }
            return this;
        }
        return super.set(headers);
    }

    @Override
    public HttpHeaders add(String name, Object value) {
        return this.add((CharSequence)name, value);
    }

    @Override
    public HttpHeaders add(CharSequence name, Object value) {
        CharSequence strVal;
        if (this.validate) {
            this.validateHeaderName0(name);
            strVal = DefaultHttpHeaders.toCharSequence(value);
            DefaultHttpHeaders.validateHeaderValue(strVal);
        } else {
            strVal = DefaultHttpHeaders.toCharSequence(value);
        }
        int h = DefaultHttpHeaders.hash(name);
        int i = DefaultHttpHeaders.index(h);
        this.add0(h, i, name, strVal);
        return this;
    }

    @Override
    public HttpHeaders add(String name, Iterable<?> values) {
        return this.add((CharSequence)name, values);
    }

    @Override
    public HttpHeaders add(CharSequence name, Iterable<?> values) {
        if (this.validate) {
            this.validateHeaderName0(name);
        }
        int h = DefaultHttpHeaders.hash(name);
        int i = DefaultHttpHeaders.index(h);
        for (Object v : values) {
            CharSequence vstr = DefaultHttpHeaders.toCharSequence(v);
            if (this.validate) {
                DefaultHttpHeaders.validateHeaderValue(vstr);
            }
            this.add0(h, i, name, vstr);
        }
        return this;
    }

    private void add0(int h, int i, CharSequence name, CharSequence value) {
        HeaderEntry newEntry;
        HeaderEntry e = this.entries[i];
        this.entries[i] = newEntry = new HeaderEntry(h, name, value);
        newEntry.next = e;
        newEntry.addBefore(this.head);
    }

    @Override
    public HttpHeaders remove(String name) {
        return this.remove((CharSequence)name);
    }

    @Override
    public HttpHeaders remove(CharSequence name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        int h = DefaultHttpHeaders.hash(name);
        int i = DefaultHttpHeaders.index(h);
        this.remove0(h, i, name);
        return this;
    }

    private void remove0(int h, int i, CharSequence name) {
        HeaderEntry next;
        HeaderEntry e = this.entries[i];
        if (e == null) {
            return;
        }
        while (e.hash == h && DefaultHttpHeaders.equalsIgnoreCase(name, e.key)) {
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
            if (next.hash == h && DefaultHttpHeaders.equalsIgnoreCase(name, next.key)) {
                e.next = next.next;
                next.remove();
                continue;
            }
            e = next;
        }
    }

    @Override
    public HttpHeaders set(String name, Object value) {
        return this.set((CharSequence)name, value);
    }

    @Override
    public HttpHeaders set(CharSequence name, Object value) {
        CharSequence strVal;
        if (this.validate) {
            this.validateHeaderName0(name);
            strVal = DefaultHttpHeaders.toCharSequence(value);
            DefaultHttpHeaders.validateHeaderValue(strVal);
        } else {
            strVal = DefaultHttpHeaders.toCharSequence(value);
        }
        int h = DefaultHttpHeaders.hash(name);
        int i = DefaultHttpHeaders.index(h);
        this.remove0(h, i, name);
        this.add0(h, i, name, strVal);
        return this;
    }

    @Override
    public HttpHeaders set(String name, Iterable<?> values) {
        return this.set((CharSequence)name, values);
    }

    @Override
    public HttpHeaders set(CharSequence name, Iterable<?> values) {
        if (values == null) {
            throw new NullPointerException("values");
        }
        if (this.validate) {
            this.validateHeaderName0(name);
        }
        int h = DefaultHttpHeaders.hash(name);
        int i = DefaultHttpHeaders.index(h);
        this.remove0(h, i, name);
        for (Object v : values) {
            if (v == null) break;
            CharSequence strVal = DefaultHttpHeaders.toCharSequence(v);
            if (this.validate) {
                DefaultHttpHeaders.validateHeaderValue(strVal);
            }
            this.add0(h, i, name, strVal);
        }
        return this;
    }

    @Override
    public HttpHeaders clear() {
        Arrays.fill(this.entries, null);
        this.head.before = this.head.after = this.head;
        return this;
    }

    @Override
    public String get(String name) {
        return this.get((CharSequence)name);
    }

    @Override
    public String get(CharSequence name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        int h = DefaultHttpHeaders.hash(name);
        int i = DefaultHttpHeaders.index(h);
        HeaderEntry e = this.entries[i];
        CharSequence value = null;
        while (e != null) {
            if (e.hash == h && DefaultHttpHeaders.equalsIgnoreCase(name, e.key)) {
                value = e.value;
            }
            e = e.next;
        }
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    @Override
    public List<String> getAll(String name) {
        return this.getAll((CharSequence)name);
    }

    @Override
    public List<String> getAll(CharSequence name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        LinkedList<String> values = new LinkedList<String>();
        int h = DefaultHttpHeaders.hash(name);
        int i = DefaultHttpHeaders.index(h);
        HeaderEntry e = this.entries[i];
        while (e != null) {
            if (e.hash == h && DefaultHttpHeaders.equalsIgnoreCase(name, e.key)) {
                values.addFirst(e.getValue());
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
    public boolean contains(CharSequence name) {
        return this.get(name) != null;
    }

    @Override
    public boolean isEmpty() {
        return this.head == this.head.after;
    }

    @Override
    public boolean contains(String name, String value, boolean ignoreCaseValue) {
        return this.contains((CharSequence)name, (CharSequence)value, ignoreCaseValue);
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value, boolean ignoreCaseValue) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        int h = DefaultHttpHeaders.hash(name);
        int i = DefaultHttpHeaders.index(h);
        HeaderEntry e = this.entries[i];
        while (e != null) {
            if (e.hash == h && DefaultHttpHeaders.equalsIgnoreCase(name, e.key) && (ignoreCaseValue ? DefaultHttpHeaders.equalsIgnoreCase(e.value, value) : e.value.equals(value))) {
                return true;
            }
            e = e.next;
        }
        return false;
    }

    @Override
    public Set<String> names() {
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        HeaderEntry e = this.head.after;
        while (e != this.head) {
            names.add(e.getKey());
            e = e.after;
        }
        return names;
    }

    private static CharSequence toCharSequence(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof CharSequence) {
            return (CharSequence)value;
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Date) {
            return HttpHeaderDateFormat.get().format((Date)value);
        }
        if (value instanceof Calendar) {
            return HttpHeaderDateFormat.get().format(((Calendar)value).getTime());
        }
        return value.toString();
    }

    void encode(ByteBuf buf) {
        HeaderEntry e = this.head.after;
        while (e != this.head) {
            e.encode(buf);
            e = e.after;
        }
    }

    private final class HeaderEntry
    implements Map.Entry<String, String> {
        final int hash;
        final CharSequence key;
        CharSequence value;
        HeaderEntry next;
        HeaderEntry before;
        HeaderEntry after;

        HeaderEntry(int hash, CharSequence key, CharSequence value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        HeaderEntry() {
            this.hash = -1;
            this.key = null;
            this.value = null;
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
            return this.key.toString();
        }

        @Override
        public String getValue() {
            return this.value.toString();
        }

        @Override
        public String setValue(String value) {
            if (value == null) {
                throw new NullPointerException("value");
            }
            HttpHeaders.validateHeaderValue(value);
            CharSequence oldValue = this.value;
            this.value = value;
            return oldValue.toString();
        }

        public String toString() {
            return this.key.toString() + '=' + this.value.toString();
        }

        void encode(ByteBuf buf) {
            HttpHeaders.encode(this.key, this.value, buf);
        }
    }

    private final class HeaderIterator
    implements Iterator<Map.Entry<String, String>> {
        private HeaderEntry current;

        private HeaderIterator() {
            this.current = DefaultHttpHeaders.this.head;
        }

        @Override
        public boolean hasNext() {
            return this.current.after != DefaultHttpHeaders.this.head;
        }

        @Override
        public Map.Entry<String, String> next() {
            this.current = this.current.after;
            if (this.current == DefaultHttpHeaders.this.head) {
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

