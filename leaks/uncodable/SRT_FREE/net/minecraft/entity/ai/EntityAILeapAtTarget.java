package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityAILeapAtTarget extends EntityAIBase {
   final EntityLiving leaper;
   EntityLivingBase leapTarget;
   final float leapMotionY;

   public EntityAILeapAtTarget(EntityLiving leapingEntity, float leapMotionYIn) {
      this.leaper = leapingEntity;
      this.leapMotionY = leapMotionYIn;
      this.setMutexBits(5);
   }

   @Override
   public boolean shouldExecute() {
      this.leapTarget = this.leaper.getAttackTarget();
      if (this.leapTarget == null) {
         return false;
      } else {
         double d0 = this.leaper.getDistanceSqToEntity(this.leapTarget);
         return d0 >= 4.0 && d0 <= 16.0 && this.leaper.onGround && this.leaper.getRNG().nextInt(5) == 0;
      }
   }

   @Override
   public boolean continueExecuting() {
      return this.leaper.onGround;
   }

   @Override
   public void startExecuting() {
      double d0 = this.leapTarget.posX - this.leaper.posX;
      double d1 = this.leapTarget.posZ - this.leaper.posZ;
      float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
      this.leaper.motionX += d0 / (double)f * 0.5 * 0.8F + this.leaper.motionX * 0.2F;
      this.leaper.motionZ += d1 / (double)f * 0.5 * 0.8F + this.leaper.motionZ * 0.2F;
      this.leaper.motionY = (double)this.leapMotionY;
   }
}
