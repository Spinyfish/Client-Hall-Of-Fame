/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListType;
import com.viaversion.viaversion.api.type.types.version.ChunkSectionType1_8;
import com.viaversion.viaversion.api.type.types.version.Metadata1_8Type;
import java.util.List;

public class Types1_8 {
    public static final Type<Metadata> METADATA = new Metadata1_8Type();
    public static final Type<List<Metadata>> METADATA_LIST = new MetaListType(METADATA);
    public static final Type<ChunkSection> CHUNK_SECTION = new ChunkSectionType1_8();
}

