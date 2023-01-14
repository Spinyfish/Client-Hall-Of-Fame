package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S40PacketDisconnect implements Packet<INetHandlerPlayClient> {
   private IChatComponent reason;

   public S40PacketDisconnect() {
   }

   public S40PacketDisconnect(IChatComponent reasonIn) {
      this.reason = reasonIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.reason = buf.readChatComponent();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeChatComponent(this.reason);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleDisconnect(this);
   }

   public IChatComponent getReason() {
      return this.reason;
   }
}
