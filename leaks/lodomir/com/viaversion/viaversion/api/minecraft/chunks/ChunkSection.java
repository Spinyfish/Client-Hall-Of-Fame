/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.minecraft.chunks;

import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionLight;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ChunkSection {
    public static final int SIZE = 4096;
    public static final int BIOME_SIZE = 64;

    public static int index(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    @Deprecated
    default public int getFlatBlock(int idx) {
        return this.palette(PaletteType.BLOCKS).idAt(idx);
    }

    @Deprecated
    default public int getFlatBlock(int x, int y, int z) {
        return this.getFlatBlock(ChunkSection.index(x, y, z));
    }

    @Deprecated
    default public void setFlatBlock(int idx, int id) {
        this.palette(PaletteType.BLOCKS).setIdAt(idx, id);
    }

    @Deprecated
    default public void setFlatBlock(int x, int y, int z, int id) {
        this.setFlatBlock(ChunkSection.index(x, y, z), id);
    }

    @Deprecated
    default public int getBlockWithoutData(int x, int y, int z) {
        return this.getFlatBlock(x, y, z) >> 4;
    }

    @Deprecated
    default public int getBlockData(int x, int y, int z) {
        return this.getFlatBlock(x, y, z) & 0xF;
    }

    @Deprecated
    default public void setBlockWithData(int x, int y, int z, int type, int data) {
        this.setFlatBlock(ChunkSection.index(x, y, z), type << 4 | data & 0xF);
    }

    @Deprecated
    default public void setBlockWithData(int idx, int type, int data) {
        this.setFlatBlock(idx, type << 4 | data & 0xF);
    }

    @Deprecated
    default public void setPaletteIndex(int idx, int index) {
        this.palette(PaletteType.BLOCKS).setPaletteIndexAt(idx, index);
    }

    @Deprecated
    default public int getPaletteIndex(int idx) {
        return this.palette(PaletteType.BLOCKS).paletteIndexAt(idx);
    }

    @Deprecated
    default public int getPaletteSize() {
        return this.palette(PaletteType.BLOCKS).size();
    }

    @Deprecated
    default public int getPaletteEntry(int index) {
        return this.palette(PaletteType.BLOCKS).idByIndex(index);
    }

    @Deprecated
    default public void setPaletteEntry(int index, int id) {
        this.palette(PaletteType.BLOCKS).setIdByIndex(index, id);
    }

    @Deprecated
    default public void replacePaletteEntry(int oldId, int newId) {
        this.palette(PaletteType.BLOCKS).replaceId(oldId, newId);
    }

    @Deprecated
    default public void addPaletteEntry(int id) {
        this.palette(PaletteType.BLOCKS).addId(id);
    }

    @Deprecated
    default public void clearPalette() {
        this.palette(PaletteType.BLOCKS).clear();
    }

    public int getNonAirBlocksCount();

    public void setNonAirBlocksCount(int var1);

    default public boolean hasLight() {
        return this.getLight() != null;
    }

    public @Nullable ChunkSectionLight getLight();

    public void setLight(@Nullable ChunkSectionLight var1);

    public @Nullable DataPalette palette(PaletteType var1);

    public void addPalette(PaletteType var1, DataPalette var2);

    public void removePalette(PaletteType var1);
}

