package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockFurnace extends BlockContainer
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private final boolean isBurning;
    private static boolean keepInventory;

    protected BlockFurnace(boolean isBurning)
    {
        super(Material.rock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.isBurning = isBurning;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *  
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.furnace);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            Block var4 = worldIn.getBlockState(pos.north()).getBlock();
            Block var5 = worldIn.getBlockState(pos.south()).getBlock();
            Block var6 = worldIn.getBlockState(pos.west()).getBlock();
            Block var7 = worldIn.getBlockState(pos.east()).getBlock();
            EnumFacing var8 = (EnumFacing)state.getValue(FACING);

            if (var8 == EnumFacing.NORTH && var4.isFullBlock() && !var5.isFullBlock())
            {
                var8 = EnumFacing.SOUTH;
            }
            else if (var8 == EnumFacing.SOUTH && var5.isFullBlock() && !var4.isFullBlock())
            {
                var8 = EnumFacing.NORTH;
            }
            else if (var8 == EnumFacing.WEST && var6.isFullBlock() && !var7.isFullBlock())
            {
                var8 = EnumFacing.EAST;
            }
            else if (var8 == EnumFacing.EAST && var7.isFullBlock() && !var6.isFullBlock())
            {
                var8 = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(FACING, var8), 2);
        }
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (this.isBurning)
        {
            EnumFacing var5 = (EnumFacing)state.getValue(FACING);
            double var6 = (double)pos.getX() + 0.5D;
            double var8 = (double)pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double var10 = (double)pos.getZ() + 0.5D;
            double var12 = 0.52D;
            double var14 = rand.nextDouble() * 0.6D - 0.3D;

            switch (BlockFurnace.SwitchEnumFacing.FACING_LOOKUP[var5.ordinal()])
            {
                case 1:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var6 - var12, var8, var10 + var14, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, var6 - var12, var8, var10 + var14, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;

                case 2:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var6 + var12, var8, var10 + var14, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, var6 + var12, var8, var10 + var14, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;

                case 3:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var6 + var14, var8, var10 - var12, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, var6 + var14, var8, var10 - var12, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;

                case 4:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var6 + var14, var8, var10 + var12, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, var6 + var14, var8, var10 + var12, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity var9 = worldIn.getTileEntity(pos);

            if (var9 instanceof TileEntityFurnace)
            {
                playerIn.displayGUIChest((TileEntityFurnace)var9);
            }

            return true;
        }
    }

    public static void setState(boolean active, World worldIn, BlockPos pos)
    {
        IBlockState var3 = worldIn.getBlockState(pos);
        TileEntity var4 = worldIn.getTileEntity(pos);
        keepInventory = true;

        if (active)
        {
            worldIn.setBlockState(pos, Blocks.lit_furnace.getDefaultState().withProperty(FACING, var3.getValue(FACING)), 3);
            worldIn.setBlockState(pos, Blocks.lit_furnace.getDefaultState().withProperty(FACING, var3.getValue(FACING)), 3);
        }
        else
        {
            worldIn.setBlockState(pos, Blocks.furnace.getDefaultState().withProperty(FACING, var3.getValue(FACING)), 3);
            worldIn.setBlockState(pos, Blocks.furnace.getDefaultState().withProperty(FACING, var3.getValue(FACING)), 3);
        }

        keepInventory = false;

        if (var4 != null)
        {
            var4.validate();
            worldIn.setTileEntity(pos, var4);
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityFurnace();
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        if (stack.hasDisplayName())
        {
            TileEntity var6 = worldIn.getTileEntity(pos);

            if (var6 instanceof TileEntityFurnace)
            {
                ((TileEntityFurnace)var6).setCustomInventoryName(stack.getDisplayName());
            }
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!keepInventory)
        {
            TileEntity var4 = worldIn.getTileEntity(pos);

            if (var4 instanceof TileEntityFurnace)
            {
                InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityFurnace)var4);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(World worldIn, BlockPos pos)
    {
        return Item.getItemFromBlock(Blocks.furnace);
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 3;
    }

    /**
     * Possibly modify the given BlockState before rendering it on an Entity (Minecarts, Endermen, ...)
     */
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing var2 = EnumFacing.getFront(meta);

        if (var2.getAxis() == EnumFacing.Axis.Y)
        {
            var2 = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, var2);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING});
    }

    static final class SwitchEnumFacing
    {
        static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];

        static
        {
            try
            {
                FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 1;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 2;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 3;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 4;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
