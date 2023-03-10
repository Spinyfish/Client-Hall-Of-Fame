/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;

public abstract class MetaTypeTemplate
extends Type<Metadata> {
    protected MetaTypeTemplate() {
        super("Metadata type", Metadata.class);
    }

    @Override
    public Class<? extends Type> getBaseClass() {
        return MetaTypeTemplate.class;
    }
}

