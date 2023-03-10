/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.viaversion.viaversion.api.minecraft;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;

public class BlockChangeRecord1_16_2
implements BlockChangeRecord {
    private final byte sectionX;
    private final byte sectionY;
    private final byte sectionZ;
    private int blockId;

    public BlockChangeRecord1_16_2(byte sectionX, byte sectionY, byte sectionZ, int blockId) {
        this.sectionX = sectionX;
        this.sectionY = sectionY;
        this.sectionZ = sectionZ;
        this.blockId = blockId;
    }

    public BlockChangeRecord1_16_2(int sectionX, int sectionY, int sectionZ, int blockId) {
        this((byte)sectionX, (byte)sectionY, (byte)sectionZ, blockId);
    }

    @Override
    public byte getSectionX() {
        return this.sectionX;
    }

    @Override
    public byte getSectionY() {
        return this.sectionY;
    }

    @Override
    public byte getSectionZ() {
        return this.sectionZ;
    }

    @Override
    public short getY(int chunkSectionY) {
        Preconditions.checkArgument((chunkSectionY >= 0 ? 1 : 0) != 0, (Object)("Invalid chunkSectionY: " + chunkSectionY));
        return (short)((chunkSectionY << 4) + this.sectionY);
    }

    @Override
    public int getBlockId() {
        return this.blockId;
    }

    @Override
    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }
}

