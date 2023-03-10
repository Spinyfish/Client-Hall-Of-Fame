/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveIndoors
extends EntityAIBase {
    private EntityCreature entityObj;
    private VillageDoorInfo doorInfo;
    private int insidePosX = -1;
    private int insidePosZ = -1;

    public EntityAIMoveIndoors(EntityCreature entityObjIn) {
        this.entityObj = entityObjIn;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        BlockPos blockpos = new BlockPos(this.entityObj);
        if ((!this.entityObj.worldObj.isDaytime() || this.entityObj.worldObj.isRaining() && !this.entityObj.worldObj.getBiomeGenForCoords(blockpos).canSpawnLightningBolt()) && !this.entityObj.worldObj.provider.getHasNoSky()) {
            if (this.entityObj.getRNG().nextInt(50) != 0) {
                return false;
            }
            if (this.insidePosX != -1 && this.entityObj.getDistanceSq(this.insidePosX, this.entityObj.posY, this.insidePosZ) < 4.0) {
                return false;
            }
            Village village = this.entityObj.worldObj.getVillageCollection().getNearestVillage(blockpos, 14);
            if (village == null) {
                return false;
            }
            this.doorInfo = village.getDoorInfo(blockpos);
            return this.doorInfo != null;
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        return !this.entityObj.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.insidePosX = -1;
        BlockPos blockpos = this.doorInfo.getInsideBlockPos();
        int i = blockpos.getX();
        int j = blockpos.getY();
        int k = blockpos.getZ();
        if (this.entityObj.getDistanceSq(blockpos) > 256.0) {
            Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.entityObj, 14, 3, new Vec3((double)i + 0.5, j, (double)k + 0.5));
            if (vec3 != null) {
                this.entityObj.getNavigator().tryMoveToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord, 1.0);
            }
        } else {
            this.entityObj.getNavigator().tryMoveToXYZ((double)i + 0.5, j, (double)k + 0.5, 1.0);
        }
    }

    @Override
    public void resetTask() {
        this.insidePosX = this.doorInfo.getInsideBlockPos().getX();
        this.insidePosZ = this.doorInfo.getInsideBlockPos().getZ();
        this.doorInfo = null;
    }
}

