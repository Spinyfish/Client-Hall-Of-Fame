/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ITileEntityProvider {
    public TileEntity createNewTileEntity(World var1, int var2);
}

