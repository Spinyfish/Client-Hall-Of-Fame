package dev.tenacity.event.impl.player;

import dev.tenacity.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPosition;

public class BoundingBoxEvent extends Event {
    private final Block block;
    private final BlockPosition blockPosition;
    private AxisAlignedBB boundingBox;

    public BoundingBoxEvent(Block block, BlockPosition pos, AxisAlignedBB boundingBox) {
        this.block = block;
        this.blockPosition = pos;
        this.boundingBox = boundingBox;
    }

    public final Block getBlock() {
        return this.block;
    }

    public final BlockPosition getBlockPos() {
        return this.blockPosition;
    }

    public final AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public final void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}
