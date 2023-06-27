package net.minecraftforge.common.capabilities;

import net.minecraft.util.*;

public interface ICapabilityProvider
{
    boolean hasCapability(final Capability<?> p0, final EnumFacing p1);
    
     <T> T getCapability(final Capability<T> p0, final EnumFacing p1);
}
