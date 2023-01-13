package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S46PacketSetCompressionLevel implements Packet<INetHandlerPlayClient> {
   private int field_179761_a;

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.field_179761_a = buf.readVarIntFromBuffer();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeVarIntToBuffer(this.field_179761_a);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSetCompressionLevel(this);
   }

   public int func_179760_a() {
      return this.field_179761_a;
   }
}
