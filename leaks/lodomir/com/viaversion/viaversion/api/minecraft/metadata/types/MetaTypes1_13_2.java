/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft.metadata.types;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.types.AbstractMetaTypes;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.ParticleType;

public final class MetaTypes1_13_2
extends AbstractMetaTypes {
    public final MetaType byteType = this.add(0, Type.BYTE);
    public final MetaType varIntType = this.add(1, Type.VAR_INT);
    public final MetaType floatType = this.add(2, Type.FLOAT);
    public final MetaType stringType = this.add(3, Type.STRING);
    public final MetaType componentType = this.add(4, Type.COMPONENT);
    public final MetaType optionalComponentType = this.add(5, Type.OPTIONAL_COMPONENT);
    public final MetaType itemType = this.add(6, Type.FLAT_VAR_INT_ITEM);
    public final MetaType booleanType = this.add(7, Type.BOOLEAN);
    public final MetaType rotationType = this.add(8, Type.ROTATION);
    public final MetaType positionType = this.add(9, Type.POSITION);
    public final MetaType optionalPositionType = this.add(10, Type.OPTIONAL_POSITION);
    public final MetaType directionType = this.add(11, Type.VAR_INT);
    public final MetaType optionalUUIDType = this.add(12, Type.OPTIONAL_UUID);
    public final MetaType blockStateType = this.add(13, Type.VAR_INT);
    public final MetaType nbtType = this.add(14, Type.NBT);
    public final MetaType particleType;

    public MetaTypes1_13_2(ParticleType particleType) {
        super(16);
        this.particleType = this.add(15, particleType);
    }
}

