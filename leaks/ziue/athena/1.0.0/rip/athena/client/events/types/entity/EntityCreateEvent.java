package rip.athena.client.events.types.entity;

import rip.athena.client.events.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;

public class EntityCreateEvent extends Event
{
    private Entity player;
    private World world;
    
    public EntityCreateEvent(final Entity player, final World world) {
        this.player = player;
        this.world = world;
    }
    
    public Entity getEntity() {
        return this.player;
    }
    
    public World getWorld() {
        return this.world;
    }
}
