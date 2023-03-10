/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft.metadata.types;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaTypes;
import com.viaversion.viaversion.api.type.Type;

public abstract class AbstractMetaTypes
implements MetaTypes {
    private final MetaType[] values;

    protected AbstractMetaTypes(int values) {
        this.values = new MetaType[values];
    }

    @Override
    public MetaType byId(int id) {
        return this.values[id];
    }

    @Override
    public MetaType[] values() {
        return this.values;
    }

    protected MetaType add(int typeId, Type<?> type) {
        MetaType metaType;
        this.values[typeId] = metaType = MetaType.create(typeId, type);
        return metaType;
    }
}

