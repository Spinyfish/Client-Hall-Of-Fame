/* November.lol © 2023 */
package lol.november.utility.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class RaycastUtils {

  /**
   * The {@link Minecraft} game instance
   */
  private static final Minecraft mc = Minecraft.getMinecraft();

  public static MovingObjectPosition cast(
    float yaw,
    float pitch,
    double reachDistance
  ) {
    Vec3 rotationVec = rotationVec(yaw, pitch);
    Vec3 vec3 = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
    Vec3 vec32 = vec3.addVector(
      rotationVec.xCoord * reachDistance,
      rotationVec.yCoord * reachDistance,
      rotationVec.zCoord * reachDistance
    );
    return mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, true);
  }

  private static Vec3 rotationVec(float yaw, float pitch) {
    double f = Math.cos(-yaw * 0.017453292F - Math.PI);
    double f1 = Math.sin(-yaw * 0.017453292F - Math.PI);
    double f2 = -Math.cos(-pitch * 0.017453292F);
    double f3 = Math.sin(-pitch * 0.017453292F);
    return new Vec3(f1 * f2, f3, f * f2);
  }
}
