/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.rewriter.meta;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;

@FunctionalInterface
public interface MetaHandler {
    public void handle(MetaHandlerEvent var1, Metadata var2);
}

