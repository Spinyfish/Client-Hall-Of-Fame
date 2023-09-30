package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDoor extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyEnum<BlockDoor.EnumHingePosition> HINGE = PropertyEnum.create("hinge", BlockDoor.EnumHingePosition.class);
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyEnum<BlockDoor.EnumDoorHalf> HALF = PropertyEnum.create("half", BlockDoor.EnumDoorHalf.class);

    protected BlockDoor(final Material materialIn) {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, Boolean.valueOf(false)).withProperty(HINGE, BlockDoor.EnumHingePosition.LEFT).withProperty(POWERED, Boolean.valueOf(false)).withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER));
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName() {
        return StatCollector.translateToLocal((this.getUnlocalizedName() + ".name").replaceAll("tile", "item"));
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isPassable(final IBlockAccess worldIn, final BlockPos pos) {
        return isOpen(combineMetadata(worldIn, pos));
    }

    public boolean isFullCube() {
        return false;
    }

    public AxisAlignedBB getSelectedBoundingBox(final World worldIn, final BlockPos pos) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getSelectedBoundingBox(worldIn, pos);
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        this.setBoundBasedOnMeta(combineMetadata(worldIn, pos));
    }

    private void setBoundBasedOnMeta(final int combinedMeta) {
        final float f = 0.1875F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
        final EnumFacing enumfacing = getFacing(combinedMeta);
        final boolean flag = isOpen(combinedMeta);
        final boolean flag1 = isHingeLeft(combinedMeta);

        if (flag) {
            if (enumfacing == EnumFacing.EAST) {
                if (!flag1) {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                } else {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
            } else if (enumfacing == EnumFacing.SOUTH) {
                if (!flag1) {
                    this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
            } else if (enumfacing == EnumFacing.WEST) {
                if (!flag1) {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                } else {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
            } else if (enumfacing == EnumFacing.NORTH) {
                if (!flag1) {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                } else {
                    this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        } else if (enumfacing == EnumFacing.EAST) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        } else if (enumfacing == EnumFacing.SOUTH) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        } else if (enumfacing == EnumFacing.WEST) {
            this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else if (enumfacing == EnumFacing.NORTH) {
            this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (this.blockMaterial == Material.iron) {
            return true;
        } else {
            final BlockPos blockpos = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
            final IBlockState iblockstate = pos.equals(blockpos) ? state : worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() != this) {
                return false;
            } else {
                state = iblockstate.cycleProperty(OPEN);
                worldIn.setBlockState(blockpos, state, 2);
                worldIn.markBlockRangeForRenderUpdate(blockpos, pos);
                worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN).booleanValue() ? 1003 : 1006, pos, 0);
                return true;
            }
        }
    }

    public void toggleDoor(final World worldIn, final BlockPos pos, final boolean open) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == this) {
            final BlockPos blockpos = iblockstate.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
            final IBlockState iblockstate1 = pos == blockpos ? iblockstate : worldIn.getBlockState(blockpos);

            if (iblockstate1.getBlock() == this && iblockstate1.getValue(OPEN).booleanValue() != open) {
                worldIn.setBlockState(blockpos, iblockstate1.withProperty(OPEN, Boolean.valueOf(open)), 2);
                worldIn.markBlockRangeForRenderUpdate(blockpos, pos);
                worldIn.playAuxSFXAtEntity(null, open ? 1003 : 1006, pos, 0);
            }
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            final BlockPos blockpos = pos.down();
            final IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() != this) {
                worldIn.setBlockToAir(pos);
            } else if (neighborBlock != this) {
                this.onNeighborBlockChange(worldIn, blockpos, iblockstate, neighborBlock);
            }
        } else {
            boolean flag1 = false;
            final BlockPos blockpos1 = pos.up();
            final IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

            if (iblockstate1.getBlock() != this) {
                worldIn.setBlockToAir(pos);
                flag1 = true;
            }

            if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
                worldIn.setBlockToAir(pos);
                flag1 = true;

                if (iblockstate1.getBlock() == this) {
                    worldIn.setBlockToAir(blockpos1);
                }
            }

            if (flag1) {
                if (!worldIn.isRemote) {
                    this.dropBlockAsItem(worldIn, pos, state, 0);
                }
            } else {
                final boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockpos1);

                if ((flag || neighborBlock.canProvidePower()) && neighborBlock != this && flag != iblockstate1.getValue(POWERED).booleanValue()) {
                    worldIn.setBlockState(blockpos1, iblockstate1.withProperty(POWERED, Boolean.valueOf(flag)), 2);

                    if (flag != state.getValue(OPEN).booleanValue()) {
                        worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(flag)), 2);
                        worldIn.markBlockRangeForRenderUpdate(pos, pos);
                        worldIn.playAuxSFXAtEntity(null, flag ? 1003 : 1006, pos, 0);
                    }
                }
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : this.getItem();
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     *
     * @param start The start vector
     * @param end   The end vector
     */
    public MovingObjectPosition collisionRayTrace(final World worldIn, final BlockPos pos, final Vec3 start, final Vec3 end) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.collisionRayTrace(worldIn, pos, start, end);
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return pos.getY() < 255 && World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
    }

    public int getMobilityFlag() {
        return 1;
    }

    public static int combineMetadata(final IBlockAccess worldIn, final BlockPos pos) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        final int i = iblockstate.getBlock().getMetaFromState(iblockstate);
        final boolean flag = isTop(i);
        final IBlockState iblockstate1 = worldIn.getBlockState(pos.down());
        final int j = iblockstate1.getBlock().getMetaFromState(iblockstate1);
        final int k = flag ? j : i;
        final IBlockState iblockstate2 = worldIn.getBlockState(pos.up());
        final int l = iblockstate2.getBlock().getMetaFromState(iblockstate2);
        final int i1 = flag ? i : l;
        final boolean flag1 = (i1 & 1) != 0;
        final boolean flag2 = (i1 & 2) != 0;
        return removeHalfBit(k) | (flag ? 8 : 0) | (flag1 ? 16 : 0) | (flag2 ? 32 : 0);
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return this.getItem();
    }

    private Item getItem() {
        return this == Blocks.iron_door ? Items.iron_door : (this == Blocks.spruce_door ? Items.spruce_door : (this == Blocks.birch_door ? Items.birch_door : (this == Blocks.jungle_door ? Items.jungle_door : (this == Blocks.acacia_door ? Items.acacia_door : (this == Blocks.dark_oak_door ? Items.dark_oak_door : Items.oak_door)))));
    }

    public void onBlockHarvested(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer player) {
        final BlockPos blockpos = pos.down();

        if (player.capabilities.isCreativeMode && state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER && worldIn.getBlockState(blockpos).getBlock() == this) {
            worldIn.setBlockToAir(blockpos);
        }
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER) {
            final IBlockState iblockstate = worldIn.getBlockState(pos.up());

            if (iblockstate.getBlock() == this) {
                state = state.withProperty(HINGE, iblockstate.getValue(HINGE)).withProperty(POWERED, iblockstate.getValue(POWERED));
            }
        } else {
            final IBlockState iblockstate1 = worldIn.getBlockState(pos.down());

            if (iblockstate1.getBlock() == this) {
                state = state.withProperty(FACING, iblockstate1.getValue(FACING)).withProperty(OPEN, iblockstate1.getValue(OPEN));
            }
        }

        return state;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(HALF, BlockDoor.EnumDoorHalf.UPPER).withProperty(HINGE, (meta & 1) > 0 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT).withProperty(POWERED, Boolean.valueOf((meta & 2) > 0)) : this.getDefaultState().withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER).withProperty(FACING, EnumFacing.getHorizontal(meta & 3).rotateYCCW()).withProperty(OPEN, Boolean.valueOf((meta & 4) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;

        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            i = i | 8;

            if (state.getValue(HINGE) == BlockDoor.EnumHingePosition.RIGHT) {
                i |= 1;
            }

            if (state.getValue(POWERED).booleanValue()) {
                i |= 2;
            }
        } else {
            i = i | state.getValue(FACING).rotateY().getHorizontalIndex();

            if (state.getValue(OPEN).booleanValue()) {
                i |= 4;
            }
        }

        return i;
    }

    protected static int removeHalfBit(final int meta) {
        return meta & 7;
    }

    public static boolean isOpen(final IBlockAccess worldIn, final BlockPos pos) {
        return isOpen(combineMetadata(worldIn, pos));
    }

    public static EnumFacing getFacing(final IBlockAccess worldIn, final BlockPos pos) {
        return getFacing(combineMetadata(worldIn, pos));
    }

    public static EnumFacing getFacing(final int combinedMeta) {
        return EnumFacing.getHorizontal(combinedMeta & 3).rotateYCCW();
    }

    protected static boolean isOpen(final int combinedMeta) {
        return (combinedMeta & 4) != 0;
    }

    protected static boolean isTop(final int meta) {
        return (meta & 8) != 0;
    }

    protected static boolean isHingeLeft(final int combinedMeta) {
        return (combinedMeta & 16) != 0;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, HALF, FACING, OPEN, HINGE, POWERED);
    }

    public enum EnumDoorHalf implements IStringSerializable {
        UPPER,
        LOWER;

        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this == UPPER ? "upper" : "lower";
        }
    }

    public enum EnumHingePosition implements IStringSerializable {
        LEFT,
        RIGHT;

        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this == LEFT ? "left" : "right";
        }
    }
}
