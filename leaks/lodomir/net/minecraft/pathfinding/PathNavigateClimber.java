/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class PathNavigateClimber
extends PathNavigateGround {
    private BlockPos targetPosition;

    public PathNavigateClimber(EntityLiving entityLivingIn, World worldIn) {
        super(entityLivingIn, worldIn);
    }

    @Override
    public PathEntity getPathToPos(BlockPos pos) {
        this.targetPosition = pos;
        return super.getPathToPos(pos);
    }

    @Override
    public PathEntity getPathToEntityLiving(Entity entityIn) {
        this.targetPosition = new BlockPos(entityIn);
        return super.getPathToEntityLiving(entityIn);
    }

    @Override
    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
        PathEntity pathentity = this.getPathToEntityLiving(entityIn);
        if (pathentity != null) {
            return this.setPath(pathentity, speedIn);
        }
        this.targetPosition = new BlockPos(entityIn);
        this.speed = speedIn;
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public void onUpdateNavigation() {
        block4: {
            block5: {
                if (!this.noPath()) {
                    super.onUpdateNavigation();
                    return;
                }
                if (this.targetPosition == null) return;
                double d0 = this.theEntity.width * this.theEntity.width;
                if (!(this.theEntity.getDistanceSqToCenter(this.targetPosition) >= d0)) break block4;
                if (this.theEntity.posY <= (double)this.targetPosition.getY()) break block5;
                BlockPos blockPos = new BlockPos(this.targetPosition.getX(), MathHelper.floor_double(this.theEntity.posY), this.targetPosition.getZ());
                if (!(this.theEntity.getDistanceSqToCenter(blockPos) >= d0)) break block4;
            }
            this.theEntity.getMoveHelper().setMoveTo(this.targetPosition.getX(), this.targetPosition.getY(), this.targetPosition.getZ(), this.speed);
            return;
        }
        this.targetPosition = null;
    }
}

