package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;

public class EntityAIFollowGolem extends EntityAIBase {
   private final EntityVillager theVillager;
   private EntityIronGolem theGolem;
   private int takeGolemRoseTick;
   private boolean tookGolemRose;

   public EntityAIFollowGolem(EntityVillager theVillagerIn) {
      this.theVillager = theVillagerIn;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      if (this.theVillager.getGrowingAge() >= 0) {
         return false;
      } else if (!this.theVillager.worldObj.isDaytime()) {
         return false;
      } else {
         List<EntityIronGolem> list = this.theVillager
            .worldObj
            .getEntitiesWithinAABB(EntityIronGolem.class, this.theVillager.getEntityBoundingBox().expand(6.0, 2.0, 6.0));
         if (list.isEmpty()) {
            return false;
         } else {
            for(EntityIronGolem entityirongolem : list) {
               if (entityirongolem.getHoldRoseTick() > 0) {
                  this.theGolem = entityirongolem;
                  break;
               }
            }

            return this.theGolem != null;
         }
      }
   }

   @Override
   public boolean continueExecuting() {
      return this.theGolem.getHoldRoseTick() <= 0;
   }

   @Override
   public void startExecuting() {
      this.takeGolemRoseTick = this.theVillager.getRNG().nextInt(320);
      this.tookGolemRose = false;
      this.theGolem.getNavigator().clearPathEntity();
   }

   @Override
   public void resetTask() {
      this.theGolem = null;
      this.theVillager.getNavigator().clearPathEntity();
   }

   @Override
   public void updateTask() {
      this.theVillager.getLookHelper().setLookPositionWithEntity(this.theGolem, 30.0F, 30.0F);
      if (this.theGolem.getHoldRoseTick() == this.takeGolemRoseTick) {
         this.theVillager.getNavigator().tryMoveToEntityLiving(this.theGolem, 0.5);
         this.tookGolemRose = true;
      }

      if (this.tookGolemRose && this.theVillager.getDistanceSqToEntity(this.theGolem) < 4.0) {
         this.theGolem.setHoldingRose(false);
         this.theVillager.getNavigator().clearPathEntity();
      }
   }
}
