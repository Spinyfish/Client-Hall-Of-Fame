/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft;

import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;

public class BlockChangeRecord1_8
implements BlockChangeRecord {
    private final byte sectionX;
    private final short y;
    private final byte sectionZ;
    private int blockId;

    public BlockChangeRecord1_8(byte sectionX, short y, byte sectionZ, int blockId) {
        this.sectionX = sectionX;
        this.y = y;
        this.sectionZ = sectionZ;
        this.blockId = blockId;
    }

    public BlockChangeRecord1_8(int sectionX, int y, int sectionZ, int blockId) {
        this((byte)sectionX, (short)y, (byte)sectionZ, blockId);
    }

    @Override
    public byte getSectionX() {
        return this.sectionX;
    }

    @Override
    public byte getSectionY() {
        return (byte)(this.y & 0xF);
    }

    @Override
    public short getY(int chunkSectionY) {
        return this.y;
    }

    @Override
    public byte getSectionZ() {
        return this.sectionZ;
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

