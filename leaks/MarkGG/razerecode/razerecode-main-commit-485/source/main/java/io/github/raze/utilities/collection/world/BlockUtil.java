package io.github.raze.utilities.collection.world;

import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;

public class BlockUtil {

    public static class BlockData {
        public BlockPosition pos;

        public EnumFacing facing;

        public BlockData(BlockPosition position, EnumFacing face) {
            this.pos = position;
            this.facing = face;
        }
    }

}
