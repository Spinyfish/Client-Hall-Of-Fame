/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntityImpl;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;

public class BlockEntityType1_18
extends Type<BlockEntity> {
    public BlockEntityType1_18() {
        super(BlockEntity.class);
    }

    @Override
    public BlockEntity read(ByteBuf buffer) throws Exception {
        byte xz = buffer.readByte();
        short y = buffer.readShort();
        int typeId = Type.VAR_INT.readPrimitive(buffer);
        CompoundTag tag = (CompoundTag)Type.NBT.read(buffer);
        return new BlockEntityImpl(xz, y, typeId, tag);
    }

    @Override
    public void write(ByteBuf buffer, BlockEntity entity) throws Exception {
        buffer.writeByte(entity.packedXZ());
        buffer.writeShort(entity.y());
        Type.VAR_INT.writePrimitive(buffer, entity.typeId());
        Type.NBT.write(buffer, entity.tag());
    }
}

