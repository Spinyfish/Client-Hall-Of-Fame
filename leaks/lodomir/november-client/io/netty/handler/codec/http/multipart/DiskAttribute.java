/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.AbstractDiskHttpData;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import java.io.IOException;

public class DiskAttribute
extends AbstractDiskHttpData
implements Attribute {
    public static String baseDirectory;
    public static boolean deleteOnExitTemporaryFile;
    public static final String prefix = "Attr_";
    public static final String postfix = ".att";

    public DiskAttribute(String name) {
        super(name, HttpConstants.DEFAULT_CHARSET, 0L);
    }

    public DiskAttribute(String name, String value) throws IOException {
        super(name, HttpConstants.DEFAULT_CHARSET, 0L);
        this.setValue(value);
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.Attribute;
    }

    @Override
    public String getValue() throws IOException {
        byte[] bytes = this.get();
        return new String(bytes, this.charset.name());
    }

    @Override
    public void setValue(String value) throws IOException {
        if (value == null) {
            throw new NullPointerException("value");
        }
        byte[] bytes = value.getBytes(this.charset.name());
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
        if (this.definedSize > 0L) {
            this.definedSize = buffer.readableBytes();
        }
        this.setContent(buffer);
    }

    @Override
    public void addContent(ByteBuf buffer, boolean last) throws IOException {
        int localsize = buffer.readableBytes();
        if (this.definedSize > 0L && this.definedSize < this.size + (long)localsize) {
            this.definedSize = this.size + (long)localsize;
        }
        super.addContent(buffer, last);
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Attribute)) {
            return false;
        }
        Attribute attribute = (Attribute)o;
        return this.getName().equalsIgnoreCase(attribute.getName());
    }

    @Override
    public int compareTo(InterfaceHttpData o) {
        if (!(o instanceof Attribute)) {
            throw new ClassCastException("Cannot compare " + (Object)((Object)this.getHttpDataType()) + " with " + (Object)((Object)o.getHttpDataType()));
        }
        return this.compareTo((Attribute)o);
    }

    @Override
    public int compareTo(Attribute o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }

    public String toString() {
        try {
            return this.getName() + '=' + this.getValue();
        }
        catch (IOException e) {
            return this.getName() + "=IoException";
        }
    }

    @Override
    protected boolean deleteOnExit() {
        return deleteOnExitTemporaryFile;
    }

    @Override
    protected String getBaseDirectory() {
        return baseDirectory;
    }

    @Override
    protected String getDiskFilename() {
        return this.getName() + postfix;
    }

    @Override
    protected String getPostfix() {
        return postfix;
    }

    @Override
    protected String getPrefix() {
        return prefix;
    }

    @Override
    public Attribute copy() {
        DiskAttribute attr = new DiskAttribute(this.getName());
        attr.setCharset(this.getCharset());
        ByteBuf content = this.content();
        if (content != null) {
            try {
                attr.setContent(content.copy());
            }
            catch (IOException e) {
                throw new ChannelException(e);
            }
        }
        return attr;
    }

    @Override
    public Attribute duplicate() {
        DiskAttribute attr = new DiskAttribute(this.getName());
        attr.setCharset(this.getCharset());
        ByteBuf content = this.content();
        if (content != null) {
            try {
                attr.setContent(content.duplicate());
            }
            catch (IOException e) {
                throw new ChannelException(e);
            }
        }
        return attr;
    }

    @Override
    public Attribute retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public Attribute retain() {
        super.retain();
        return this;
    }

    static {
        deleteOnExitTemporaryFile = true;
    }
}

