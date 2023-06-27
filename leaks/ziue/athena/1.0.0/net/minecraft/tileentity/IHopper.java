package net.minecraft.tileentity;

import net.minecraft.inventory.*;
import net.minecraft.world.*;

public interface IHopper extends IInventory
{
    World getWorld();
    
    double getXPos();
    
    double getYPos();
    
    double getZPos();
}
