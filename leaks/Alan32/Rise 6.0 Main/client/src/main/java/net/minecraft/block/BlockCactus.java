package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCactus extends Block {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

    protected BlockCactus() {
        super(Material.cactus);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        final BlockPos blockpos = pos.up();

        if (worldIn.isAirBlock(blockpos)) {
            int i;

            for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {
            }

            if (i < 3) {
                final int j = state.getValue(AGE).intValue();

                if (j == 15) {
                    worldIn.setBlockState(blockpos, this.getDefaultState());
                    final IBlockState iblockstate = state.withProperty(AGE, Integer.valueOf(0));
                    worldIn.setBlockState(pos, iblockstate, 4);
                    this.onNeighborBlockChange(worldIn, blockpos, iblockstate, this);
                } else {
                    worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(j + 1)), 4);
                }
            }
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        final float f = 0.0625F;
        return new AxisAlignedBB((float) pos.getX() + f, pos.getY(), (float) pos.getZ() + f, (float) (pos.getX() + 1) - f, (float) (pos.getY() + 1) - f, (float) (pos.getZ() + 1) - f);
    }

    public AxisAlignedBB getSelectedBoundingBox(final World worldIn, final BlockPos pos) {
        final float f = 0.0625F;
        return new AxisAlignedBB((float) pos.getX() + f, pos.getY(), (float) pos.getZ() + f, (float) (pos.getX() + 1) - f, pos.getY() + 1, (float) (pos.getZ() + 1) - f);
    }

    public boolean isFullCube() {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!this.canBlockStay(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    public boolean canBlockStay(final World worldIn, final BlockPos pos) {
        for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getMaterial().isSolid()) {
                return false;
            }
        }

        final Block block = worldIn.getBlockState(pos.down()).getBlock();
        return block == Blocks.cactus || block == Blocks.sand;
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(final World worldIn, final BlockPos pos, final IBlockState state, final Entity entityIn) {
        entityIn.attackEntityFrom(DamageSource.cactus, 1.0F);
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(AGE).intValue();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, AGE);
    }
}
