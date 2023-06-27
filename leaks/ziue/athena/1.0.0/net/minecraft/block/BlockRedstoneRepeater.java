package net.minecraft.block;

import net.minecraft.block.properties.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.block.state.*;

public class BlockRedstoneRepeater extends BlockRedstoneDiode
{
    public static final PropertyBool LOCKED;
    public static final PropertyInteger DELAY;
    
    protected BlockRedstoneRepeater(final boolean powered) {
        super(powered);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty<Comparable>)BlockRedstoneRepeater.FACING, EnumFacing.NORTH).withProperty((IProperty<Comparable>)BlockRedstoneRepeater.DELAY, 1).withProperty((IProperty<Comparable>)BlockRedstoneRepeater.LOCKED, false));
    }
    
    @Override
    public String getLocalizedName() {
        return StatCollector.translateToLocal("item.diode.name");
    }
    
    @Override
    public IBlockState getActualState(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return state.withProperty((IProperty<Comparable>)BlockRedstoneRepeater.LOCKED, this.isLocked(worldIn, pos, state));
    }
    
    @Override
    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (!playerIn.capabilities.allowEdit) {
            return false;
        }
        worldIn.setBlockState(pos, state.cycleProperty((IProperty<Comparable>)BlockRedstoneRepeater.DELAY), 3);
        return true;
    }
    
    @Override
    protected int getDelay(final IBlockState state) {
        return state.getValue((IProperty<Integer>)BlockRedstoneRepeater.DELAY) * 2;
    }
    
    @Override
    protected IBlockState getPoweredState(final IBlockState unpoweredState) {
        final Integer integer = unpoweredState.getValue((IProperty<Integer>)BlockRedstoneRepeater.DELAY);
        final Boolean obool = unpoweredState.getValue((IProperty<Boolean>)BlockRedstoneRepeater.LOCKED);
        final EnumFacing enumfacing = unpoweredState.getValue((IProperty<EnumFacing>)BlockRedstoneRepeater.FACING);
        return Blocks.powered_repeater.getDefaultState().withProperty((IProperty<Comparable>)BlockRedstoneRepeater.FACING, enumfacing).withProperty((IProperty<Comparable>)BlockRedstoneRepeater.DELAY, integer).withProperty((IProperty<Comparable>)BlockRedstoneRepeater.LOCKED, obool);
    }
    
    @Override
    protected IBlockState getUnpoweredState(final IBlockState poweredState) {
        final Integer integer = poweredState.getValue((IProperty<Integer>)BlockRedstoneRepeater.DELAY);
        final Boolean obool = poweredState.getValue((IProperty<Boolean>)BlockRedstoneRepeater.LOCKED);
        final EnumFacing enumfacing = poweredState.getValue((IProperty<EnumFacing>)BlockRedstoneRepeater.FACING);
        return Blocks.unpowered_repeater.getDefaultState().withProperty((IProperty<Comparable>)BlockRedstoneRepeater.FACING, enumfacing).withProperty((IProperty<Comparable>)BlockRedstoneRepeater.DELAY, integer).withProperty((IProperty<Comparable>)BlockRedstoneRepeater.LOCKED, obool);
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Items.repeater;
    }
    
    @Override
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Items.repeater;
    }
    
    @Override
    public boolean isLocked(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state) {
        return this.getPowerOnSides(worldIn, pos, state) > 0;
    }
    
    @Override
    protected boolean canPowerSide(final Block blockIn) {
        return BlockRedstoneDiode.isRedstoneRepeaterBlockID(blockIn);
    }
    
    @Override
    public void randomDisplayTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (this.isRepeaterPowered) {
            final EnumFacing enumfacing = state.getValue((IProperty<EnumFacing>)BlockRedstoneRepeater.FACING);
            final double d0 = pos.getX() + 0.5f + (rand.nextFloat() - 0.5f) * 0.2;
            final double d2 = pos.getY() + 0.4f + (rand.nextFloat() - 0.5f) * 0.2;
            final double d3 = pos.getZ() + 0.5f + (rand.nextFloat() - 0.5f) * 0.2;
            float f = -5.0f;
            if (rand.nextBoolean()) {
                f = (float)(state.getValue((IProperty<Integer>)BlockRedstoneRepeater.DELAY) * 2 - 1);
            }
            f /= 16.0f;
            final double d4 = f * enumfacing.getFrontOffsetX();
            final double d5 = f * enumfacing.getFrontOffsetZ();
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0 + d4, d2, d3 + d5, 0.0, 0.0, 0.0, new int[0]);
        }
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        this.notifyNeighbors(worldIn, pos, state);
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty((IProperty<Comparable>)BlockRedstoneRepeater.FACING, EnumFacing.getHorizontal(meta)).withProperty((IProperty<Comparable>)BlockRedstoneRepeater.LOCKED, false).withProperty((IProperty<Comparable>)BlockRedstoneRepeater.DELAY, 1 + (meta >> 2));
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= state.getValue((IProperty<EnumFacing>)BlockRedstoneRepeater.FACING).getHorizontalIndex();
        i |= state.getValue((IProperty<Integer>)BlockRedstoneRepeater.DELAY) - 1 << 2;
        return i;
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockRedstoneRepeater.FACING, BlockRedstoneRepeater.DELAY, BlockRedstoneRepeater.LOCKED });
    }
    
    static {
        LOCKED = PropertyBool.create("locked");
        DELAY = PropertyInteger.create("delay", 1, 4);
    }
}
