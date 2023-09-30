package alos.stella.event.events;

import kotlin.jvm.internal.Intrinsics;
import alos.stella.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BlockBBEvent extends Event {
    public final int x;
    public final int y;
    public final int z;
    @NotNull
    private final Block block;
    @Nullable
    public AxisAlignedBB boundingBox;

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }

    public final int getZ() {
        return this.z;
    }

    @NotNull
    public final Block getBlock() {
        return this.block;
    }

    @Nullable
    public final AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public final void setBoundingBox(@Nullable AxisAlignedBB var1) {
        this.boundingBox = var1;
    }

    public BlockBBEvent(@NotNull BlockPos blockPos, @NotNull Block block, @Nullable AxisAlignedBB boundingBox) {
        Intrinsics.checkNotNullParameter(blockPos, "blockPos");
        Intrinsics.checkNotNullParameter(block, "block");
        this.block = block;
        this.boundingBox = boundingBox;
        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.z = blockPos.getZ();
    }
}
