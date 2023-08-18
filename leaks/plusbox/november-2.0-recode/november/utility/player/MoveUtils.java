/* November.lol © 2023 */
package lol.november.utility.player;

import static lol.november.utility.math.MathUtils.ANGLE_TO_RADIANS;

import lol.november.listener.event.player.move.EventMove;
import net.minecraft.client.Minecraft;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class MoveUtils {

  /**
   * The {@link Minecraft} game instance
   */
  private static final Minecraft mc = Minecraft.getMinecraft();

  /**
   * Checks if the player is moving or not
   *
   * @return if the player has forward or strafe movement
   */
  public static boolean moving() {
    return (
      mc.thePlayer.movementInput.moveForward != 0.0f ||
        mc.thePlayer.movementInput.moveStrafe != 0.0f
    );
  }

  /**
   * Sets a speed to a {@link EventMove}
   *
   * @param event the {@link EventMove}
   * @param speed the speed to strafe at
   */
  public static void setSpeed(EventMove event, double speed) {
    double[] strafe = strafe(speed);

    event.setX(strafe[0]);
    event.setZ(strafe[1]);

    mc.thePlayer.motionX = strafe[0];
    mc.thePlayer.motionZ = strafe[1];
  }

  /**
   * Sets speed
   *
   * @param speed the speed to strafe at
   */
  public static void setSpeed(double speed) {
    double[] strafe = strafe(speed);

    mc.thePlayer.motionX = strafe[0];
    mc.thePlayer.motionZ = strafe[1];
  }

  /**
   * Calculates strafe movement with a custom speed
   *
   * @param speed the speed value
   * @return an array of two doubles containing the xz strafe motion
   */
  public static double[] strafe(double speed) {
    double direction = directional();
    return new double[]{
      -Math.sin(direction) * speed,
      Math.cos(direction) * speed,
    };
  }

  public static double speed() {
    return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX
      + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
  }

  /**
   * Gets the direction for our movement
   *
   * @return the direction of where we are facing relative to our movement
   */
  public static double directional() {
    // get our current angle (our player yaw rotation)
    float yaw = mc.thePlayer.rotationYaw;

    // if we're moving backwards, reverse our yaw
    if (mc.thePlayer.moveForward < 0.0f) yaw -= 180.0f;

    // this is for handling holding forward & strafing side to side at the same time
    float forward = mc.thePlayer.moveForward * 0.5f;
    if (forward == 0.0f) forward = 1.0f;

    float strafe = mc.thePlayer.moveStrafing;
    if (strafe > 0.0f) {
      yaw -= 90.0f * forward;
    } else if (strafe < 0.0f) {
      yaw += 90.0f * forward;
    }

    // convert angle to radians
    return yaw * ANGLE_TO_RADIANS;
  }
}
