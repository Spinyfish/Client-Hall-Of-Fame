/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class EntityAIRunAroundLikeCrazy
extends EntityAIBase {
    private EntityHorse horseHost;
    private double speed;
    private double targetX;
    private double targetY;
    private double targetZ;

    public EntityAIRunAroundLikeCrazy(EntityHorse horse, double speedIn) {
        this.horseHost = horse;
        this.speed = speedIn;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.horseHost.isTame() && this.horseHost.riddenByEntity != null) {
            Vec3 vec3 = RandomPositionGenerator.findRandomTarget(this.horseHost, 5, 4);
            if (vec3 == null) {
                return false;
            }
            this.targetX = vec3.x;
            this.targetY = vec3.y;
            this.targetZ = vec3.z;
            return true;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        this.horseHost.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    @Override
    public boolean continueExecuting() {
        return !this.horseHost.getNavigator().noPath() && this.horseHost.riddenByEntity != null;
    }

    @Override
    public void updateTask() {
        if (this.horseHost.getRNG().nextInt(50) == 0) {
            if (this.horseHost.riddenByEntity instanceof EntityPlayer) {
                int i = this.horseHost.getTemper();
                int j = this.horseHost.getMaxTemper();
                if (j > 0 && this.horseHost.getRNG().nextInt(j) < i) {
                    this.horseHost.setTamedBy((EntityPlayer)this.horseHost.riddenByEntity);
                    this.horseHost.worldObj.setEntityState(this.horseHost, (byte)7);
                    return;
                }
                this.horseHost.increaseTemper(5);
            }
            this.horseHost.riddenByEntity.mountEntity(null);
            this.horseHost.riddenByEntity = null;
            this.horseHost.makeHorseRearWithSound();
            this.horseHost.worldObj.setEntityState(this.horseHost, (byte)6);
        }
    }
}

