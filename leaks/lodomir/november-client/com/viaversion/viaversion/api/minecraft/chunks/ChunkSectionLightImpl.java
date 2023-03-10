/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.minecraft.chunks;

import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionLight;
import com.viaversion.viaversion.api.minecraft.chunks.NibbleArray;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ChunkSectionLightImpl
implements ChunkSectionLight {
    private NibbleArray blockLight = new NibbleArray(4096);
    private NibbleArray skyLight;

    @Override
    public void setBlockLight(byte[] data) {
        if (data.length != 2048) {
            throw new IllegalArgumentException("Data length != 2048");
        }
        if (this.blockLight == null) {
            this.blockLight = new NibbleArray(data);
        } else {
            this.blockLight.setHandle(data);
        }
    }

    @Override
    public void setSkyLight(byte[] data) {
        if (data.length != 2048) {
            throw new IllegalArgumentException("Data length != 2048");
        }
        if (this.skyLight == null) {
            this.skyLight = new NibbleArray(data);
        } else {
            this.skyLight.setHandle(data);
        }
    }

    @Override
    public byte @Nullable [] getBlockLight() {
        return this.blockLight == null ? null : this.blockLight.getHandle();
    }

    @Override
    public @Nullable NibbleArray getBlockLightNibbleArray() {
        return this.blockLight;
    }

    @Override
    public byte @Nullable [] getSkyLight() {
        return this.skyLight == null ? null : this.skyLight.getHandle();
    }

    @Override
    public @Nullable NibbleArray getSkyLightNibbleArray() {
        return this.skyLight;
    }

    @Override
    public void readBlockLight(ByteBuf input) {
        if (this.blockLight == null) {
            this.blockLight = new NibbleArray(4096);
        }
        input.readBytes(this.blockLight.getHandle());
    }

    @Override
    public void readSkyLight(ByteBuf input) {
        if (this.skyLight == null) {
            this.skyLight = new NibbleArray(4096);
        }
        input.readBytes(this.skyLight.getHandle());
    }

    @Override
    public void writeBlockLight(ByteBuf output) {
        output.writeBytes(this.blockLight.getHandle());
    }

    @Override
    public void writeSkyLight(ByteBuf output) {
        output.writeBytes(this.skyLight.getHandle());
    }

    @Override
    public boolean hasSkyLight() {
        return this.skyLight != null;
    }

    @Override
    public boolean hasBlockLight() {
        return this.blockLight != null;
    }
}

