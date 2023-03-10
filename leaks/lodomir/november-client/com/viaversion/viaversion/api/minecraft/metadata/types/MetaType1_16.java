/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft.metadata.types;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_16;

@Deprecated
public enum MetaType1_16 implements MetaType
{
    BYTE(0, Type.BYTE),
    VAR_INT(1, Type.VAR_INT),
    FLOAT(2, Type.FLOAT),
    STRING(3, Type.STRING),
    COMPONENT(4, Type.COMPONENT),
    OPT_COMPONENT(5, Type.OPTIONAL_COMPONENT),
    ITEM(6, Type.FLAT_VAR_INT_ITEM),
    BOOLEAN(7, Type.BOOLEAN),
    ROTATION(8, Type.ROTATION),
    POSITION(9, Type.POSITION1_14),
    OPT_POSITION(10, Type.OPTIONAL_POSITION_1_14),
    DIRECTION(11, Type.VAR_INT),
    OPT_UUID(12, Type.OPTIONAL_UUID),
    BLOCK_STATE(13, Type.VAR_INT),
    NBT(14, Type.NBT),
    PARTICLE(15, Types1_16.PARTICLE),
    VILLAGER_DATA(16, Type.VILLAGER_DATA),
    OPT_VAR_INT(17, Type.OPTIONAL_VAR_INT),
    POSE(18, Type.VAR_INT);

    private static final MetaType1_16[] VALUES;
    private final int typeId;
    private final Type type;

    private MetaType1_16(int typeId, Type type) {
        this.typeId = typeId;
        this.type = type;
    }

    public static MetaType1_16 byId(int id) {
        return VALUES[id];
    }

    @Override
    public int typeId() {
        return this.typeId;
    }

    @Override
    public Type type() {
        return this.type;
    }

    static {
        VALUES = MetaType1_16.values();
    }
}

