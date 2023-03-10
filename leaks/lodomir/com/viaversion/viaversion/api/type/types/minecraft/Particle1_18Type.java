/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.AbstractParticleType;

@Deprecated
public class Particle1_18Type
extends AbstractParticleType {
    public Particle1_18Type() {
        this.readers.put(2, this.blockHandler());
        this.readers.put(3, this.blockHandler());
        this.readers.put(24, this.blockHandler());
        this.readers.put(14, this.dustHandler());
        this.readers.put(15, this.dustTransitionHandler());
        this.readers.put(35, this.itemHandler(Type.FLAT_VAR_INT_ITEM));
        this.readers.put(36, this.vibrationHandler(Type.POSITION1_14));
    }
}

