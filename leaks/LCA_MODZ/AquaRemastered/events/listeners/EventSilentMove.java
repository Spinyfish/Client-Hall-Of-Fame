package events.listeners;

import events.Event;
import intent.AquaDev.aqua.utils.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class EventSilentMove extends Event {
   private boolean silent;
   private float yaw;

   public EventSilentMove(float yaw) {
      this.yaw = yaw;
   }

   public boolean isSilent() {
      return this.silent;
   }

   public void setSilent(boolean silent) {
      this.silent = silent;
   }

   public float getYaw() {
      return this.yaw;
   }

   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   public float[] moveSilent(float strafe, float forward) {
      Minecraft mc = Minecraft.getMinecraft();
      float diff = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - RotationUtil.yaw);
      float newForward = 0.0F;
      float newStrafe = 0.0F;
      if ((double)diff >= 22.5 && (double)diff < 67.5) {
         newStrafe += strafe;
         newForward += forward;
         newStrafe -= forward;
         newForward += strafe;
      } else if ((double)diff >= 67.5 && (double)diff < 112.5) {
         newStrafe -= forward;
         newForward += strafe;
      } else if ((double)diff >= 112.5 && (double)diff < 157.5) {
         newStrafe -= strafe;
         newForward -= forward;
         newStrafe -= forward;
         newForward += strafe;
      } else if ((double)diff >= 157.5 || (double)diff <= -157.5) {
         newStrafe -= strafe;
         newForward -= forward;
      } else if ((double)diff > -157.5 && (double)diff <= -112.5) {
         newStrafe -= strafe;
         newForward -= forward;
         newStrafe += forward;
         newForward -= strafe;
      } else if ((double)diff > -112.5 && (double)diff <= -67.5) {
         newStrafe += forward;
         newForward -= strafe;
      } else if ((double)diff > -67.5 && (double)diff <= -22.5) {
         newStrafe += strafe;
         newForward += forward;
         newStrafe += forward;
         newForward -= strafe;
      } else {
         newStrafe += strafe;
         newForward += forward;
      }

      return new float[]{newStrafe, newForward};
   }
}
