/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoor
extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyEnum<EnumHingePosition> HINGE = PropertyEnum.create("hinge", EnumHingePosition.class);
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyEnum<EnumDoorHalf> HALF = PropertyEnum.create("half", EnumDoorHalf.class);

    protected BlockDoor(Material materialIn) {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, false).withProperty(HINGE, EnumHingePosition.LEFT).withProperty(POWERED, false).withProperty(HALF, EnumDoorHalf.LOWER));
    }

    @Override
    public String getLocalizedName() {
        return StatCollector.translateToLocal((this.getUnlocalizedName() + ".name").replaceAll("tile", "item"));
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return BlockDoor.isOpen(BlockDoor.combineMetadata(worldIn, pos));
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getSelectedBoundingBox(worldIn, pos);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        this.setBoundBasedOnMeta(BlockDoor.combineMetadata(worldIn, pos));
    }

    private void setBoundBasedOnMeta(int combinedMeta) {
        float f = 0.1875f;
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f);
        EnumFacing enumfacing = BlockDoor.getFacing(combinedMeta);
        boolean flag = BlockDoor.isOpen(combinedMeta);
        boolean flag1 = BlockDoor.isHingeLeft(combinedMeta);
        if (flag) {
            if (enumfacing == EnumFacing.EAST) {
                if (!flag1) {
                    this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, f);
                } else {
                    this.setBlockBounds(0.0f, 0.0f, 1.0f - f, 1.0f, 1.0f, 1.0f);
                }
            } else if (enumfacing == EnumFacing.SOUTH) {
                if (!flag1) {
                    this.setBlockBounds(1.0f - f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                } else {
                    this.setBlockBounds(0.0f, 0.0f, 0.0f, f, 1.0f, 1.0f);
                }
            } else if (enumfacing == EnumFacing.WEST) {
                if (!flag1) {
                    this.setBlockBounds(0.0f, 0.0f, 1.0f - f, 1.0f, 1.0f, 1.0f);
                } else {
                    this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, f);
                }
            } else if (enumfacing == EnumFacing.NORTH) {
                if (!flag1) {
                    this.setBlockBounds(0.0f, 0.0f, 0.0f, f, 1.0f, 1.0f);
                } else {
                    this.setBlockBounds(1.0f - f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        } else if (enumfacing == EnumFacing.EAST) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, f, 1.0f, 1.0f);
        } else if (enumfacing == EnumFacing.SOUTH) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, f);
        } else if (enumfacing == EnumFacing.WEST) {
            this.setBlockBounds(1.0f - f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        } else if (enumfacing == EnumFacing.NORTH) {
            this.setBlockBounds(0.0f, 0.0f, 1.0f - f, 1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate;
        if (this.blockMaterial == Material.iron) {
            return true;
        }
        BlockPos blockpos = state.getValue(HALF) == EnumDoorHalf.LOWER ? pos : pos.down();
        IBlockState iBlockState = iblockstate = pos.equals(blockpos) ? state : worldIn.getBlockState(blockpos);
        if (iblockstate.getBlock() != this) {
            return false;
        }
        state = iblockstate.cycleProperty(OPEN);
        worldIn.setBlockState(blockpos, state, 2);
        worldIn.markBlockRangeForRenderUpdate(blockpos, pos);
        worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN) != false ? 1003 : 1006, pos, 0);
        return true;
    }

    public void toggleDoor(World worldIn, BlockPos pos, boolean open) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        if (iblockstate.getBlock() == this) {
            IBlockState iblockstate1;
            BlockPos blockpos = iblockstate.getValue(HALF) == EnumDoorHalf.LOWER ? pos : pos.down();
            IBlockState iBlockState = iblockstate1 = pos == blockpos ? iblockstate : worldIn.getBlockState(blockpos);
            if (iblockstate1.getBlock() == this && iblockstate1.getValue(OPEN) != open) {
                worldIn.setBlockState(blockpos, iblockstate1.withProperty(OPEN, open), 2);
                worldIn.markBlockRangeForRenderUpdate(blockpos, pos);
                worldIn.playAuxSFXAtEntity(null, open ? 1003 : 1006, pos, 0);
            }
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (state.getValue(HALF) == EnumDoorHalf.UPPER) {
            BlockPos blockpos = pos.down();
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            if (iblockstate.getBlock() != this) {
                worldIn.setBlockToAir(pos);
            } else if (neighborBlock != this) {
                this.onNeighborBlockChange(worldIn, blockpos, iblockstate, neighborBlock);
            }
        } else {
            boolean flag1 = false;
            BlockPos blockpos1 = pos.up();
            IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);
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
                boolean flag;
                boolean bl = flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockpos1);
                if ((flag || neighborBlock.canProvidePower()) && neighborBlock != this && flag != iblockstate1.getValue(POWERED)) {
                    worldIn.setBlockState(blockpos1, iblockstate1.withProperty(POWERED, flag), 2);
                    if (flag != state.getValue(OPEN)) {
                        worldIn.setBlockState(pos, state.withProperty(OPEN, flag), 2);
                        worldIn.markBlockRangeForRenderUpdate(pos, pos);
                        worldIn.playAuxSFXAtEntity(null, flag ? 1003 : 1006, pos, 0);
                    }
                }
            }
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == EnumDoorHalf.UPPER ? null : this.getItem();
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.collisionRayTrace(worldIn, pos, start, end);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return pos.getY() >= 255 ? false : World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
    }

    @Override
    public int getMobilityFlag() {
        return 1;
    }

    public static int combineMetadata(IBlockAccess worldIn, BlockPos pos) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        int i = iblockstate.getBlock().getMetaFromState(iblockstate);
        boolean flag = BlockDoor.isTop(i);
        IBlockState iblockstate1 = worldIn.getBlockState(pos.down());
        int j = iblockstate1.getBlock().getMetaFromState(iblockstate1);
        int k = flag ? j : i;
        IBlockState iblockstate2 = worldIn.getBlockState(pos.up());
        int l = iblockstate2.getBlock().getMetaFromState(iblockstate2);
        int i1 = flag ? i : l;
        boolean flag1 = (i1 & 1) != 0;
        boolean flag2 = (i1 & 2) != 0;
        return BlockDoor.removeHalfBit(k) | (flag ? 8 : 0) | (flag1 ? 16 : 0) | (flag2 ? 32 : 0);
    }

    @Override
    public Item getItem(World worldIn, BlockPos pos) {
        return this.getItem();
    }

    private Item getItem() {
        return this == Blocks.iron_door ? Items.iron_door : (this == Blocks.spruce_door ? Items.spruce_door : (this == Blocks.birch_door ? Items.birch_door : (this == Blocks.jungle_door ? Items.jungle_door : (this == Blocks.acacia_door ? Items.acacia_door : (this == Blocks.dark_oak_door ? Items.dark_oak_door : Items.oak_door)))));
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        BlockPos blockpos = pos.down();
        if (player.capabilities.isCreativeMode && state.getValue(HALF) == EnumDoorHalf.UPPER && worldIn.getBlockState(blockpos).getBlock() == this) {
            worldIn.setBlockToAir(blockpos);
        }
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (state.getValue(HALF) == EnumDoorHalf.LOWER) {
            IBlockState iblockstate = worldIn.getBlockState(pos.up());
            if (iblockstate.getBlock() == this) {
                state = state.withProperty(HINGE, iblockstate.getValue(HINGE)).withProperty(POWERED, iblockstate.getValue(POWERED));
            }
        } else {
            IBlockState iblockstate1 = worldIn.getBlockState(pos.down());
            if (iblockstate1.getBlock() == this) {
                state = state.withProperty(FACING, iblockstate1.getValue(FACING)).withProperty(OPEN, iblockstate1.getValue(OPEN));
            }
        }
        return state;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(HALF, EnumDoorHalf.UPPER).withProperty(HINGE, (meta & 1) > 0 ? EnumHingePosition.RIGHT : EnumHingePosition.LEFT).withProperty(POWERED, (meta & 2) > 0) : this.getDefaultState().withProperty(HALF, EnumDoorHalf.LOWER).withProperty(FACING, EnumFacing.getHorizontal(meta & 3).rotateYCCW()).withProperty(OPEN, (meta & 4) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        if (state.getValue(HALF) == EnumDoorHalf.UPPER) {
            i |= 8;
            if (state.getValue(HINGE) == EnumHingePosition.RIGHT) {
                i |= 1;
            }
            if (state.getValue(POWERED).booleanValue()) {
                i |= 2;
            }
        } else {
            i |= state.getValue(FACING).rotateY().getHorizontalIndex();
            if (state.getValue(OPEN).booleanValue()) {
                i |= 4;
            }
        }
        return i;
    }

    protected static int removeHalfBit(int meta) {
        return meta & 7;
    }

    public static boolean isOpen(IBlockAccess worldIn, BlockPos pos) {
        return BlockDoor.isOpen(BlockDoor.combineMetadata(worldIn, pos));
    }

    public static EnumFacing getFacing(IBlockAccess worldIn, BlockPos pos) {
        return BlockDoor.getFacing(BlockDoor.combineMetadata(worldIn, pos));
    }

    public static EnumFacing getFacing(int combinedMeta) {
        return EnumFacing.getHorizontal(combinedMeta & 3).rotateYCCW();
    }

    protected static boolean isOpen(int combinedMeta) {
        return (combinedMeta & 4) != 0;
    }

    protected static boolean isTop(int meta) {
        return (meta & 8) != 0;
    }

    protected static boolean isHingeLeft(int combinedMeta) {
        return (combinedMeta & 0x10) != 0;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, HALF, FACING, OPEN, HINGE, POWERED);
    }

    public static enum EnumHingePosition implements IStringSerializable
    {
        LEFT,
        RIGHT;


        public String toString() {
            return this.getName();
        }

        @Override
        public String getName() {
            return this == LEFT ? "left" : "right";
        }
    }

    public static enum EnumDoorHalf implements IStringSerializable
    {
        UPPER,
        LOWER;


        public String toString() {
            return this.getName();
        }

        @Override
        public String getName() {
            return this == UPPER ? "upper" : "lower";
        }
    }
}

