package net.minecraft.world;

import net.minecraft.util.*;

public interface IWorldNameable
{
    String getName();
    
    boolean hasCustomName();
    
    IChatComponent getDisplayName();
}
