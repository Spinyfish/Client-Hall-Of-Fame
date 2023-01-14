package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S05PacketSpawnPosition implements Packet<INetHandlerPlayClient> {
   private BlockPos spawnBlockPos;

   public S05PacketSpawnPosition() {
   }

   public S05PacketSpawnPosition(BlockPos spawnBlockPosIn) {
      this.spawnBlockPos = spawnBlockPosIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) {
      this.spawnBlockPos = buf.readBlockPos();
   }

   @Override
   public void writePacketData(PacketBuffer buf) {
      buf.writeBlockPos(this.spawnBlockPos);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSpawnPosition(this);
   }

   public BlockPos getSpawnPos() {
      return this.spawnBlockPos;
   }
}
