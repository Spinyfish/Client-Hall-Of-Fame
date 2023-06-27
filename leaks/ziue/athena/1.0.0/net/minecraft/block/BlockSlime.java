package net.minecraft.block;

import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;

public class BlockSlime extends BlockBreakable
{
    public BlockSlime() {
        super(Material.clay, false, MapColor.grassColor);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.slipperiness = 0.8f;
    }
    
    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }
    
    @Override
    public void onFallenUpon(final World worldIn, final BlockPos pos, final Entity entityIn, final float fallDistance) {
        if (entityIn.isSneaking()) {
            super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        }
        else {
            entityIn.fall(fallDistance, 0.0f);
        }
    }
    
    @Override
    public void onLanded(final World worldIn, final Entity entityIn) {
        if (entityIn.isSneaking()) {
            super.onLanded(worldIn, entityIn);
        }
        else if (entityIn.motionY < 0.0) {
            entityIn.motionY = -entityIn.motionY;
        }
    }
    
    @Override
    public void onEntityCollidedWithBlock(final World worldIn, final BlockPos pos, final Entity entityIn) {
        if (Math.abs(entityIn.motionY) < 0.1 && !entityIn.isSneaking()) {
            final double d0 = 0.4 + Math.abs(entityIn.motionY) * 0.2;
            entityIn.motionX *= d0;
            entityIn.motionZ *= d0;
        }
        super.onEntityCollidedWithBlock(worldIn, pos, entityIn);
    }
}
