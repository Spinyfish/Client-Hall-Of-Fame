package net.minecraft.block;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockRedstoneRepeater extends BlockRedstoneDiode {
    public static final PropertyBool LOCKED = PropertyBool.create("locked");
    public static final PropertyInteger DELAY = PropertyInteger.create("delay", 1, 4);

    protected BlockRedstoneRepeater(final boolean powered) {
        super(powered);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DELAY, Integer.valueOf(1)).withProperty(LOCKED, Boolean.valueOf(false)));
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName() {
        return StatCollector.translateToLocal("item.diode.name");
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return state.withProperty(LOCKED, Boolean.valueOf(this.isLocked(worldIn, pos, state)));
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (!playerIn.capabilities.allowEdit) {
            return false;
        } else {
            worldIn.setBlockState(pos, state.cycleProperty(DELAY), 3);
            return true;
        }
    }

    protected int getDelay(final IBlockState state) {
        return state.getValue(DELAY).intValue() * 2;
    }

    protected IBlockState getPoweredState(final IBlockState unpoweredState) {
        final Integer integer = unpoweredState.getValue(DELAY);
        final Boolean obool = unpoweredState.getValue(LOCKED);
        final EnumFacing enumfacing = unpoweredState.getValue(FACING);
        return Blocks.powered_repeater.getDefaultState().withProperty(FACING, enumfacing).withProperty(DELAY, integer).withProperty(LOCKED, obool);
    }

    protected IBlockState getUnpoweredState(final IBlockState poweredState) {
        final Integer integer = poweredState.getValue(DELAY);
        final Boolean obool = poweredState.getValue(LOCKED);
        final EnumFacing enumfacing = poweredState.getValue(FACING);
        return Blocks.unpowered_repeater.getDefaultState().withProperty(FACING, enumfacing).withProperty(DELAY, integer).withProperty(LOCKED, obool);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Items.repeater;
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Items.repeater;
    }

    public boolean isLocked(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state) {
        return this.getPowerOnSides(worldIn, pos, state) > 0;
    }

    protected boolean canPowerSide(final Block blockIn) {
        return isRedstoneRepeaterBlockID(blockIn);
    }

    public void randomDisplayTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (this.isRepeaterPowered) {
            final EnumFacing enumfacing = state.getValue(FACING);
            final double d0 = (double) ((float) pos.getX() + 0.5F) + (double) (rand.nextFloat() - 0.5F) * 0.2D;
            final double d1 = (double) ((float) pos.getY() + 0.4F) + (double) (rand.nextFloat() - 0.5F) * 0.2D;
            final double d2 = (double) ((float) pos.getZ() + 0.5F) + (double) (rand.nextFloat() - 0.5F) * 0.2D;
            float f = -5.0F;

            if (rand.nextBoolean()) {
                f = (float) (state.getValue(DELAY).intValue() * 2 - 1);
            }

            f = f / 16.0F;
            final double d3 = f * (float) enumfacing.getFrontOffsetX();
            final double d4 = f * (float) enumfacing.getFrontOffsetZ();
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
        }
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        this.notifyNeighbors(worldIn, pos, state);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(LOCKED, Boolean.valueOf(false)).withProperty(DELAY, Integer.valueOf(1 + (meta >> 2)));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();
        i = i | state.getValue(DELAY).intValue() - 1 << 2;
        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, DELAY, LOCKED);
    }
}
