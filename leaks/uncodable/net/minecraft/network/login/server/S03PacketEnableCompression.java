package net.minecraft.network.login.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S03PacketEnableCompression implements Packet<INetHandlerLoginClient> {
   private int compressionTreshold;

   public S03PacketEnableCompression() {
   }

   public S03PacketEnableCompression(int compressionTresholdIn) {
      this.compressionTreshold = compressionTresholdIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.compressionTreshold = buf.readVarIntFromBuffer();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeVarIntToBuffer(this.compressionTreshold);
   }

   public void processPacket(INetHandlerLoginClient handler) {
      handler.handleEnableCompression(this);
   }

   public int getCompressionTreshold() {
      return this.compressionTreshold;
   }
}
