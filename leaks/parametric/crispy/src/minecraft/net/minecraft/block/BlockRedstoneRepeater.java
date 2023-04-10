package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.IProperty;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneDiode
{
    public static final PropertyBool LOCKED = PropertyBool.create("locked");
    public static final PropertyInteger DELAY = PropertyInteger.create("delay", 1, 4);

    protected BlockRedstoneRepeater(boolean powered)
    {
        super(powered);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DELAY, Integer.valueOf(1)).withProperty(LOCKED, Boolean.valueOf(false)));
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(LOCKED, Boolean.valueOf(this.isLocked(worldIn, pos, state)));
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!playerIn.capabilities.allowEdit)
        {
            return false;
        }
        else
        {
            worldIn.setBlockState(pos, state.cycleProperty(DELAY), 3);
            return true;
        }
    }

    protected int getDelay(IBlockState state)
    {
        return ((Integer)state.getValue(DELAY)).intValue() * 2;
    }

    protected IBlockState getPoweredState(IBlockState unpoweredState)
    {
        Integer var2 = (Integer)unpoweredState.getValue(DELAY);
        Boolean var3 = (Boolean)unpoweredState.getValue(LOCKED);
        EnumFacing var4 = (EnumFacing)unpoweredState.getValue(FACING);
        return Blocks.powered_repeater.getDefaultState().withProperty(FACING, var4).withProperty(DELAY, var2).withProperty(LOCKED, var3);
    }

    protected IBlockState getUnpoweredState(IBlockState poweredState)
    {
        Integer var2 = (Integer)poweredState.getValue(DELAY);
        Boolean var3 = (Boolean)poweredState.getValue(LOCKED);
        EnumFacing var4 = (EnumFacing)poweredState.getValue(FACING);
        return Blocks.unpowered_repeater.getDefaultState().withProperty(FACING, var4).withProperty(DELAY, var2).withProperty(LOCKED, var3);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *  
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.repeater;
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(World worldIn, BlockPos pos)
    {
        return Items.repeater;
    }

    public boolean isLocked(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        return this.getPowerOnSides(worldIn, pos, state) > 0;
    }

    protected boolean canPowerSide(Block blockIn)
    {
        return isRedstoneRepeaterBlockID(blockIn);
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (this.isRepeaterPowered)
        {
            EnumFacing var5 = (EnumFacing)state.getValue(FACING);
            double var6 = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            double var8 = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            double var10 = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            float var12 = -5.0F;

            if (rand.nextBoolean())
            {
                var12 = (float)(((Integer)state.getValue(DELAY)).intValue() * 2 - 1);
            }

            var12 /= 16.0F;
            double var13 = (double)(var12 * (float)var5.getFrontOffsetX());
            double var15 = (double)(var12 * (float)var5.getFrontOffsetZ());
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, var6 + var13, var8, var10 + var15, 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        this.notifyNeighbors(worldIn, pos, state);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(LOCKED, Boolean.valueOf(false)).withProperty(DELAY, Integer.valueOf(1 + (meta >> 2)));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        byte var2 = 0;
        int var3 = var2 | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
        var3 |= ((Integer)state.getValue(DELAY)).intValue() - 1 << 2;
        return var3;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, DELAY, LOCKED});
    }
}
