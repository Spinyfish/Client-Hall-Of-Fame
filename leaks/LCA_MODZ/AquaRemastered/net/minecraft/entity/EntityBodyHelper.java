package net.minecraft.entity;

import events.listeners.EventFakePreMotion;
import events.listeners.EventPreMotion;
import net.minecraft.util.MathHelper;

public class EntityBodyHelper {
   private EntityLivingBase theLiving;
   private int rotationTickCounter;
   private float prevRenderYawHead;

   public EntityBodyHelper(EntityLivingBase p_i1611_1_) {
      this.theLiving = p_i1611_1_;
   }

   public void updateRenderAngles() {
      double d0 = this.theLiving.posX - this.theLiving.prevPosX;
      double d1 = this.theLiving.posZ - this.theLiving.prevPosZ;
      if (d0 * d0 + d1 * d1 > 2.5000003E-7F) {
         if (EventPreMotion.getInstance != null) {
            this.theLiving.renderYawOffset = EventPreMotion.getYaw();
         }

         if (EventFakePreMotion.getInstance != null) {
            this.theLiving.renderYawOffset = EventFakePreMotion.getYaw();
         }

         this.theLiving.rotationYawHead = this.computeAngleWithBound(this.theLiving.renderYawOffset, this.theLiving.rotationYawHead, 75.0F);
         this.prevRenderYawHead = this.theLiving.rotationYawHead;
         this.rotationTickCounter = 0;
      } else {
         float f = 75.0F;
         if (Math.abs(this.theLiving.rotationYawHead - this.prevRenderYawHead) > 15.0F) {
            this.rotationTickCounter = 0;
            this.prevRenderYawHead = this.theLiving.rotationYawHead;
         } else {
            ++this.rotationTickCounter;
            int i = 10;
            if (this.rotationTickCounter > 10) {
               f = Math.max(1.0F - (float)(this.rotationTickCounter - 10) / 10.0F, 0.0F) * 75.0F;
            }
         }
      }
   }

   private float computeAngleWithBound(float p_75665_1_, float p_75665_2_, float p_75665_3_) {
      float f = MathHelper.wrapAngleTo180_float(p_75665_1_ - p_75665_2_);
      if (f < -p_75665_3_) {
         f = -p_75665_3_;
      }

      if (f >= p_75665_3_) {
         f = p_75665_3_;
      }

      return p_75665_1_ - f;
   }
}
