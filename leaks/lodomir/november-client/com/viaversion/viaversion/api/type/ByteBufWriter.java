/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type;

import io.netty.buffer.ByteBuf;

public interface ByteBufWriter<T> {
    public void write(ByteBuf var1, T var2) throws Exception;
}

