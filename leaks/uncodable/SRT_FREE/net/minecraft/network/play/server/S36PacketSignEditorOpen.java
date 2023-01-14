package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S36PacketSignEditorOpen implements Packet<INetHandlerPlayClient> {
   private BlockPos signPosition;

   public S36PacketSignEditorOpen() {
   }

   public S36PacketSignEditorOpen(BlockPos signPositionIn) {
      this.signPosition = signPositionIn;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSignEditorOpen(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.signPosition = buf.readBlockPos();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeBlockPos(this.signPosition);
   }

   public BlockPos getSignPosition() {
      return this.signPosition;
   }
}
