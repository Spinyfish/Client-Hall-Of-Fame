/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public interface HttpData
extends InterfaceHttpData,
ByteBufHolder {
    public void setContent(ByteBuf var1) throws IOException;

    public void addContent(ByteBuf var1, boolean var2) throws IOException;

    public void setContent(File var1) throws IOException;

    public void setContent(InputStream var1) throws IOException;

    public boolean isCompleted();

    public long length();

    public void delete();

    public byte[] get() throws IOException;

    public ByteBuf getByteBuf() throws IOException;

    public ByteBuf getChunk(int var1) throws IOException;

    public String getString() throws IOException;

    public String getString(Charset var1) throws IOException;

    public void setCharset(Charset var1);

    public Charset getCharset();

    public boolean renameTo(File var1) throws IOException;

    public boolean isInMemory();

    public File getFile() throws IOException;

    @Override
    public HttpData copy();

    @Override
    public HttpData duplicate();

    @Override
    public HttpData retain();

    @Override
    public HttpData retain(int var1);
}

