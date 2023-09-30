package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockReed extends Block {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

    protected BlockReed() {
        super(Material.plants);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        final float f = 0.375F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
        this.setTickRandomly(true);
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (worldIn.getBlockState(pos.down()).getBlock() == Blocks.reeds || this.checkForDrop(worldIn, pos, state)) {
            if (worldIn.isAirBlock(pos.up())) {
                int i;

                for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {
                }

                if (i < 3) {
                    final int j = state.getValue(AGE).intValue();

                    if (j == 15) {
                        worldIn.setBlockState(pos.up(), this.getDefaultState());
                        worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(0)), 4);
                    } else {
                        worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(j + 1)), 4);
                    }
                }
            }
        }
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        final Block block = worldIn.getBlockState(pos.down()).getBlock();

        if (block == this) {
            return true;
        } else if (block != Blocks.grass && block != Blocks.dirt && block != Blocks.sand) {
            return false;
        } else {
            for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                if (worldIn.getBlockState(pos.offset(enumfacing).down()).getBlock().getMaterial() == Material.water) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        this.checkForDrop(worldIn, pos, state);
    }

    protected final boolean checkForDrop(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (this.canBlockStay(worldIn, pos)) {
            return true;
        } else {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        }
    }

    public boolean canBlockStay(final World worldIn, final BlockPos pos) {
        return this.canPlaceBlockAt(worldIn, pos);
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        return null;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Items.reeds;
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

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Items.reeds;
    }

    public int colorMultiplier(final IBlockAccess worldIn, final BlockPos pos, final int renderPass) {
        return worldIn.getBiomeGenForCoords(pos).getGrassColorAtPos(pos);
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
