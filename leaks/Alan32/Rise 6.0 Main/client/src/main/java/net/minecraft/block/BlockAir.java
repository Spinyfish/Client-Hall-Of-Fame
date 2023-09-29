package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.IdentityHashMap;
import java.util.Map;

public class BlockAir extends Block {
    private static final Map mapOriginalOpacity = new IdentityHashMap();

    protected BlockAir() {
        super(Material.air);
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType() {
        return -1;
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        return null;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean canCollideCheck(final IBlockState state, final boolean hitIfLiquid) {
        return false;
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *
     * @param chance  The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
    }

    /**
     * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
     */
    public boolean isReplaceable(final World worldIn, final BlockPos pos) {
        return true;
    }

    public static void setLightOpacity(final Block p_setLightOpacity_0_, final int p_setLightOpacity_1_) {
        if (!mapOriginalOpacity.containsKey(p_setLightOpacity_0_)) {
            mapOriginalOpacity.put(p_setLightOpacity_0_, p_setLightOpacity_0_.lightOpacity);
        }

        p_setLightOpacity_0_.lightOpacity = p_setLightOpacity_1_;
    }

    public static void restoreLightOpacity(final Block p_restoreLightOpacity_0_) {
        if (mapOriginalOpacity.containsKey(p_restoreLightOpacity_0_)) {
            final int i = (Integer) mapOriginalOpacity.get(p_restoreLightOpacity_0_);
            setLightOpacity(p_restoreLightOpacity_0_, i);
        }
    }
}
