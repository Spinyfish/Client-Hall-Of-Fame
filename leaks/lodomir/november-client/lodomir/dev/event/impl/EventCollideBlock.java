/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl;

import lodomir.dev.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;

public final class EventCollideBlock
extends Event {
    private AxisAlignedBB collisionBoundingBox;
    private Block block;
    private int x;
    private int y;
    private int z;

    public EventCollideBlock(AxisAlignedBB collisionBoundingBox, Block block, int x, int y, int z) {
        this.collisionBoundingBox = collisionBoundingBox;
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public AxisAlignedBB getCollisionBoundingBox() {
        return this.collisionBoundingBox;
    }

    public void setCollisionBoundingBox(AxisAlignedBB collisionBoundingBox) {
        this.collisionBoundingBox = collisionBoundingBox;
    }

    public Block getBlock() {
        return this.block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}

