package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S00PacketKeepAlive implements Packet<INetHandlerPlayClient> {
   private int id;

   public S00PacketKeepAlive() {
   }

   public S00PacketKeepAlive(int idIn) {
      this.id = idIn;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleKeepAlive(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.id = buf.readVarIntFromBuffer();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeVarIntToBuffer(this.id);
   }

   public int func_149134_c() {
      return this.id;
   }
}
