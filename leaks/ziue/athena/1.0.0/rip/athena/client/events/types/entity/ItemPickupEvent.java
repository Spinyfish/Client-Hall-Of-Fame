package rip.athena.client.events.types.entity;

import rip.athena.client.events.*;
import net.minecraft.entity.*;

public class ItemPickupEvent extends Event
{
    private EntityLivingBase entity;
    private Entity item;
    
    public ItemPickupEvent(final EntityLivingBase entity, final Entity item) {
        this.entity = entity;
        this.item = item;
    }
    
    public EntityLivingBase getEntity() {
        return this.entity;
    }
    
    public Entity getItem() {
        return this.item;
    }
}
