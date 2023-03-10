/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.types.minecraft.MetaTypeTemplate;
import io.netty.buffer.ByteBuf;

public abstract class OldMetaType
extends MetaTypeTemplate {
    private static final int END = 127;

    @Override
    public Metadata read(ByteBuf buffer) throws Exception {
        byte index = buffer.readByte();
        if (index == 127) {
            return null;
        }
        MetaType type = this.getType((index & 0xE0) >> 5);
        return new Metadata(index & 0x1F, type, type.type().read(buffer));
    }

    protected abstract MetaType getType(int var1);

    @Override
    public void write(ByteBuf buffer, Metadata object) throws Exception {
        if (object == null) {
            buffer.writeByte(127);
        } else {
            int index = (object.metaType().typeId() << 5 | object.id() & 0x1F) & 0xFF;
            buffer.writeByte(index);
            object.metaType().type().write(buffer, object.getValue());
        }
    }
}

