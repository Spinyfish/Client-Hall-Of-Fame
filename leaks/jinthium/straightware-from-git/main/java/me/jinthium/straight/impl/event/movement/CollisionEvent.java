package me.jinthium.straight.impl.event.movement;

import me.jinthium.straight.api.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;


public class CollisionEvent extends Event {
    private Block block;
    private BlockPos blockPos;

    private AxisAlignedBB collisionBox;

    public CollisionEvent(Block block, BlockPos blockPos, AxisAlignedBB collisionBox) {
        this.block = block;
        this.blockPos = blockPos;
        this.collisionBox = collisionBox;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public AxisAlignedBB getCollisionBox() {
        return collisionBox;
    }

    public void setCollisionBox(AxisAlignedBB collisionBox) {
        this.collisionBox = collisionBox;
    }
}