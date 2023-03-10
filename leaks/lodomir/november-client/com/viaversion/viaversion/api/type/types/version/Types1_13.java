/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaTypes1_13;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListType;
import com.viaversion.viaversion.api.type.types.minecraft.ParticleType;
import com.viaversion.viaversion.api.type.types.version.ChunkSectionType1_13;
import com.viaversion.viaversion.api.type.types.version.MetadataType;
import java.util.List;

public final class Types1_13 {
    public static final Type<ChunkSection> CHUNK_SECTION = new ChunkSectionType1_13();
    public static final ParticleType PARTICLE = new ParticleType();
    public static final MetaTypes1_13 META_TYPES = new MetaTypes1_13(PARTICLE);
    public static final Type<Metadata> METADATA = new MetadataType(META_TYPES);
    public static final Type<List<Metadata>> METADATA_LIST = new MetaListType(METADATA);
}

