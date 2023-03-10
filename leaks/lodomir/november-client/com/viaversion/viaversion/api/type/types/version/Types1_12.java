/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListType;
import com.viaversion.viaversion.api.type.types.version.Metadata1_12Type;
import java.util.List;

public class Types1_12 {
    public static final Type<Metadata> METADATA = new Metadata1_12Type();
    public static final Type<List<Metadata>> METADATA_LIST = new MetaListType(METADATA);
}

