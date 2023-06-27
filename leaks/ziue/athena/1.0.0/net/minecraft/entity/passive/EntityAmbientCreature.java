package net.minecraft.entity.passive;

import net.minecraft.entity.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;

public abstract class EntityAmbientCreature extends EntityLiving implements IAnimals
{
    public EntityAmbientCreature(final World worldIn) {
        super(worldIn);
    }
    
    @Override
    public boolean allowLeashing() {
        return false;
    }
    
    @Override
    protected boolean interact(final EntityPlayer player) {
        return false;
    }
}
