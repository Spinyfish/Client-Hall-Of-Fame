package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCocoa extends BlockDirectional implements IGrowable {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 2);

    public BlockCocoa() {
        super(Material.plants);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            this.dropBlock(worldIn, pos, state);
        } else if (worldIn.rand.nextInt(5) == 0) {
            final int i = state.getValue(AGE).intValue();

            if (i < 2) {
                worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i + 1)), 2);
            }
        }
    }

    public boolean canBlockStay(final World worldIn, BlockPos pos, final IBlockState state) {
        pos = pos.offset(state.getValue(FACING));
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        return iblockstate.getBlock() == Blocks.log && iblockstate.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE;
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

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    public AxisAlignedBB getSelectedBoundingBox(final World worldIn, final BlockPos pos) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getSelectedBoundingBox(worldIn, pos);
    }

    @SuppressWarnings("incomplete-switch")
    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        final EnumFacing enumfacing = iblockstate.getValue(FACING);
        final int i = iblockstate.getValue(AGE).intValue();
        final int j = 4 + i * 2;
        final int k = 5 + i * 2;
        final float f = (float) j / 2.0F;

        switch (enumfacing) {
            case SOUTH:
                this.setBlockBounds((8.0F - f) / 16.0F, (12.0F - (float) k) / 16.0F, (15.0F - (float) j) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F);
                break;

            case NORTH:
                this.setBlockBounds((8.0F - f) / 16.0F, (12.0F - (float) k) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + (float) j) / 16.0F);
                break;

            case WEST:
                this.setBlockBounds(0.0625F, (12.0F - (float) k) / 16.0F, (8.0F - f) / 16.0F, (1.0F + (float) j) / 16.0F, 0.75F, (8.0F + f) / 16.0F);
                break;

            case EAST:
                this.setBlockBounds((15.0F - (float) j) / 16.0F, (12.0F - (float) k) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F);
        }
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        final EnumFacing enumfacing = EnumFacing.fromAngle(placer.rotationYaw);
        worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        if (!facing.getAxis().isHorizontal()) {
            facing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, facing.getOpposite()).withProperty(AGE, Integer.valueOf(0));
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            this.dropBlock(worldIn, pos, state);
        }
    }

    private void dropBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        worldIn.setBlockState(pos, Blocks.air.getDefaultState(), 3);
        this.dropBlockAsItem(worldIn, pos, state, 0);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *
     * @param chance  The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        final int i = state.getValue(AGE).intValue();
        int j = 1;

        if (i >= 2) {
            j = 3;
        }

        for (int k = 0; k < j; ++k) {
            spawnAsEntity(worldIn, pos, new ItemStack(Items.dye, 1, EnumDyeColor.BROWN.getDyeDamage()));
        }
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Items.dye;
    }

    public int getDamageValue(final World worldIn, final BlockPos pos) {
        return EnumDyeColor.BROWN.getDyeDamage();
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(final World worldIn, final BlockPos pos, final IBlockState state, final boolean isClient) {
        return state.getValue(AGE).intValue() < 2;
    }

    public boolean canUseBonemeal(final World worldIn, final Random rand, final BlockPos pos, final IBlockState state) {
        return true;
    }

    public void grow(final World worldIn, final Random rand, final BlockPos pos, final IBlockState state) {
        worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(state.getValue(AGE).intValue() + 1)), 2);
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(AGE, Integer.valueOf((meta & 15) >> 2));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();
        i = i | state.getValue(AGE).intValue() << 2;
        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, AGE);
    }
}
