/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.minecraft.chunks;

import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionLight;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionLightImpl;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.DataPaletteImpl;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import java.util.EnumMap;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ChunkSectionImpl
implements ChunkSection {
    private final EnumMap<PaletteType, DataPalette> palettes = new EnumMap(PaletteType.class);
    private ChunkSectionLight light;
    private int nonAirBlocksCount;

    public ChunkSectionImpl() {
    }

    public ChunkSectionImpl(boolean holdsLight) {
        this.addPalette(PaletteType.BLOCKS, new DataPaletteImpl(4096));
        if (holdsLight) {
            this.light = new ChunkSectionLightImpl();
        }
    }

    public ChunkSectionImpl(boolean holdsLight, int expectedPaletteLength) {
        this.addPalette(PaletteType.BLOCKS, new DataPaletteImpl(4096, expectedPaletteLength));
        if (holdsLight) {
            this.light = new ChunkSectionLightImpl();
        }
    }

    @Override
    public int getNonAirBlocksCount() {
        return this.nonAirBlocksCount;
    }

    @Override
    public void setNonAirBlocksCount(int nonAirBlocksCount) {
        this.nonAirBlocksCount = nonAirBlocksCount;
    }

    @Override
    public @Nullable ChunkSectionLight getLight() {
        return this.light;
    }

    @Override
    public void setLight(@Nullable ChunkSectionLight light) {
        this.light = light;
    }

    @Override
    public DataPalette palette(PaletteType type) {
        return this.palettes.get((Object)type);
    }

    @Override
    public void addPalette(PaletteType type, DataPalette palette) {
        this.palettes.put(type, palette);
    }

    @Override
    public void removePalette(PaletteType type) {
        this.palettes.remove((Object)type);
    }
}

