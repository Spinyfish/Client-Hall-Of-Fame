package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S02PacketChat implements Packet<INetHandlerPlayClient> {
   private IChatComponent chatComponent;
   private byte type;

   public S02PacketChat() {
   }

   public S02PacketChat(IChatComponent component) {
      this(component, (byte)1);
   }

   public S02PacketChat(IChatComponent message, byte typeIn) {
      this.chatComponent = message;
      this.type = typeIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.chatComponent = buf.readChatComponent();
      this.type = buf.readByte();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeChatComponent(this.chatComponent);
      buf.writeByte(this.type);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleChat(this);
   }

   public IChatComponent getChatComponent() {
      return this.chatComponent;
   }

   public boolean isChat() {
      return this.type == 1 || this.type == 2;
   }

   public byte getType() {
      return this.type;
   }

   public void setChatComponent(IChatComponent chatComponent) {
      this.chatComponent = chatComponent;
   }

   public void setType(byte type) {
      this.type = type;
   }
}