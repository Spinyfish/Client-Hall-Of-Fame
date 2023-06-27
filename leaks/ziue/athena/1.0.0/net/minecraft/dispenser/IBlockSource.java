package net.minecraft.dispenser;

import net.minecraft.util.*;
import net.minecraft.tileentity.*;

public interface IBlockSource extends ILocatableSource
{
    double getX();
    
    double getY();
    
    double getZ();
    
    BlockPos getBlockPos();
    
    int getBlockMetadata();
    
     <T extends TileEntity> T getBlockTileEntity();
}
