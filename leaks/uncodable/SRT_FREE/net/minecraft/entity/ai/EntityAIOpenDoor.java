package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIOpenDoor extends EntityAIDoorInteract {
   final boolean closeDoor;
   int closeDoorTemporisation;

   public EntityAIOpenDoor(EntityLiving entitylivingIn, boolean shouldClose) {
      super(entitylivingIn);
      this.theEntity = entitylivingIn;
      this.closeDoor = shouldClose;
   }

   @Override
   public boolean continueExecuting() {
      return !this.closeDoor || this.closeDoorTemporisation <= 0 || super.continueExecuting();
   }

   @Override
   public void startExecuting() {
      this.closeDoorTemporisation = 20;
      this.doorBlock.toggleDoor(this.theEntity.worldObj, this.doorPosition, true);
   }

   @Override
   public void resetTask() {
      if (this.closeDoor) {
         this.doorBlock.toggleDoor(this.theEntity.worldObj, this.doorPosition, false);
      }
   }

   @Override
   public void updateTask() {
      --this.closeDoorTemporisation;
      super.updateTask();
   }
}
