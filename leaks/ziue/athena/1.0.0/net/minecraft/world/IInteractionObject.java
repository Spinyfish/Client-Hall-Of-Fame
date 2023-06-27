package net.minecraft.world;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;

public interface IInteractionObject extends IWorldNameable
{
    Container createContainer(final InventoryPlayer p0, final EntityPlayer p1);
    
    String getGuiID();
}
