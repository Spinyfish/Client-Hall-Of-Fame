package rip.athena.client.events.types.entity;

import rip.athena.client.events.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class PlayerInteractEvent extends Event
{
    private Action action;
    private BlockPos pos;
    private World world;
    
    public PlayerInteractEvent(final Action action, final BlockPos pos, final World world) {
        this.action = action;
        this.pos = pos;
        this.world = world;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public enum Action
    {
        USE_ITEM, 
        LEFT_CLICK_BLOCK, 
        RIGHT_CLICK_BLOCK;
    }
}
