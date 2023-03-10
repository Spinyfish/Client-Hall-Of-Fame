/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.viaversion.viaversion.api.type.types;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class StringType
extends Type<String> {
    private static final int maxJavaCharUtf8Length = Character.toString('\uffff').getBytes(StandardCharsets.UTF_8).length;
    private final int maxLength;

    public StringType() {
        this(Short.MAX_VALUE);
    }

    public StringType(int maxLength) {
        super(String.class);
        this.maxLength = maxLength;
    }

    @Override
    public String read(ByteBuf buffer) throws Exception {
        int len = Type.VAR_INT.readPrimitive(buffer);
        Preconditions.checkArgument((len <= this.maxLength * maxJavaCharUtf8Length ? 1 : 0) != 0, (String)("Cannot receive string longer than Short.MAX_VALUE * " + maxJavaCharUtf8Length + " bytes (got %s bytes)"), (Object[])new Object[]{len});
        String string = buffer.toString(buffer.readerIndex(), len, StandardCharsets.UTF_8);
        buffer.skipBytes(len);
        Preconditions.checkArgument((string.length() <= this.maxLength ? 1 : 0) != 0, (String)"Cannot receive string longer than Short.MAX_VALUE characters (got %s bytes)", (Object[])new Object[]{string.length()});
        return string;
    }

    @Override
    public void write(ByteBuf buffer, String object) throws Exception {
        Preconditions.checkArgument((object.length() <= this.maxLength ? 1 : 0) != 0, (String)"Cannot send string longer than Short.MAX_VALUE (got %s characters)", (Object[])new Object[]{object.length()});
        byte[] b = object.getBytes(StandardCharsets.UTF_8);
        Type.VAR_INT.writePrimitive(buffer, b.length);
        buffer.writeBytes(b);
    }
}

