package net.minecraft.inventory;

import net.minecraft.util.*;
import net.minecraft.item.*;

public interface ISidedInventory extends IInventory
{
    int[] getSlotsForFace(final EnumFacing p0);
    
    boolean canInsertItem(final int p0, final ItemStack p1, final EnumFacing p2);
    
    boolean canExtractItem(final int p0, final ItemStack p1, final EnumFacing p2);
}
