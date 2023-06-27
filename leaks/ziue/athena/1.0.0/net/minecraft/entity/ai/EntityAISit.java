package net.minecraft.entity.ai;

import net.minecraft.entity.passive.*;
import net.minecraft.entity.*;

public class EntityAISit extends EntityAIBase
{
    private EntityTameable theEntity;
    private boolean isSitting;
    
    public EntityAISit(final EntityTameable entityIn) {
        this.theEntity = entityIn;
        this.setMutexBits(5);
    }
    
    @Override
    public boolean shouldExecute() {
        if (!this.theEntity.isTamed()) {
            return false;
        }
        if (this.theEntity.isInWater()) {
            return false;
        }
        if (!this.theEntity.onGround) {
            return false;
        }
        final EntityLivingBase entitylivingbase = this.theEntity.getOwner();
        return entitylivingbase == null || ((this.theEntity.getDistanceSqToEntity(entitylivingbase) >= 144.0 || entitylivingbase.getAITarget() == null) && this.isSitting);
    }
    
    @Override
    public void startExecuting() {
        this.theEntity.getNavigator().clearPathEntity();
        this.theEntity.setSitting(true);
    }
    
    @Override
    public void resetTask() {
        this.theEntity.setSitting(false);
    }
    
    public void setSitting(final boolean sitting) {
        this.isSitting = sitting;
    }
}
