/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public abstract class PartialType<T, X>
extends Type<T> {
    private final X param;

    protected PartialType(X param, Class<T> type) {
        super(type);
        this.param = param;
    }

    protected PartialType(X param, String name, Class<T> type) {
        super(name, type);
        this.param = param;
    }

    public abstract T read(ByteBuf var1, X var2) throws Exception;

    public abstract void write(ByteBuf var1, X var2, T var3) throws Exception;

    @Override
    public final T read(ByteBuf buffer) throws Exception {
        return this.read(buffer, this.param);
    }

    @Override
    public final void write(ByteBuf buffer, T object) throws Exception {
        this.write(buffer, this.param, object);
    }
}

