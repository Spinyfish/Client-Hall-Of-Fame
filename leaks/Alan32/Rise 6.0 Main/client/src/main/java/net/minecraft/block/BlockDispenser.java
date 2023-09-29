package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.RegistryDefaulted;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDispenser extends BlockContainer {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");
    public static final RegistryDefaulted<Item, IBehaviorDispenseItem> dispenseBehaviorRegistry = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
    protected Random rand = new Random();

    protected BlockDispenser() {
        super(Material.rock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TRIGGERED, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World worldIn) {
        return 4;
    }

    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        this.setDefaultDirection(worldIn, pos, state);
    }

    private void setDefaultDirection(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (!worldIn.isRemote) {
            EnumFacing enumfacing = state.getValue(FACING);
            final boolean flag = worldIn.getBlockState(pos.north()).getBlock().isFullBlock();
            final boolean flag1 = worldIn.getBlockState(pos.south()).getBlock().isFullBlock();

            if (enumfacing == EnumFacing.NORTH && flag && !flag1) {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && flag1 && !flag) {
                enumfacing = EnumFacing.NORTH;
            } else {
                final boolean flag2 = worldIn.getBlockState(pos.west()).getBlock().isFullBlock();
                final boolean flag3 = worldIn.getBlockState(pos.east()).getBlock().isFullBlock();

                if (enumfacing == EnumFacing.WEST && flag2 && !flag3) {
                    enumfacing = EnumFacing.EAST;
                } else if (enumfacing == EnumFacing.EAST && flag3 && !flag2) {
                    enumfacing = EnumFacing.WEST;
                }
            }

            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing).withProperty(TRIGGERED, Boolean.valueOf(false)), 2);
        }
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            final TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityDispenser) {
                playerIn.displayGUIChest((TileEntityDispenser) tileentity);

                if (tileentity instanceof TileEntityDropper) {
                    playerIn.triggerAchievement(StatList.field_181731_O);
                } else {
                    playerIn.triggerAchievement(StatList.field_181733_Q);
                }
            }

            return true;
        }
    }

    protected void dispense(final World worldIn, final BlockPos pos) {
        final BlockSourceImpl blocksourceimpl = new BlockSourceImpl(worldIn, pos);
        final TileEntityDispenser tileentitydispenser = blocksourceimpl.getBlockTileEntity();

        if (tileentitydispenser != null) {
            final int i = tileentitydispenser.getDispenseSlot();

            if (i < 0) {
                worldIn.playAuxSFX(1001, pos, 0);
            } else {
                final ItemStack itemstack = tileentitydispenser.getStackInSlot(i);
                final IBehaviorDispenseItem ibehaviordispenseitem = this.getBehavior(itemstack);

                if (ibehaviordispenseitem != IBehaviorDispenseItem.itemDispenseBehaviorProvider) {
                    final ItemStack itemstack1 = ibehaviordispenseitem.dispense(blocksourceimpl, itemstack);
                    tileentitydispenser.setInventorySlotContents(i, itemstack1.stackSize <= 0 ? null : itemstack1);
                }
            }
        }
    }

    protected IBehaviorDispenseItem getBehavior(final ItemStack stack) {
        return dispenseBehaviorRegistry.getObject(stack == null ? null : stack.getItem());
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        final boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
        final boolean flag1 = state.getValue(TRIGGERED).booleanValue();

        if (flag && !flag1) {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(true)), 4);
        } else if (!flag && flag1) {
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(false)), 4);
        }
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (!worldIn.isRemote) {
            this.dispense(worldIn, pos);
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileEntityDispenser();
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer)).withProperty(TRIGGERED, Boolean.valueOf(false));
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer)), 2);

        if (stack.hasDisplayName()) {
            final TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityDispenser) {
                ((TileEntityDispenser) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityDispenser) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityDispenser) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    /**
     * Get the position where the dispenser at the given Coordinates should dispense to.
     */
    public static IPosition getDispensePosition(final IBlockSource coords) {
        final EnumFacing enumfacing = getFacing(coords.getBlockMetadata());
        final double d0 = coords.getX() + 0.7D * (double) enumfacing.getFrontOffsetX();
        final double d1 = coords.getY() + 0.7D * (double) enumfacing.getFrontOffsetY();
        final double d2 = coords.getZ() + 0.7D * (double) enumfacing.getFrontOffsetZ();
        return new PositionImpl(d0, d1, d2);
    }

    /**
     * Get the facing of a dispenser with the given metadata
     */
    public static EnumFacing getFacing(final int meta) {
        return EnumFacing.getFront(meta & 7);
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(final World worldIn, final BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType() {
        return 3;
    }

    /**
     * Possibly modify the given BlockState before rendering it on an Entity (Minecarts, Endermen, ...)
     */
    public IBlockState getStateForEntityRender(final IBlockState state) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(TRIGGERED, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getIndex();

        if (state.getValue(TRIGGERED).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, TRIGGERED);
    }
}
