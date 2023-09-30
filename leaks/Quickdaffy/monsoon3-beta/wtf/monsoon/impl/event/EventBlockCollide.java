/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.event;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import wtf.monsoon.api.event.Event;

public final class EventBlockCollide
extends Event {
    private AxisAlignedBB collisionBoundingBox;
    private Block block;
    private int x;
    private int y;
    private int z;

    public AxisAlignedBB getCollisionBoundingBox() {
        return this.collisionBoundingBox;
    }

    public Block getBlock() {
        return this.block;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public void setCollisionBoundingBox(AxisAlignedBB collisionBoundingBox) {
        this.collisionBoundingBox = collisionBoundingBox;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public EventBlockCollide(AxisAlignedBB collisionBoundingBox, Block block, int x, int y, int z) {
        this.collisionBoundingBox = collisionBoundingBox;
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

