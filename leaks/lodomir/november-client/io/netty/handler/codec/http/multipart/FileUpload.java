/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.multipart.HttpData;

public interface FileUpload
extends HttpData {
    public String getFilename();

    public void setFilename(String var1);

    public void setContentType(String var1);

    public String getContentType();

    public void setContentTransferEncoding(String var1);

    public String getContentTransferEncoding();

    @Override
    public FileUpload copy();

    @Override
    public FileUpload duplicate();

    @Override
    public FileUpload retain();

    @Override
    public FileUpload retain(int var1);
}

