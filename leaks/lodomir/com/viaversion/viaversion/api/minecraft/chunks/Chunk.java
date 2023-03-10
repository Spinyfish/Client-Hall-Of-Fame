/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.minecraft.chunks;

import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.util.BitSet;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Chunk {
    public int getX();

    public int getZ();

    public boolean isBiomeData();

    public boolean isFullChunk();

    public boolean isIgnoreOldLightData();

    public void setIgnoreOldLightData(boolean var1);

    public int getBitmask();

    public void setBitmask(int var1);

    public @Nullable BitSet getChunkMask();

    public void setChunkMask(BitSet var1);

    public @Nullable ChunkSection[] getSections();

    public void setSections(ChunkSection[] var1);

    public int @Nullable [] getBiomeData();

    public void setBiomeData(int @Nullable [] var1);

    public @Nullable CompoundTag getHeightMap();

    public void setHeightMap(@Nullable CompoundTag var1);

    public List<CompoundTag> getBlockEntities();

    public List<BlockEntity> blockEntities();
}

