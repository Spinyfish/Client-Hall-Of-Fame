/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;

final class HttpHeaderEntity
implements CharSequence {
    private final String name;
    private final int hash;
    private final byte[] bytes;
    private final int separatorLen;

    public HttpHeaderEntity(String name) {
        this(name, null);
    }

    public HttpHeaderEntity(String name, byte[] separator) {
        this.name = name;
        this.hash = HttpHeaders.hash(name);
        byte[] nameBytes = name.getBytes(CharsetUtil.US_ASCII);
        if (separator == null) {
            this.bytes = nameBytes;
            this.separatorLen = 0;
        } else {
            this.separatorLen = separator.length;
            this.bytes = new byte[nameBytes.length + separator.length];
            System.arraycopy(nameBytes, 0, this.bytes, 0, nameBytes.length);
            System.arraycopy(separator, 0, this.bytes, nameBytes.length, separator.length);
        }
    }

    int hash() {
        return this.hash;
    }

    @Override
    public int length() {
        return this.bytes.length - this.separatorLen;
    }

    @Override
    public char charAt(int index) {
        if (this.bytes.length - this.separatorLen <= index) {
            throw new IndexOutOfBoundsException();
        }
        return (char)this.bytes[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new HttpHeaderEntity(this.name.substring(start, end));
    }

    @Override
    public String toString() {
        return this.name;
    }

    boolean encode(ByteBuf buf) {
        buf.writeBytes(this.bytes);
        return this.separatorLen > 0;
    }
}

