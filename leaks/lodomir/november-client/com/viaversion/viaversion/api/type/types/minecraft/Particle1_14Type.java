/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.AbstractParticleType;

@Deprecated
public class Particle1_14Type
extends AbstractParticleType {
    public Particle1_14Type() {
        this.readers.put(3, this.blockHandler());
        this.readers.put(23, this.blockHandler());
        this.readers.put(14, this.dustHandler());
        this.readers.put(32, this.itemHandler(Type.FLAT_VAR_INT_ITEM));
    }
}

