package net.minecraft.entity.ai;

import java.util.*;
import net.minecraft.entity.*;
import com.google.common.collect.*;

public class EntitySenses
{
    EntityLiving entityObj;
    List<Entity> seenEntities;
    List<Entity> unseenEntities;
    
    public EntitySenses(final EntityLiving entityObjIn) {
        this.seenEntities = (List<Entity>)Lists.newArrayList();
        this.unseenEntities = (List<Entity>)Lists.newArrayList();
        this.entityObj = entityObjIn;
    }
    
    public void clearSensingCache() {
        this.seenEntities.clear();
        this.unseenEntities.clear();
    }
    
    public boolean canSee(final Entity entityIn) {
        if (this.seenEntities.contains(entityIn)) {
            return true;
        }
        if (this.unseenEntities.contains(entityIn)) {
            return false;
        }
        this.entityObj.worldObj.theProfiler.startSection("canSee");
        final boolean flag = this.entityObj.canEntityBeSeen(entityIn);
        this.entityObj.worldObj.theProfiler.endSection();
        if (flag) {
            this.seenEntities.add(entityIn);
        }
        else {
            this.unseenEntities.add(entityIn);
        }
        return flag;
    }
}
