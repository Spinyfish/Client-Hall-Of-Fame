package net.minecraft.client.renderer;

import net.minecraft.util.BlockPos;

public class DestroyBlockProgress {
   private final BlockPos position;
   private int partialBlockProgress;
   private int createdAtCloudUpdateTick;

   public DestroyBlockProgress(BlockPos positionIn) {
      this.position = positionIn;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void setPartialBlockDamage(int damage) {
      if (damage > 10) {
         damage = 10;
      }

      this.partialBlockProgress = damage;
   }

   public int getPartialBlockDamage() {
      return this.partialBlockProgress;
   }

   public void setCloudUpdateTick(int createdAtCloudUpdateTickIn) {
      this.createdAtCloudUpdateTick = createdAtCloudUpdateTickIn;
   }

   public int getCreationCloudUpdateTick() {
      return this.createdAtCloudUpdateTick;
   }
}
