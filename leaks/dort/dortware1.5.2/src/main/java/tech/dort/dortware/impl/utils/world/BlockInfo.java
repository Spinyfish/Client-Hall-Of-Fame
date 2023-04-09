package tech.dort.dortware.impl.utils.world;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockInfo {

    private final BlockPos position;
    private final EnumFacing direction;

    public BlockInfo(BlockPos position, EnumFacing direction) {
        this.position = position;
        this.direction = direction;
    }

    public BlockPos getPosition() {
        return position;
    }

    public EnumFacing getDirection() {
        return direction;
    }
}
