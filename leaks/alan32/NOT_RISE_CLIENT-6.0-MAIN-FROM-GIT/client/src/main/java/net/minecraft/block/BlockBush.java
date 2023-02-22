package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

import java.util.Random;

public class BlockBush extends Block {
    protected BlockBush() {
        this(Material.plants);
    }

    protected BlockBush(final Material materialIn) {
        this(materialIn, materialIn.getMaterialMapColor());
    }

    protected BlockBush(final Material p_i46452_1_, final MapColor p_i46452_2_) {
        super(p_i46452_1_, p_i46452_2_);
        this.setTickRandomly(true);
        final float f = 0.2F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 3.0F, 0.5F + f);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(final Block ground) {
        return ground == Blocks.grass || ground == Blocks.dirt || ground == Blocks.farmland;
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
        this.checkAndDropBlock(worldIn, pos, state);
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    protected void checkAndDropBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockState(pos, Blocks.air.getDefaultState(), 3);
        }
    }

    public boolean canBlockStay(final World worldIn, final BlockPos pos, final IBlockState state) {
        return this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
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

    public boolean isFullCube() {
        return false;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }
}
