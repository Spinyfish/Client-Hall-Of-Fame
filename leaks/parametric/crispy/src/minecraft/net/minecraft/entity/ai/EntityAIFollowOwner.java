package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowOwner extends EntityAIBase
{
    private EntityTameable thePet;
    private EntityLivingBase theOwner;
    World theWorld;
    private double followSpeed;
    private PathNavigate petPathfinder;
    private int field_75343_h;
    float maxDist;
    float minDist;
    private boolean field_75344_i;

    public EntityAIFollowOwner(EntityTameable thePetIn, double followSpeedIn, float minDistIn, float maxDistIn)
    {
        this.thePet = thePetIn;
        this.theWorld = thePetIn.worldObj;
        this.followSpeed = followSpeedIn;
        this.petPathfinder = thePetIn.getNavigator();
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;
        this.setMutexBits(3);

        if (!(thePetIn.getNavigator() instanceof PathNavigateGround))
        {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase var1 = this.thePet.getOwnerEntity();

        if (var1 == null)
        {
            return false;
        }
        else if (this.thePet.isSitting())
        {
            return false;
        }
        else if (this.thePet.getDistanceSqToEntity(var1) < (double)(this.minDist * this.minDist))
        {
            return false;
        }
        else
        {
            this.theOwner = var1;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > (double)(this.maxDist * this.maxDist) && !this.thePet.isSitting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_75343_h = 0;
        this.field_75344_i = ((PathNavigateGround)this.thePet.getNavigator()).getAvoidsWater();
        ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.theOwner = null;
        this.petPathfinder.clearPathEntity();
        ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(true);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, (float)this.thePet.getVerticalFaceSpeed());

        if (!this.thePet.isSitting())
        {
            if (--this.field_75343_h <= 0)
            {
                this.field_75343_h = 10;

                if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.followSpeed))
                {
                    if (!this.thePet.getLeashed())
                    {
                        if (this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0D)
                        {
                            int var1 = MathHelper.floor_double(this.theOwner.posX) - 2;
                            int var2 = MathHelper.floor_double(this.theOwner.posZ) - 2;
                            int var3 = MathHelper.floor_double(this.theOwner.getEntityBoundingBox().minY);

                            for (int var4 = 0; var4 <= 4; ++var4)
                            {
                                for (int var5 = 0; var5 <= 4; ++var5)
                                {
                                    if ((var4 < 1 || var5 < 1 || var4 > 3 || var5 > 3) && World.doesBlockHaveSolidTopSurface(this.theWorld, new BlockPos(var1 + var4, var3 - 1, var2 + var5)) && !this.theWorld.getBlockState(new BlockPos(var1 + var4, var3, var2 + var5)).getBlock().isFullCube() && !this.theWorld.getBlockState(new BlockPos(var1 + var4, var3 + 1, var2 + var5)).getBlock().isFullCube())
                                    {
                                        this.thePet.setLocationAndAngles((double)((float)(var1 + var4) + 0.5F), (double)var3, (double)((float)(var2 + var5) + 0.5F), this.thePet.rotationYaw, this.thePet.rotationPitch);
                                        this.petPathfinder.clearPathEntity();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
