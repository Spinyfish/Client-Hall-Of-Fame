package events.listeners;

import events.Event;
import intent.AquaDev.aqua.utils.RotationUtil;
import net.minecraft.client.Minecraft;

public class EventPreMotion extends Event {
   public static EventPreMotion getInstance;
   private static float yaw;
   private static float pitch;

   public EventPreMotion(float yaw, float pitch) {
      EventPreMotion.yaw = yaw;
      EventPreMotion.pitch = pitch;
   }

   public static float getYaw() {
      return yaw;
   }

   public void setYaw(float yaw) {
      EventPreMotion.yaw = yaw;
      Minecraft.getMinecraft().thePlayer.renderYawOffset = RotationUtil.calculateCorrectYawOffset(yaw);
      Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
   }

   public static float getPitch() {
      return pitch;
   }

   public void setPitch(float pitch) {
      EventPreMotion.pitch = pitch;
      Minecraft.getMinecraft().thePlayer.prevRotationPitchHead = pitch;
      Minecraft.getMinecraft().thePlayer.rotationPitchHead = pitch;
   }
}
