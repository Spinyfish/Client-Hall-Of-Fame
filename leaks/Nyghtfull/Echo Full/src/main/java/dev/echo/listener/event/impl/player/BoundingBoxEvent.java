package dev.echo.listener.event.impl.player;

import dev.echo.listener.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class BoundingBoxEvent extends Event {
    private final Block block;
    private final BlockPos blockPos;
    private AxisAlignedBB boundingBox;

    public BoundingBoxEvent(Block block, BlockPos pos, AxisAlignedBB boundingBox) {
        this.block = block;
        this.blockPos = pos;
        this.boundingBox = boundingBox;
    }

    public final Block getBlock() {
        return this.block;
    }

    public final BlockPos getBlockPos() {
        return this.blockPos;
    }

    public final AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public final void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}
