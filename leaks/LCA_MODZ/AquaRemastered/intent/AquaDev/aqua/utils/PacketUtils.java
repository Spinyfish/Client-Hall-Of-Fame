package intent.AquaDev.aqua.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketUtils {
   static Minecraft mc = Minecraft.getMinecraft();

   public static void sendPacket(Packet<?> packet, boolean silent) {
      if (mc.thePlayer != null) {
         mc.getNetHandler().getNetworkManager().sendPacket(packet);
      }
   }

   public static void sendPacketNoEvent(Packet packet) {
      sendPacket(packet, true);
   }

   public static void sendPacket(Packet packet) {
      sendPacket(packet, false);
   }
}
