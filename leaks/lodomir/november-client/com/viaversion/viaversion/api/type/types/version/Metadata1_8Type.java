/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_8;
import com.viaversion.viaversion.api.type.types.minecraft.OldMetaType;

public class Metadata1_8Type
extends OldMetaType {
    @Override
    protected MetaType getType(int index) {
        return MetaType1_8.byId(index);
    }
}

