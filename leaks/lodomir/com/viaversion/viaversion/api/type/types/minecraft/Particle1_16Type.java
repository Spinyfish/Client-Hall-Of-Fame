/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.AbstractParticleType;

@Deprecated
public class Particle1_16Type
extends AbstractParticleType {
    public Particle1_16Type() {
        this.readers.put(3, this.blockHandler());
        this.readers.put(23, this.blockHandler());
        this.readers.put(14, this.dustHandler());
        this.readers.put(34, this.itemHandler(Type.FLAT_VAR_INT_ITEM));
    }
}

