package net.minecraft.block;

import net.minecraft.world.*;
import net.minecraft.tileentity.*;

public interface ITileEntityProvider
{
    TileEntity createNewTileEntity(final World p0, final int p1);
}
