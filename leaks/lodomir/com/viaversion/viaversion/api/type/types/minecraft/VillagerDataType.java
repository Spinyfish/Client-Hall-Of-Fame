/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.VillagerData;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class VillagerDataType
extends Type<VillagerData> {
    public VillagerDataType() {
        super(VillagerData.class);
    }

    @Override
    public VillagerData read(ByteBuf buffer) throws Exception {
        return new VillagerData(Type.VAR_INT.readPrimitive(buffer), Type.VAR_INT.readPrimitive(buffer), Type.VAR_INT.readPrimitive(buffer));
    }

    @Override
    public void write(ByteBuf buffer, VillagerData object) throws Exception {
        Type.VAR_INT.writePrimitive(buffer, object.type());
        Type.VAR_INT.writePrimitive(buffer, object.profession());
        Type.VAR_INT.writePrimitive(buffer, object.level());
    }
}

