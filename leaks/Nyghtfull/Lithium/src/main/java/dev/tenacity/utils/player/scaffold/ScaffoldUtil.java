package dev.tenacity.utils.player.scaffold;

import com.sun.javafx.geom.Vec3d;
import dev.tenacity.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.Vec3;

public class ScaffoldUtil implements Utils {

    public static boolean isBlockUnder() {
        for (int y = 0; y >= -2; y--) {
            Vec3 vector = mc.thePlayer.getPositionVector();

            BlockPosition position = new BlockPosition(
                    Math.floor(vector.xCoord),
                    Math.floor(vector.yCoord),
                    Math.floor(vector.zCoord)
            );

            if (isValidBlock(position.add(0, y, 0))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidBlock(BlockPosition position) {
        return !mc.theWorld.isAirBlock(position) && !mc.theWorld.getBlockState(position).getBlock().getMaterial().isLiquid();
    }

}
