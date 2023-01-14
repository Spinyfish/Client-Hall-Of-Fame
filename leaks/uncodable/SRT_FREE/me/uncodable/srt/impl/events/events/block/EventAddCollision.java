package me.uncodable.srt.impl.events.events.block;

import me.uncodable.srt.impl.events.api.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class EventAddCollision extends Event {
   private AxisAlignedBB boundingBox;
   private Block block;
   private BlockPos pos;

   public EventAddCollision(Block block, BlockPos pos, AxisAlignedBB boundingBox) {
      this.block = block;
      this.pos = pos;
      this.boundingBox = boundingBox;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public Block getBlock() {
      return this.block;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void setBoundingBox(AxisAlignedBB boundingBox) {
      this.boundingBox = boundingBox;
   }

   public void setBlock(Block block) {
      this.block = block;
   }

   public void setPos(BlockPos pos) {
      this.pos = pos;
   }
}
