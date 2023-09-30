package dev.tenacity.utils.player.rotations;

import dev.tenacity.utils.Utils;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

public class ScaffoldRotationUtil implements Utils {

    public static boolean overBlock(EnumFacing enumFacing, BlockPosition pos, boolean strict) {
        MovingObjectPosition movingObjectPosition = mc.objectMouseOver;

        if (movingObjectPosition == null) {
            return false;
        }

        if (movingObjectPosition.hitVec == null) {
            return false;
        }

        return movingObjectPosition.getBlockPos().equals(pos) && (!strict || movingObjectPosition.sideHit == enumFacing);
    }

}
