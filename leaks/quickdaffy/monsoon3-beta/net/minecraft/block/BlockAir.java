/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockAir
extends Block {
    private static Map mapOriginalOpacity = new IdentityHashMap();

    protected BlockAir() {
        super(Material.air);
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return null;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }

    @Override
    public boolean isReplaceable(World worldIn, BlockPos pos) {
        return true;
    }

    public static void setLightOpacity(Block p_setLightOpacity_0_, int p_setLightOpacity_1_) {
        if (!mapOriginalOpacity.containsKey(p_setLightOpacity_0_)) {
            mapOriginalOpacity.put(p_setLightOpacity_0_, p_setLightOpacity_0_.lightOpacity);
        }
        p_setLightOpacity_0_.lightOpacity = p_setLightOpacity_1_;
    }

    public static void restoreLightOpacity(Block p_restoreLightOpacity_0_) {
        if (mapOriginalOpacity.containsKey(p_restoreLightOpacity_0_)) {
            int i = (Integer)mapOriginalOpacity.get(p_restoreLightOpacity_0_);
            BlockAir.setLightOpacity(p_restoreLightOpacity_0_, i);
        }
    }
}

