/* November.lol © 2023 */
package lol.november.utility.math;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class RotationUtils {

  /**
   * The {@link Minecraft} game instance
   */
  private static final Minecraft mc = Minecraft.getMinecraft();

  /**
   * Calculates rotations to the {@link EntityLivingBase}
   *
   * @param entity the {@link EntityLivingBase} target
   * @return a float array of rotations
   */
  public static float[] entity(EntityLivingBase entity) {
    double diffX = mc.thePlayer.posX - entity.posX;
    double diffY =
      (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) - entity.posY;
    double diffZ = mc.thePlayer.posZ - entity.posZ;

    float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) + 90.0f;
    float pitch = (float) Math.toDegrees(
      Math.atan2(diffY, Math.hypot(diffX, diffZ))
    );

    return new float[]{yaw, pitch};
  }

  public static float[] block(BlockPos pos, EnumFacing facing) {
    double diffX =
      mc.thePlayer.posX - (pos.getX() - (facing.getFrontOffsetX() / 2.0));
    double diffY =
      (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) - (pos.getY() - (facing.getFrontOffsetY() / 2.0));
    double diffZ =
      mc.thePlayer.posZ - (pos.getZ() - (facing.getFrontOffsetZ() / 2.0));

    float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) + 90.0f;
    float pitch = (float) Math.toDegrees(
      Math.atan2(diffY, Math.hypot(diffX, diffZ))
    );

    return new float[]{yaw, pitch};
  }

  public static float[] middleBlock(BlockPos pos, EnumFacing facing) {
    double diffX =
      (Math.floor(mc.thePlayer.posX) + 0.5) -
        (pos.getX() - (facing.getFrontOffsetX() / 2.0));
    double diffY =
      (Math.floor(mc.thePlayer.posY) + 0.5) -
        (pos.getY() - (facing.getFrontOffsetY() / 2.0));
    double diffZ =
      (Math.floor(mc.thePlayer.posZ) + 0.5) -
        (pos.getZ() - (facing.getFrontOffsetZ() / 2.0));

    float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) + 90.0f;
    float pitch = (float) Math.toDegrees(
      Math.atan2(diffY, Math.hypot(diffX, diffZ))
    );

    return new float[]{yaw, pitch};
  }
}
