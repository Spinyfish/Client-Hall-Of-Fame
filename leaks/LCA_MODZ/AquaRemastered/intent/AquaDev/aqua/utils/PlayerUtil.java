package intent.AquaDev.aqua.utils;

import events.listeners.EventPlayerMove;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class PlayerUtil {
   public static double getSpeed() {
      return Math.sqrt(
         Minecraft.getMinecraft().thePlayer.motionX * Minecraft.getMinecraft().thePlayer.motionX
            + Minecraft.getMinecraft().thePlayer.motionZ * Minecraft.getMinecraft().thePlayer.motionZ
      );
   }

   public static void setSpeed(double speed) {
      Minecraft mc = Minecraft.getMinecraft();
      double yaw = (double)mc.thePlayer.rotationYaw;
      boolean isMoving = mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F;
      boolean isMovingForward = mc.thePlayer.moveForward > 0.0F;
      boolean isMovingBackward = mc.thePlayer.moveForward < 0.0F;
      boolean isMovingRight = mc.thePlayer.moveStrafing > 0.0F;
      boolean isMovingLeft = mc.thePlayer.moveStrafing < 0.0F;
      boolean isMovingSideways = isMovingLeft || isMovingRight;
      boolean isMovingStraight = isMovingForward || isMovingBackward;
      if (isMoving) {
         if (isMovingForward && !isMovingSideways) {
            yaw += 0.0;
         } else if (isMovingBackward && !isMovingSideways) {
            yaw += 180.0;
         } else if (isMovingForward && isMovingLeft) {
            yaw += 45.0;
         } else if (isMovingForward) {
            yaw -= 45.0;
         } else if (!isMovingStraight && isMovingLeft) {
            yaw += 90.0;
         } else if (!isMovingStraight && isMovingRight) {
            yaw -= 90.0;
         } else if (isMovingBackward && isMovingLeft) {
            yaw += 135.0;
         } else if (isMovingBackward) {
            yaw -= 135.0;
         }

         yaw = Math.toRadians(yaw);
         mc.thePlayer.motionX = -Math.sin(yaw) * speed;
         mc.thePlayer.motionZ = Math.cos(yaw) * speed;
      }
   }

   public static void setSpeed1(EventPlayerMove moveEvent, double moveSpeed) {
      Minecraft mc = Minecraft.getMinecraft();
      setSpeed1(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, (double)mc.thePlayer.movementInput.moveStrafe, (double)mc.thePlayer.movementInput.moveForward);
   }

   public static void setSpeed1(EventPlayerMove moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
      double forward = pseudoForward;
      double strafe = pseudoStrafe;
      float yaw = pseudoYaw;
      if (pseudoForward != 0.0) {
         if (pseudoStrafe > 0.0) {
            yaw = pseudoYaw + (float)(pseudoForward > 0.0 ? -45 : 45);
         } else if (pseudoStrafe < 0.0) {
            yaw = pseudoYaw + (float)(pseudoForward > 0.0 ? 45 : -45);
         }

         strafe = 0.0;
         if (pseudoForward > 0.0) {
            forward = 1.0;
         } else if (pseudoForward < 0.0) {
            forward = -1.0;
         }
      }

      if (strafe > 0.0) {
         strafe = 1.0;
      } else if (strafe < 0.0) {
         strafe = -1.0;
      }

      double mx = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      double mz = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
      moveEvent.setX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
      moveEvent.setZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
   }

   public static double getDistanceToBlock(BlockPos pos) {
      double f = Minecraft.getMinecraft().thePlayer.posX - (double)pos.getX();
      double f1 = Minecraft.getMinecraft().thePlayer.posY - (double)pos.getY();
      double f2 = Minecraft.getMinecraft().thePlayer.posZ - (double)pos.getZ();
      return (double)MathHelper.sqrt_double(f * f + f1 * f1 + f2 * f2);
   }
}
