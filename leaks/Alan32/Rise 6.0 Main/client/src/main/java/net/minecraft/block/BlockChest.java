package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockChest extends BlockContainer {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    /**
     * 0 : Normal chest, 1 : Trapped chest
     */
    public final int chestType;

    protected BlockChest(final int type) {
        super(Material.wood);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.chestType = type;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType() {
        return 2;
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        if (worldIn.getBlockState(pos.north()).getBlock() == this) {
            this.setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
        } else if (worldIn.getBlockState(pos.south()).getBlock() == this) {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
        } else if (worldIn.getBlockState(pos.west()).getBlock() == this) {
            this.setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        } else if (worldIn.getBlockState(pos.east()).getBlock() == this) {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
        } else {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
    }

    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.checkForSurroundingChests(worldIn, pos, state);

        for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            final BlockPos blockpos = pos.offset(enumfacing);
            final IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() == this) {
                this.checkForSurroundingChests(worldIn, blockpos, iblockstate);
            }
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        final EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
        state = state.withProperty(FACING, enumfacing);
        final BlockPos blockpos = pos.north();
        final BlockPos blockpos1 = pos.south();
        final BlockPos blockpos2 = pos.west();
        final BlockPos blockpos3 = pos.east();
        final boolean flag = this == worldIn.getBlockState(blockpos).getBlock();
        final boolean flag1 = this == worldIn.getBlockState(blockpos1).getBlock();
        final boolean flag2 = this == worldIn.getBlockState(blockpos2).getBlock();
        final boolean flag3 = this == worldIn.getBlockState(blockpos3).getBlock();

        if (!flag && !flag1 && !flag2 && !flag3) {
            worldIn.setBlockState(pos, state, 3);
        } else if (enumfacing.getAxis() != EnumFacing.Axis.X || !flag && !flag1) {
            if (enumfacing.getAxis() == EnumFacing.Axis.Z && (flag2 || flag3)) {
                if (flag2) {
                    worldIn.setBlockState(blockpos2, state, 3);
                } else {
                    worldIn.setBlockState(blockpos3, state, 3);
                }

                worldIn.setBlockState(pos, state, 3);
            }
        } else {
            if (flag) {
                worldIn.setBlockState(blockpos, state, 3);
            } else {
                worldIn.setBlockState(blockpos1, state, 3);
            }

            worldIn.setBlockState(pos, state, 3);
        }

        if (stack.hasDisplayName()) {
            final TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    public IBlockState checkForSurroundingChests(final World worldIn, final BlockPos pos, IBlockState state) {
        if (worldIn.isRemote) {
            return state;
        } else {
            final IBlockState iblockstate = worldIn.getBlockState(pos.north());
            final IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
            final IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
            final IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = state.getValue(FACING);
            final Block block = iblockstate.getBlock();
            final Block block1 = iblockstate1.getBlock();
            final Block block2 = iblockstate2.getBlock();
            final Block block3 = iblockstate3.getBlock();

            if (block != this && block1 != this) {
                final boolean flag = block.isFullBlock();
                final boolean flag1 = block1.isFullBlock();

                if (block2 == this || block3 == this) {
                    final BlockPos blockpos1 = block2 == this ? pos.west() : pos.east();
                    final IBlockState iblockstate6 = worldIn.getBlockState(blockpos1.north());
                    final IBlockState iblockstate7 = worldIn.getBlockState(blockpos1.south());
                    enumfacing = EnumFacing.SOUTH;
                    final EnumFacing enumfacing2;

                    if (block2 == this) {
                        enumfacing2 = iblockstate2.getValue(FACING);
                    } else {
                        enumfacing2 = iblockstate3.getValue(FACING);
                    }

                    if (enumfacing2 == EnumFacing.NORTH) {
                        enumfacing = EnumFacing.NORTH;
                    }

                    final Block block6 = iblockstate6.getBlock();
                    final Block block7 = iblockstate7.getBlock();

                    if ((flag || block6.isFullBlock()) && !flag1 && !block7.isFullBlock()) {
                        enumfacing = EnumFacing.SOUTH;
                    }

                    if ((flag1 || block7.isFullBlock()) && !flag && !block6.isFullBlock()) {
                        enumfacing = EnumFacing.NORTH;
                    }
                }
            } else {
                final BlockPos blockpos = block == this ? pos.north() : pos.south();
                final IBlockState iblockstate4 = worldIn.getBlockState(blockpos.west());
                final IBlockState iblockstate5 = worldIn.getBlockState(blockpos.east());
                enumfacing = EnumFacing.EAST;
                final EnumFacing enumfacing1;

                if (block == this) {
                    enumfacing1 = iblockstate.getValue(FACING);
                } else {
                    enumfacing1 = iblockstate1.getValue(FACING);
                }

                if (enumfacing1 == EnumFacing.WEST) {
                    enumfacing = EnumFacing.WEST;
                }

                final Block block4 = iblockstate4.getBlock();
                final Block block5 = iblockstate5.getBlock();

                if ((block2.isFullBlock() || block4.isFullBlock()) && !block3.isFullBlock() && !block5.isFullBlock()) {
                    enumfacing = EnumFacing.EAST;
                }

                if ((block3.isFullBlock() || block5.isFullBlock()) && !block2.isFullBlock() && !block4.isFullBlock()) {
                    enumfacing = EnumFacing.WEST;
                }
            }

            state = state.withProperty(FACING, enumfacing);
            worldIn.setBlockState(pos, state, 3);
            return state;
        }
    }

    public IBlockState correctFacing(final World worldIn, final BlockPos pos, final IBlockState state) {
        EnumFacing enumfacing = null;

        for (final EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
            final IBlockState iblockstate = worldIn.getBlockState(pos.offset(enumfacing1));

            if (iblockstate.getBlock() == this) {
                return state;
            }

            if (iblockstate.getBlock().isFullBlock()) {
                if (enumfacing != null) {
                    enumfacing = null;
                    break;
                }

                enumfacing = enumfacing1;
            }
        }

        if (enumfacing != null) {
            return state.withProperty(FACING, enumfacing.getOpposite());
        } else {
            EnumFacing enumfacing2 = state.getValue(FACING);

            if (worldIn.getBlockState(pos.offset(enumfacing2)).getBlock().isFullBlock()) {
                enumfacing2 = enumfacing2.getOpposite();
            }

            if (worldIn.getBlockState(pos.offset(enumfacing2)).getBlock().isFullBlock()) {
                enumfacing2 = enumfacing2.rotateY();
            }

            if (worldIn.getBlockState(pos.offset(enumfacing2)).getBlock().isFullBlock()) {
                enumfacing2 = enumfacing2.getOpposite();
            }

            return state.withProperty(FACING, enumfacing2);
        }
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        int i = 0;
        final BlockPos blockpos = pos.west();
        final BlockPos blockpos1 = pos.east();
        final BlockPos blockpos2 = pos.north();
        final BlockPos blockpos3 = pos.south();

        if (worldIn.getBlockState(blockpos).getBlock() == this) {
            if (this.isDoubleChest(worldIn, blockpos)) {
                return false;
            }

            ++i;
        }

        if (worldIn.getBlockState(blockpos1).getBlock() == this) {
            if (this.isDoubleChest(worldIn, blockpos1)) {
                return false;
            }

            ++i;
        }

        if (worldIn.getBlockState(blockpos2).getBlock() == this) {
            if (this.isDoubleChest(worldIn, blockpos2)) {
                return false;
            }

            ++i;
        }

        if (worldIn.getBlockState(blockpos3).getBlock() == this) {
            if (this.isDoubleChest(worldIn, blockpos3)) {
                return false;
            }

            ++i;
        }

        return i <= 1;
    }

    private boolean isDoubleChest(final World worldIn, final BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock() != this) {
            return false;
        } else {
            for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() == this) {
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
        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityChest) {
            tileentity.updateContainingBlockInfo();
        }
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            final ILockableContainer ilockablecontainer = this.getLockableContainer(worldIn, pos);

            if (ilockablecontainer != null) {
                playerIn.displayGUIChest(ilockablecontainer);

                if (this.chestType == 0) {
                    playerIn.triggerAchievement(StatList.field_181723_aa);
                } else if (this.chestType == 1) {
                    playerIn.triggerAchievement(StatList.field_181737_U);
                }
            }

            return true;
        }
    }

    public ILockableContainer getLockableContainer(final World worldIn, final BlockPos pos) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (!(tileentity instanceof TileEntityChest)) {
            return null;
        } else {
            ILockableContainer ilockablecontainer = (TileEntityChest) tileentity;

            if (this.isBlocked(worldIn, pos)) {
                return null;
            } else {
                for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                    final BlockPos blockpos = pos.offset(enumfacing);
                    final Block block = worldIn.getBlockState(blockpos).getBlock();

                    if (block == this) {
                        if (this.isBlocked(worldIn, blockpos)) {
                            return null;
                        }

                        final TileEntity tileentity1 = worldIn.getTileEntity(blockpos);

                        if (tileentity1 instanceof TileEntityChest) {
                            if (enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH) {
                                ilockablecontainer = new InventoryLargeChest("container.chestDouble", ilockablecontainer, (TileEntityChest) tileentity1);
                            } else {
                                ilockablecontainer = new InventoryLargeChest("container.chestDouble", (TileEntityChest) tileentity1, ilockablecontainer);
                            }
                        }
                    }
                }

                return ilockablecontainer;
            }
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileEntityChest();
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower() {
        return this.chestType == 1;
    }

    public int isProvidingWeakPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        if (!this.canProvidePower()) {
            return 0;
        } else {
            int i = 0;
            final TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityChest) {
                i = ((TileEntityChest) tileentity).numPlayersUsing;
            }

            return MathHelper.clamp_int(i, 0, 15);
        }
    }

    public int isProvidingStrongPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        return side == EnumFacing.UP ? this.isProvidingWeakPower(worldIn, pos, state, side) : 0;
    }

    private boolean isBlocked(final World worldIn, final BlockPos pos) {
        return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos);
    }

    private boolean isBelowSolidBlock(final World worldIn, final BlockPos pos) {
        return worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
    }

    private boolean isOcelotSittingOnChest(final World worldIn, final BlockPos pos) {
        for (final Entity entity : worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1))) {
            final EntityOcelot entityocelot = (EntityOcelot) entity;

            if (entityocelot.isSitting()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(final World worldIn, final BlockPos pos) {
        return Container.calcRedstoneFromInventory(this.getLockableContainer(worldIn, pos));
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING);
    }
}
