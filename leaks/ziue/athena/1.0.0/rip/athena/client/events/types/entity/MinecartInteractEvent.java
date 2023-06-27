package rip.athena.client.events.types.entity;

import rip.athena.client.events.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;

public class MinecartInteractEvent extends Event
{
    private Entity entity;
    private EntityMinecart minecart;
    
    public MinecartInteractEvent(final Entity entity, final EntityMinecart minecart) {
        this.entity = entity;
        this.minecart = minecart;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    public EntityMinecart getMinecart() {
        return this.minecart;
    }
}
