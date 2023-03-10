/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.TagData;
import com.viaversion.viaversion.util.Int2IntBiMap;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface MappingData {
    public void load();

    public int getNewBlockStateId(int var1);

    public int getNewBlockId(int var1);

    public int getNewItemId(int var1);

    public int getOldItemId(int var1);

    public int getNewParticleId(int var1);

    public @Nullable List<TagData> getTags(RegistryType var1);

    public @Nullable Int2IntBiMap getItemMappings();

    public @Nullable ParticleMappings getParticleMappings();

    public @Nullable Mappings getBlockMappings();

    public @Nullable Mappings getBlockEntityMappings();

    public @Nullable Mappings getBlockStateMappings();

    public @Nullable Mappings getSoundMappings();

    public @Nullable Mappings getStatisticsMappings();
}

