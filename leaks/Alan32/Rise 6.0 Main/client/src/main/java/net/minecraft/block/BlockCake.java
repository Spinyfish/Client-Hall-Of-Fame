package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCake extends Block {
    public static final PropertyInteger BITES = PropertyInteger.create("bites", 0, 6);

    protected BlockCake() {
        super(Material.cake);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BITES, Integer.valueOf(0)));
        this.setTickRandomly(true);
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        final float f = 0.0625F;
        final float f1 = (float) (1 + worldIn.getBlockState(pos).getValue(BITES).intValue() * 2) / 16.0F;
        final float f2 = 0.5F;
        this.setBlockBounds(f1, 0.0F, f, 1.0F - f, f2, 1.0F - f);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        final float f = 0.0625F;
        final float f1 = 0.5F;
        this.setBlockBounds(f, 0.0F, f, 1.0F - f, f1, 1.0F - f);
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        final float f = 0.0625F;
        final float f1 = (float) (1 + state.getValue(BITES).intValue() * 2) / 16.0F;
        final float f2 = 0.5F;
        return new AxisAlignedBB((float) pos.getX() + f1, pos.getY(), (float) pos.getZ() + f, (float) (pos.getX() + 1) - f, (float) pos.getY() + f2, (float) (pos.getZ() + 1) - f);
    }

    public AxisAlignedBB getSelectedBoundingBox(final World worldIn, final BlockPos pos) {
        return this.getCollisionBoundingBox(worldIn, pos, worldIn.getBlockState(pos));
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

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        this.eatCake(worldIn, pos, state, playerIn);
        return true;
    }

    public void onBlockClicked(final World worldIn, final BlockPos pos, final EntityPlayer playerIn) {
        this.eatCake(worldIn, pos, worldIn.getBlockState(pos), playerIn);
    }

    private void eatCake(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer player) {
        if (player.canEat(false)) {
            player.triggerAchievement(StatList.field_181724_H);
            player.getFoodStats().addStats(2, 0.1F);
            final int i = state.getValue(BITES).intValue();

            if (i < 6) {
                worldIn.setBlockState(pos, state.withProperty(BITES, Integer.valueOf(i + 1)), 3);
            } else {
                worldIn.setBlockToAir(pos);
            }
        }
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!this.canBlockStay(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
        }
    }

    private boolean canBlockStay(final World worldIn, final BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getBlock().getMaterial().isSolid();
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random random) {
        return 0;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return null;
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Items.cake;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BITES, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BITES).intValue();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, BITES);
    }

    public int getComparatorInputOverride(final World worldIn, final BlockPos pos) {
        return (7 - worldIn.getBlockState(pos).getValue(BITES).intValue()) * 2;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }
}
