package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityAIOcelotAttack extends EntityAIBase {
   final World theWorld;
   final EntityLiving theEntity;
   EntityLivingBase theVictim;
   int attackCountdown;

   public EntityAIOcelotAttack(EntityLiving theEntityIn) {
      this.theEntity = theEntityIn;
      this.theWorld = theEntityIn.worldObj;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase entitylivingbase = this.theEntity.getAttackTarget();
      if (entitylivingbase == null) {
         return false;
      } else {
         this.theVictim = entitylivingbase;
         return true;
      }
   }

   @Override
   public boolean continueExecuting() {
      return !this.theVictim.isEntityAlive()
         || this.theEntity.getDistanceSqToEntity(this.theVictim) > 225.0
         || this.theEntity.getNavigator().noPath() && !this.shouldExecute();
   }

   @Override
   public void resetTask() {
      this.theVictim = null;
      this.theEntity.getNavigator().clearPathEntity();
   }

   @Override
   public void updateTask() {
      this.theEntity.getLookHelper().setLookPositionWithEntity(this.theVictim, 30.0F, 30.0F);
      double d0 = (double)(this.theEntity.width * 2.0F * this.theEntity.width * 2.0F);
      double d1 = this.theEntity.getDistanceSq(this.theVictim.posX, this.theVictim.getEntityBoundingBox().minY, this.theVictim.posZ);
      double d2 = 0.8;
      if (d1 > d0 && d1 < 16.0) {
         d2 = 1.33;
      } else if (d1 < 225.0) {
         d2 = 0.6;
      }

      this.theEntity.getNavigator().tryMoveToEntityLiving(this.theVictim, d2);
      this.attackCountdown = Math.max(this.attackCountdown - 1, 0);
      if (d1 <= d0 && this.attackCountdown <= 0) {
         this.attackCountdown = 20;
         this.theEntity.attackEntityAsMob(this.theVictim);
      }
   }
}
