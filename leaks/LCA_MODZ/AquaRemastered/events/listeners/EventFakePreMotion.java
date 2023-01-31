package events.listeners;

import events.Event;
import intent.AquaDev.aqua.utils.RotationUtil;
import net.minecraft.client.Minecraft;

public class EventFakePreMotion extends Event {
   public static EventFakePreMotion getInstance;
   private static float yaw;
   private static float pitch;

   public EventFakePreMotion(float yaw, float pitch) {
      EventFakePreMotion.yaw = yaw;
      EventFakePreMotion.pitch = pitch;
   }

   public static float getYaw() {
      return yaw;
   }

   public void setYaw(float yaw) {
      EventFakePreMotion.yaw = yaw;
      Minecraft.getMinecraft().thePlayer.renderYawOffset = RotationUtil.calculateCorrectYawOffset(yaw);
      Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
   }

   public static float getPitch() {
      return pitch;
   }

   public void setPitch(float pitch) {
      EventFakePreMotion.pitch = pitch;
      Minecraft.getMinecraft().thePlayer.prevRotationPitchHead = pitch;
      Minecraft.getMinecraft().thePlayer.rotationPitchHead = pitch;
   }
}
