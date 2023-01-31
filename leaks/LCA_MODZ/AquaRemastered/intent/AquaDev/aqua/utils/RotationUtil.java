package intent.AquaDev.aqua.utils;

import intent.AquaDev.aqua.Aqua;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class RotationUtil {
   public static float yaw;
   public static float renderYawOffset;
   public static float pitch;
   public static boolean RotationInUse;
   static Minecraft mc = Minecraft.getMinecraft();

   public static float[] Intavee(EntityPlayerSP player, EntityLivingBase target) {
      float yawRandom = (float)MathHelper.getRandomDoubleInRange(new Random(), -0.2, 0.2);
      float pitchRandom = (float)MathHelper.getRandomDoubleInRange(new Random(), -0.02, 0.02);
      double posX = Aqua.setmgr.getSetting("KillauraRandom").isState() ? target.posX - player.posX + (double)yawRandom : target.posX - player.posX;
      double posY = Aqua.setmgr.getSetting("KillauraClampPitch").isState()
         ? MathHelper.clamp_double(player.posY + (double)player.getEyeHeight(), target.getEntityBoundingBox().minY, target.getEntityBoundingBox().maxY)
            - (player.posY + (double)player.getEyeHeight())
         : (
            Aqua.setmgr.getSetting("KillauraRandom").isState()
               ? target.posY + (double)target.getEyeHeight() - (player.posY + (double)player.getEyeHeight() - (double)pitchRandom)
               : target.posY + (double)target.getEyeHeight() - (player.posY + (double)player.getEyeHeight())
         );
      double posZ = Aqua.setmgr.getSetting("KillauraRandom").isState() ? target.posZ - player.posZ - (double)yawRandom : target.posZ - player.posZ;
      double var14 = (double)MathHelper.sqrt_double(posX * posX + posZ * posZ);
      float yaw = (float)(Math.atan2(posZ, posX) * 180.0 / Math.PI) - 90.0F;
      float pitch = (float)(-(Math.atan2(posY, var14) * 180.0 / Math.PI));
      if (Aqua.setmgr.getSetting("KillauraMouseSensiFix").isState()) {
         float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
         float f3 = f2 * f2 * f2 * 1.2F;
         yaw -= yaw % f3;
         pitch -= pitch % (f3 * f2);
      }

      return new float[]{yaw, pitch};
   }

   public static float[] lookAtPosBed(double x, double y, double z) {
      double dirx = mc.thePlayer.posX - x;
      double diry = mc.thePlayer.posY - y;
      double dirz = mc.thePlayer.posZ - z;
      double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
      dirx /= len;
      diry /= len;
      dirz /= len;
      float yaw = (float)Math.atan2(dirz, dirx);
      float pitch = (float)((double)((float)Math.asin(diry)) + 0.3);
      pitch = (float)((double)pitch * 180.0 / Math.PI);
      yaw = (float)((double)yaw * 180.0 / Math.PI);
      yaw = (float)((double)yaw + 90.0);
      float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
      float f3 = f2 * f2 * f2 * 1.2F;
      yaw -= yaw % f3;
      pitch -= pitch % (f3 * f2);
      return new float[]{yaw, pitch};
   }

   public static void setRotation(float y, float p) {
      if (p > 90.0F) {
         p = 90.0F;
      } else if (p < -90.0F) {
         p = -90.0F;
      }

      yaw = y;
      pitch = p;
      RotationInUse = true;
   }

   public static boolean setYaw(float y, float speed) {
      setRotation(yaw, pitch);
      if (speed >= 360.0F) {
         yaw = y;
         return true;
      } else if (!isInRange((double)yaw, y, speed) && !(speed >= 360.0F)) {
         if (getRotation((double)yaw, y) < 0) {
            yaw -= speed;
         } else {
            yaw += speed;
         }

         return false;
      } else {
         yaw = y;
         return true;
      }
   }

   public static int getRotation(double before, float after) {
      while(before > 360.0) {
         before -= 360.0;
      }

      while(before < 0.0) {
         before += 360.0;
      }

      while(after > 360.0F) {
         after -= 360.0F;
      }

      while(after < 0.0F) {
         after += 360.0F;
      }

      if (before > (double)after) {
         return before - (double)after > 180.0 ? 1 : -1;
      } else {
         return (double)after - before > 180.0 ? -1 : 1;
      }
   }

   public static boolean isInRange(double before, float after, float max) {
      while(before > 360.0) {
         before -= 360.0;
      }

      while(before < 0.0) {
         before += 360.0;
      }

      while(after > 360.0F) {
         after -= 360.0F;
      }

      while(after < 0.0F) {
         after += 360.0F;
      }

      if (before > (double)after) {
         if (before - (double)after > 180.0 && 360.0 - before - (double)after <= (double)max) {
            return true;
         } else {
            return before - (double)after <= (double)max;
         }
      } else if ((double)after - before > 180.0 && (double)(360.0F - after) - before <= (double)max) {
         return true;
      } else {
         return (double)after - before <= (double)max;
      }
   }

   public static boolean setPitch(float p, float speed) {
      if (p > 90.0F) {
         p = 90.0F;
      } else if (p < -90.0F) {
         p = -90.0F;
      }

      if (!(Math.abs(pitch - p) <= speed) && !(speed >= 360.0F)) {
         if (p < pitch) {
            pitch -= speed;
         } else {
            pitch += speed;
         }

         return true;
      } else {
         pitch = p;
         return false;
      }
   }

   public static float calculateCorrectYawOffset(float yaw) {
      double xDiff = mc.thePlayer.posX - mc.thePlayer.prevPosX;
      double zDiff = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
      float dist = (float)(xDiff * xDiff + zDiff * zDiff);
      float renderYawOffset = mc.thePlayer.renderYawOffset;
      float offset = renderYawOffset;
      if (dist > 0.0025000002F) {
         offset = (float)MathHelper.func_181159_b(zDiff, xDiff) * 180.0F / (float) Math.PI - 90.0F;
      }

      if (mc.thePlayer != null && mc.thePlayer.swingProgress > 0.0F) {
         offset = yaw;
      }

      float offsetDiff = MathHelper.wrapAngleTo180_float(offset - renderYawOffset);
      renderYawOffset += offsetDiff * 0.3F;
      float yawOffsetDiff = MathHelper.wrapAngleTo180_float(yaw - renderYawOffset);
      if (yawOffsetDiff < -75.0F) {
         yawOffsetDiff = -75.0F;
      }

      if (yawOffsetDiff >= 75.0F) {
         yawOffsetDiff = 75.0F;
      }

      renderYawOffset = yaw - yawOffsetDiff;
      if (yawOffsetDiff * yawOffsetDiff > 2500.0F) {
         renderYawOffset += yawOffsetDiff * 0.2F;
      }

      return renderYawOffset;
   }

   public static float getAngle(Entity entity) {
      double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, 1.0) - RenderUtil.interpolate(mc.thePlayer.posX, mc.thePlayer.lastTickPosX, 1.0);
      double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, 1.0) - RenderUtil.interpolate(mc.thePlayer.posZ, mc.thePlayer.lastTickPosZ, 1.0);
      float yaw = (float)(-Math.toDegrees(Math.atan2(x, z)));
      return (float)((double)yaw - RenderUtil.interpolate((double)mc.thePlayer.rotationYaw, (double)mc.thePlayer.prevRotationYaw, 1.0));
   }
}
