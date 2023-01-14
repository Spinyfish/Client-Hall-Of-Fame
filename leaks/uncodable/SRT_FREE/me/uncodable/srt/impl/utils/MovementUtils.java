package me.uncodable.srt.impl.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

public class MovementUtils {
   private static final Minecraft MC = Minecraft.getMinecraft();

   public static void setMoveSpeed(double moveSpeed, boolean breakWhenStopped) {
      float forward = MC.thePlayer.movementInput.moveForward;
      float strafe = MC.thePlayer.movementInput.moveStrafe;
      float yaw = MC.thePlayer.rotationYaw;
      if ((double)forward == 0.0 && (double)strafe == 0.0 && breakWhenStopped) {
         MC.thePlayer.motionX = 0.0;
         MC.thePlayer.motionZ = 0.0;
      }

      int d = 45;
      if ((double)forward != 0.0) {
         if ((double)strafe > 0.0) {
            yaw += (float)((double)forward > 0.0 ? -d : d);
         } else if ((double)strafe < 0.0) {
            yaw += (float)((double)forward > 0.0 ? d : -d);
         }

         strafe = 0.0F;
         if ((double)forward > 0.0) {
            forward = 1.0F;
         } else if ((double)forward < 0.0) {
            forward = -1.0F;
         }
      }

      double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
      MC.thePlayer.motionX = (double)forward * moveSpeed * cos + (double)strafe * moveSpeed * sin;
      MC.thePlayer.motionZ = (double)forward * moveSpeed * sin - (double)strafe * moveSpeed * cos;
   }

   public static void setMoveSpeed(double moveSpeed) {
      setMoveSpeed(moveSpeed, true);
   }

   public static void addFriction() {
      addFriction(0.91);
   }

   public static void addFriction(double friction) {
      MC.thePlayer.motionX *= friction;
      MC.thePlayer.motionZ *= friction;
   }

   public static void setMoveSpeedTeleport(double moveSpeed) {
      double x = -Math.sin(Math.toRadians((double)MC.thePlayer.rotationYaw)) * moveSpeed;
      double z = Math.cos(Math.toRadians((double)MC.thePlayer.rotationYaw)) * moveSpeed;
      EntityUtils.teleportToPos(new BlockPos(MC.thePlayer.posX + x, MC.thePlayer.posY, MC.thePlayer.posZ + z), false);
      MC.thePlayer.setPosition(MC.thePlayer.posX + x, MC.thePlayer.posY, MC.thePlayer.posZ + z);
   }

   public static double getBaseMoveSpeed() {
      double baseSpeed = 0.2873;
      if (MC.thePlayer != null && MC.thePlayer.isPotionActive(Potion.moveSpeed)) {
         int amplifier = MC.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
         baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
      }

      return baseSpeed;
   }

   public static boolean isMoving() {
      return MC.thePlayer.moveForward != 0.0F || MC.thePlayer.moveStrafing != 0.0F;
   }

   public static boolean isMoving2() {
      return MC.thePlayer.moveForward != 0.0F || MC.thePlayer.moveStrafing != 0.0F || MC.gameSettings.keyBindJump.isKeyDown();
   }

   public static void zeroMotion() {
      MC.thePlayer.motionX = MC.thePlayer.motionZ = 0.0;
   }

   public static void zeroMotion2() {
      MC.thePlayer.motionX = MC.thePlayer.motionY = MC.thePlayer.motionZ = 0.0;
   }
}
