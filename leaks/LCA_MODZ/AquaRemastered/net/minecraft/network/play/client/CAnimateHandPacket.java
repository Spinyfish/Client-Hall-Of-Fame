package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CAnimateHandPacket implements Packet<INetHandlerPlayServer> {
   private Hand hand;

   public CAnimateHandPacket() {
   }

   public CAnimateHandPacket(Hand handIn) {
      this.hand = handIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.hand = buf.readEnumValue(Hand.class);
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.hand);
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.handleAnimation(this);
   }

   public Hand getHand() {
      return this.hand;
   }
}
