package me.uncodable.srt.impl.events.events.packet;

import me.uncodable.srt.impl.events.api.Event;
import net.minecraft.network.Packet;

public class EventPacket extends Event {
   private Packet packet;

   public EventPacket(Packet packet) {
      this.packet = packet;
   }

   public Packet getPacket() {
      return this.packet;
   }

   public void setPacket(Packet packet) {
      this.packet = packet;
   }
}
