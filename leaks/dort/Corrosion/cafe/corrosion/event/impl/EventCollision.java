package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class EventCollision extends Event {
    private final Block block;
    private final BlockPos blockPos;
    private AxisAlignedBB boundingBox;

    public Block getBlock() {
        return this.block;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }

    public EventCollision(Block block, BlockPos blockPos, AxisAlignedBB boundingBox) {
        this.block = block;
        this.blockPos = blockPos;
        this.boundingBox = boundingBox;
    }
}
