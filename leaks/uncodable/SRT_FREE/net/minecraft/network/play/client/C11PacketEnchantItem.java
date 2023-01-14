package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C11PacketEnchantItem implements Packet<INetHandlerPlayServer> {
   private int windowId;
   private int button;

   public C11PacketEnchantItem() {
   }

   public C11PacketEnchantItem(int windowId, int button) {
      this.windowId = windowId;
      this.button = button;
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processEnchantItem(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.windowId = buf.readByte();
      this.button = buf.readByte();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeByte(this.windowId);
      buf.writeByte(this.button);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public int getButton() {
      return this.button;
   }
}
