package me.uncodable.srt.impl.utils;

import net.minecraft.network.Packet;

public class PacketUtils {
   public static <T extends Packet> T getPacket(Packet packet) {
      return (T)packet;
   }
}
