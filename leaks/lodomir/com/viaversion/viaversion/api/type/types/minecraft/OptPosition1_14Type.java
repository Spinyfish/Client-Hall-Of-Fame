/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class OptPosition1_14Type
extends Type<Position> {
    public OptPosition1_14Type() {
        super(Position.class);
    }

    @Override
    public Position read(ByteBuf buffer) throws Exception {
        boolean present = buffer.readBoolean();
        if (!present) {
            return null;
        }
        return (Position)Type.POSITION1_14.read(buffer);
    }

    @Override
    public void write(ByteBuf buffer, Position object) throws Exception {
        buffer.writeBoolean(object != null);
        if (object != null) {
            Type.POSITION1_14.write(buffer, object);
        }
    }
}

