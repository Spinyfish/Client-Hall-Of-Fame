package net.minecraft.dispenser;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPosition;

public interface IBlockSource extends ILocatableSource
{
    double getX();

    double getY();

    double getZ();

    BlockPosition getBlockPos();

    int getBlockMetadata();

    <T extends TileEntity> T getBlockTileEntity();
}
