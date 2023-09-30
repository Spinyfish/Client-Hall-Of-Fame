package net.minecraft.block;

import dev.tenacity.module.impl.render.XRay;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumWorldBlockLayer;

import java.util.Random;

public class BlockGlass extends BlockBreakable
{
    public BlockGlass(Material materialIn, boolean ignoreSimilarity)
    {
        super(materialIn, ignoreSimilarity);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return XRay.enabled ? EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.CUTOUT;
    }

    public boolean isFullCube()
    {
        return false;
    }

    protected boolean canSilkHarvest()
    {
        return true;
    }
}
