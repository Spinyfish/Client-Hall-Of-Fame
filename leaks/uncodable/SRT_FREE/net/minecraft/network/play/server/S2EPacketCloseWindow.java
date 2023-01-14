package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2EPacketCloseWindow implements Packet<INetHandlerPlayClient> {
   private int windowId;

   public S2EPacketCloseWindow() {
   }

   public S2EPacketCloseWindow(int windowIdIn) {
      this.windowId = windowIdIn;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleCloseWindow(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.windowId = buf.readUnsignedByte();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeByte(this.windowId);
   }
}
