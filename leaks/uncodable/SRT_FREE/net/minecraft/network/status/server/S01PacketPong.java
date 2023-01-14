package net.minecraft.network.status.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusClient;

public class S01PacketPong implements Packet<INetHandlerStatusClient> {
   private long clientTime;

   public S01PacketPong() {
   }

   public S01PacketPong(long time) {
      this.clientTime = time;
   }

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.clientTime = buf.readLong();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeLong(this.clientTime);
   }

   public void processPacket(INetHandlerStatusClient handler) {
      handler.handlePong(this);
   }
}
