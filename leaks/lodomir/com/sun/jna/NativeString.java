/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import java.nio.CharBuffer;

class NativeString
implements CharSequence,
Comparable {
    static final String WIDE_STRING = "--WIDE-STRING--";
    private Pointer pointer;
    private String encoding;

    public NativeString(String string) {
        this(string, Native.getDefaultStringEncoding());
    }

    public NativeString(String string, boolean wide) {
        this(string, wide ? WIDE_STRING : Native.getDefaultStringEncoding());
    }

    public NativeString(WString string) {
        this(string.toString(), WIDE_STRING);
    }

    public NativeString(String string, String encoding) {
        if (string == null) {
            throw new NullPointerException("String must not be null");
        }
        this.encoding = encoding;
        if (WIDE_STRING.equals(this.encoding)) {
            int len = (string.length() + 1) * Native.WCHAR_SIZE;
            this.pointer = new StringMemory(len);
            this.pointer.setWideString(0L, string);
        } else {
            byte[] data = Native.getBytes(string, encoding);
            this.pointer = new StringMemory(data.length + 1);
            this.pointer.write(0L, data, 0, data.length);
            this.pointer.setByte(data.length, (byte)0);
        }
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean equals(Object other) {
        if (other instanceof CharSequence) {
            return this.compareTo(other) == 0;
        }
        return false;
    }

    @Override
    public String toString() {
        boolean wide = WIDE_STRING.equals(this.encoding);
        String s = wide ? "const wchar_t*" : "const char*";
        s = s + "(" + (wide ? this.pointer.getWideString(0L) : this.pointer.getString(0L, this.encoding)) + ")";
        return s;
    }

    public Pointer getPointer() {
        return this.pointer;
    }

    @Override
    public char charAt(int index) {
        return this.toString().charAt(index);
    }

    @Override
    public int length() {
        return this.toString().length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return CharBuffer.wrap(this.toString()).subSequence(start, end);
    }

    public int compareTo(Object other) {
        if (other == null) {
            return 1;
        }
        return this.toString().compareTo(other.toString());
    }

    private class StringMemory
    extends Memory {
        public StringMemory(long size) {
            super(size);
        }

        @Override
        public String toString() {
            return NativeString.this.toString();
        }
    }
}

