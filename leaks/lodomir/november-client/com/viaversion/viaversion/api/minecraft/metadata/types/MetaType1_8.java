/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft.metadata.types;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.Type;

public enum MetaType1_8 implements MetaType
{
    Byte(0, Type.BYTE),
    Short(1, Type.SHORT),
    Int(2, Type.INT),
    Float(3, Type.FLOAT),
    String(4, Type.STRING),
    Slot(5, Type.ITEM),
    Position(6, Type.VECTOR),
    Rotation(7, Type.ROTATION),
    NonExistent(-1, Type.NOTHING);

    private final int typeID;
    private final Type type;

    private MetaType1_8(int typeID, Type type) {
        this.typeID = typeID;
        this.type = type;
    }

    public static MetaType1_8 byId(int id) {
        return MetaType1_8.values()[id];
    }

    @Override
    public int typeId() {
        return this.typeID;
    }

    @Override
    public Type type() {
        return this.type;
    }
}

