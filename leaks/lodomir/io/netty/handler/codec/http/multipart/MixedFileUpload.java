/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryFileUpload;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MixedFileUpload
implements FileUpload {
    private FileUpload fileUpload;
    private final long limitSize;
    private final long definedSize;

    public MixedFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size, long limitSize) {
        this.limitSize = limitSize;
        this.fileUpload = size > this.limitSize ? new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size) : new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
        this.definedSize = size;
    }

    @Override
    public void addContent(ByteBuf buffer, boolean last) throws IOException {
        if (this.fileUpload instanceof MemoryFileUpload && this.fileUpload.length() + (long)buffer.readableBytes() > this.limitSize) {
            DiskFileUpload diskFileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize);
            ByteBuf data = this.fileUpload.getByteBuf();
            if (data != null && data.isReadable()) {
                diskFileUpload.addContent(data.retain(), false);
            }
            this.fileUpload.release();
            this.fileUpload = diskFileUpload;
        }
        this.fileUpload.addContent(buffer, last);
    }

    @Override
    public void delete() {
        this.fileUpload.delete();
    }

    @Override
    public byte[] get() throws IOException {
        return this.fileUpload.get();
    }

    @Override
    public ByteBuf getByteBuf() throws IOException {
        return this.fileUpload.getByteBuf();
    }

    @Override
    public Charset getCharset() {
        return this.fileUpload.getCharset();
    }

    @Override
    public String getContentType() {
        return this.fileUpload.getContentType();
    }

    @Override
    public String getContentTransferEncoding() {
        return this.fileUpload.getContentTransferEncoding();
    }

    @Override
    public String getFilename() {
        return this.fileUpload.getFilename();
    }

    @Override
    public String getString() throws IOException {
        return this.fileUpload.getString();
    }

    @Override
    public String getString(Charset encoding) throws IOException {
        return this.fileUpload.getString(encoding);
    }

    @Override
    public boolean isCompleted() {
        return this.fileUpload.isCompleted();
    }

    @Override
    public boolean isInMemory() {
        return this.fileUpload.isInMemory();
    }

    @Override
    public long length() {
        return this.fileUpload.length();
    }

    @Override
    public boolean renameTo(File dest) throws IOException {
        return this.fileUpload.renameTo(dest);
    }

    @Override
    public void setCharset(Charset charset) {
        this.fileUpload.setCharset(charset);
    }

    @Override
    public void setContent(ByteBuf buffer) throws IOException {
        if ((long)buffer.readableBytes() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
            FileUpload memoryUpload = this.fileUpload;
            this.fileUpload = new DiskFileUpload(memoryUpload.getName(), memoryUpload.getFilename(), memoryUpload.getContentType(), memoryUpload.getContentTransferEncoding(), memoryUpload.getCharset(), this.definedSize);
            memoryUpload.release();
        }
        this.fileUpload.setContent(buffer);
    }

    @Override
    public void setContent(File file) throws IOException {
        if (file.length() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
            FileUpload memoryUpload = this.fileUpload;
            this.fileUpload = new DiskFileUpload(memoryUpload.getName(), memoryUpload.getFilename(), memoryUpload.getContentType(), memoryUpload.getContentTransferEncoding(), memoryUpload.getCharset(), this.definedSize);
            memoryUpload.release();
        }
        this.fileUpload.setContent(file);
    }

    @Override
    public void setContent(InputStream inputStream) throws IOException {
        if (this.fileUpload instanceof MemoryFileUpload) {
            FileUpload memoryUpload = this.fileUpload;
            this.fileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize);
            memoryUpload.release();
        }
        this.fileUpload.setContent(inputStream);
    }

    @Override
    public void setContentType(String contentType) {
        this.fileUpload.setContentType(contentType);
    }

    @Override
    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.fileUpload.setContentTransferEncoding(contentTransferEncoding);
    }

    @Override
    public void setFilename(String filename) {
        this.fileUpload.setFilename(filename);
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return this.fileUpload.getHttpDataType();
    }

    @Override
    public String getName() {
        return this.fileUpload.getName();
    }

    @Override
    public int compareTo(InterfaceHttpData o) {
        return this.fileUpload.compareTo(o);
    }

    public String toString() {
        return "Mixed: " + this.fileUpload.toString();
    }

    @Override
    public ByteBuf getChunk(int length) throws IOException {
        return this.fileUpload.getChunk(length);
    }

    @Override
    public File getFile() throws IOException {
        return this.fileUpload.getFile();
    }

    @Override
    public FileUpload copy() {
        return this.fileUpload.copy();
    }

    @Override
    public FileUpload duplicate() {
        return this.fileUpload.duplicate();
    }

    @Override
    public ByteBuf content() {
        return this.fileUpload.content();
    }

    @Override
    public int refCnt() {
        return this.fileUpload.refCnt();
    }

    @Override
    public FileUpload retain() {
        this.fileUpload.retain();
        return this;
    }

    @Override
    public FileUpload retain(int increment) {
        this.fileUpload.retain(increment);
        return this;
    }

    @Override
    public boolean release() {
        return this.fileUpload.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.fileUpload.release(decrement);
    }
}

