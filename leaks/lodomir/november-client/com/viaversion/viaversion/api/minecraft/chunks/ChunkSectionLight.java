/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.minecraft.chunks;

import com.viaversion.viaversion.api.minecraft.chunks.NibbleArray;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ChunkSectionLight {
    public static final int LIGHT_LENGTH = 2048;

    public boolean hasSkyLight();

    public boolean hasBlockLight();

    public byte @Nullable [] getSkyLight();

    public byte @Nullable [] getBlockLight();

    public void setSkyLight(byte[] var1);

    public void setBlockLight(byte[] var1);

    public @Nullable NibbleArray getSkyLightNibbleArray();

    public @Nullable NibbleArray getBlockLightNibbleArray();

    public void readSkyLight(ByteBuf var1);

    public void readBlockLight(ByteBuf var1);

    public void writeSkyLight(ByteBuf var1);

    public void writeBlockLight(ByteBuf var1);
}

