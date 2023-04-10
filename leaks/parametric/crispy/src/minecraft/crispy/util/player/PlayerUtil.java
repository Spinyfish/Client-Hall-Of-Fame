package crispy.util.player;

import crispy.util.MinecraftUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class PlayerUtil implements MinecraftUtil {
    public static boolean isMoving() {
        if ((!mc.thePlayer.isCollidedHorizontally) && (!mc.thePlayer.isSneaking())) {
            return ((mc.thePlayer.movementInput.moveForward != 0.0F || mc.thePlayer.movementInput.moveStrafe != 0.0F));
        }
        return false;
    }

    public static boolean isMoving2() {
        return ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F));
    }

    public static boolean canSeeBlock(BlockPos p_Pos) {
        if (mc.thePlayer == null)
            return false;

        return Minecraft.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3(p_Pos.getX(), p_Pos.getY(), p_Pos.getZ()), false, true, false) == null;
    }

}