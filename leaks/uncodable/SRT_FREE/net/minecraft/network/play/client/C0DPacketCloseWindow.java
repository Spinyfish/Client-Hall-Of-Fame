package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0DPacketCloseWindow implements Packet<INetHandlerPlayServer> {
   private int windowId;

   public C0DPacketCloseWindow() {
   }

   public C0DPacketCloseWindow(int windowId) {
      this.windowId = windowId;
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processCloseWindow(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.windowId = buf.readByte();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeByte(this.windowId);
   }
}
