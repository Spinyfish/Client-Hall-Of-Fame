package net.minecraft.world;

import net.minecraft.inventory.*;

public interface ILockableContainer extends IInventory, IInteractionObject
{
    boolean isLocked();
    
    void setLockCode(final LockCode p0);
    
    LockCode getLockCode();
}
