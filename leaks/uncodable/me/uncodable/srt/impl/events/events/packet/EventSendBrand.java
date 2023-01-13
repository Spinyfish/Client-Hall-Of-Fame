package me.uncodable.srt.impl.events.events.packet;

import me.uncodable.srt.impl.events.api.Event;
import net.minecraft.network.PacketBuffer;

public class EventSendBrand extends Event {
   private String channelIn;
   private PacketBuffer dataIn;

   public EventSendBrand(String channelIn, PacketBuffer dataIn) {
      this.channelIn = channelIn;
      this.dataIn = dataIn;
   }

   public String getChannelIn() {
      return this.channelIn;
   }

   public PacketBuffer getDataIn() {
      return this.dataIn;
   }

   public void setChannelIn(String channelIn) {
      this.channelIn = channelIn;
   }

   public void setDataIn(PacketBuffer dataIn) {
      this.dataIn = dataIn;
   }
}
