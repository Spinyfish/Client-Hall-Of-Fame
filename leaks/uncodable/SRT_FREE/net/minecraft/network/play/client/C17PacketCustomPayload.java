package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C17PacketCustomPayload implements Packet<INetHandlerPlayServer> {
   private String channel;
   private PacketBuffer data;

   public C17PacketCustomPayload() {
   }

   public C17PacketCustomPayload(String channelIn, PacketBuffer dataIn) {
      this.channel = channelIn;
      this.data = dataIn;
      if (dataIn.writerIndex() > 32767) {
         throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.channel = buf.readStringFromBuffer(20);
      int i = buf.readableBytes();
      if (i >= 0 && i <= 32767) {
         this.data = new PacketBuffer(buf.readBytes(i));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeString(this.channel);
      buf.writeBytes(this.data);
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processVanilla250Packet(this);
   }

   public String getChannelName() {
      return this.channel;
   }

   public PacketBuffer getBufferData() {
      return this.data;
   }

   public void setChannel(String channel) {
      this.channel = channel;
   }

   public void setData(PacketBuffer data) {
      this.data = data;
   }

   public String getChannel() {
      return this.channel;
   }

   public PacketBuffer getData() {
      return this.data;
   }
}
