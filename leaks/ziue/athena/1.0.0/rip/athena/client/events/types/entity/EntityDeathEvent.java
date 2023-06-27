package rip.athena.client.events.types.entity;

import rip.athena.client.events.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;

public class EntityDeathEvent extends Event
{
    private Entity entity;
    private DamageSource cause;
    
    public EntityDeathEvent(final Entity entity, final DamageSource cause) {
        this.entity = entity;
        this.cause = cause;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    public DamageSource getCause() {
        return this.cause;
    }
}
