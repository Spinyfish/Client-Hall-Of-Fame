/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.rewriter;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.rewriter.Rewriter;

public abstract class RewriterBase<T extends Protocol>
implements Rewriter<T> {
    protected final T protocol;

    protected RewriterBase(T protocol) {
        this.protocol = protocol;
    }

    @Override
    public final void register() {
        this.registerPackets();
        this.registerRewrites();
    }

    protected void registerPackets() {
    }

    protected void registerRewrites() {
    }

    @Override
    public T protocol() {
        return this.protocol;
    }
}

