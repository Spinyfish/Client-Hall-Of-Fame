/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type;

import io.netty.buffer.ByteBuf;

public interface ByteBufReader<T> {
    public T read(ByteBuf var1) throws Exception;
}

