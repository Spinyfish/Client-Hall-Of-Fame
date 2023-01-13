package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtTarget extends EntityAITarget {
   final EntityTameable theEntityTameable;
   EntityLivingBase theTarget;
   private int field_142050_e;

   public EntityAIOwnerHurtTarget(EntityTameable theEntityTameableIn) {
      super(theEntityTameableIn, false);
      this.theEntityTameable = theEntityTameableIn;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theEntityTameable.isTamed()) {
         return false;
      } else {
         EntityLivingBase entitylivingbase = this.theEntityTameable.getOwner();
         if (entitylivingbase == null) {
            return false;
         } else {
            this.theTarget = entitylivingbase.getLastAttacker();
            int i = entitylivingbase.getLastAttackerTime();
            return i != this.field_142050_e
               && this.isSuitableTarget(this.theTarget)
               && this.theEntityTameable.shouldAttackEntity(this.theTarget, entitylivingbase);
         }
      }
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.theTarget);
      EntityLivingBase entitylivingbase = this.theEntityTameable.getOwner();
      if (entitylivingbase != null) {
         this.field_142050_e = entitylivingbase.getLastAttackerTime();
      }

      super.startExecuting();
   }
}
