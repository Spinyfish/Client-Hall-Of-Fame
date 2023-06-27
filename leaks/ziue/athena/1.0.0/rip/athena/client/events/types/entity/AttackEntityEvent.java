package rip.athena.client.events.types.entity;

import rip.athena.client.events.*;
import net.minecraft.entity.*;

public class AttackEntityEvent extends Event
{
    private Entity entity;
    private Entity target;
    
    public AttackEntityEvent(final Entity entity, final Entity target) {
        this.entity = entity;
        this.target = target;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    public Entity getTarget() {
        return this.target;
    }
}
