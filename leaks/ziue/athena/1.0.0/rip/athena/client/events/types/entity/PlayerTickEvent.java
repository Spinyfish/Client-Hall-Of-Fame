package rip.athena.client.events.types.entity;

import rip.athena.client.events.*;
import net.minecraft.entity.player.*;

public class PlayerTickEvent extends Event
{
    private EntityPlayer player;
    
    public PlayerTickEvent(final EntityPlayer player) {
        this.player = player;
    }
    
    public EntityPlayer getPlayer() {
        return this.player;
    }
}
