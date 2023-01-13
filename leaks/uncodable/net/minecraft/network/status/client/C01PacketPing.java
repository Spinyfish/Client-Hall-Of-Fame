package net.minecraft.network.status.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C01PacketPing implements Packet<INetHandlerStatusServer> {
   private long clientTime;

   public C01PacketPing() {
   }

   public C01PacketPing(long ping) {
      this.clientTime = ping;
   }

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.clientTime = buf.readLong();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeLong(this.clientTime);
   }

   public void processPacket(INetHandlerStatusServer handler) {
      handler.processPing(this);
   }

   public long getClientTime() {
      return this.clientTime;
   }
}
