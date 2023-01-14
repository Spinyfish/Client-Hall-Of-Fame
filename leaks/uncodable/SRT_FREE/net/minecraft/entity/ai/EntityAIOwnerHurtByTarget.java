package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtByTarget extends EntityAITarget {
   final EntityTameable theDefendingTameable;
   EntityLivingBase theOwnerAttacker;
   private int field_142051_e;

   public EntityAIOwnerHurtByTarget(EntityTameable theDefendingTameableIn) {
      super(theDefendingTameableIn, false);
      this.theDefendingTameable = theDefendingTameableIn;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theDefendingTameable.isTamed()) {
         return false;
      } else {
         EntityLivingBase entitylivingbase = this.theDefendingTameable.getOwner();
         if (entitylivingbase == null) {
            return false;
         } else {
            this.theOwnerAttacker = entitylivingbase.getAITarget();
            int i = entitylivingbase.getRevengeTimer();
            return i != this.field_142051_e
               && this.isSuitableTarget(this.theOwnerAttacker)
               && this.theDefendingTameable.shouldAttackEntity(this.theOwnerAttacker, entitylivingbase);
         }
      }
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.theOwnerAttacker);
      EntityLivingBase entitylivingbase = this.theDefendingTameable.getOwner();
      if (entitylivingbase != null) {
         this.field_142051_e = entitylivingbase.getRevengeTimer();
      }

      super.startExecuting();
   }
}
