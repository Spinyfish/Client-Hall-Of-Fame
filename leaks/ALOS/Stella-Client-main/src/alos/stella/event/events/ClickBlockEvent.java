package alos.stella.event.events;

import alos.stella.event.Event;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

public final class ClickBlockEvent extends Event {
    @Nullable
    private final BlockPos clickedBlock;
    @Nullable
    private final EnumFacing enumFacing;

    @Nullable
    public final BlockPos getClickedBlock() {
        return this.clickedBlock;
    }

    @Nullable
    public final EnumFacing getEnumFacing() {
        return this.enumFacing;
    }

    public ClickBlockEvent(@Nullable BlockPos clickedBlock, @Nullable EnumFacing enumFacing) {
        this.clickedBlock = clickedBlock;
        this.enumFacing = enumFacing;
    }
}