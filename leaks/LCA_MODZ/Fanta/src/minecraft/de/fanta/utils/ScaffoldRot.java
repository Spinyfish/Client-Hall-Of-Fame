package de.fanta.utils;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class ScaffoldRot {
    public BlockPos position;
    public EnumFacing face;

    public ScaffoldRot(BlockPos position, EnumFacing face) {
        this.position = position;
        this.face = face;
    }
}
