/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft.blockentity;

import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;

public final class BlockEntityImpl
implements BlockEntity {
    private final byte packedXZ;
    private final short y;
    private final int typeId;
    private final CompoundTag tag;

    public BlockEntityImpl(byte packedXZ, short y, int typeId, CompoundTag tag) {
        this.packedXZ = packedXZ;
        this.y = y;
        this.typeId = typeId;
        this.tag = tag;
    }

    @Override
    public byte packedXZ() {
        return this.packedXZ;
    }

    @Override
    public short y() {
        return this.y;
    }

    @Override
    public int typeId() {
        return this.typeId;
    }

    @Override
    public CompoundTag tag() {
        return this.tag;
    }
}

