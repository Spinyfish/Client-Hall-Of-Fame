package net.minecraft.block;

import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.block.state.*;
import com.google.common.base.*;
import net.minecraft.util.*;

public class BlockDoor extends Block
{
    public static final PropertyDirection FACING;
    public static final PropertyBool OPEN;
    public static final PropertyEnum<EnumHingePosition> HINGE;
    public static final PropertyBool POWERED;
    public static final PropertyEnum<EnumDoorHalf> HALF;
    
    protected BlockDoor(final Material materialIn) {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty<Comparable>)BlockDoor.FACING, EnumFacing.NORTH).withProperty((IProperty<Comparable>)BlockDoor.OPEN, false).withProperty(BlockDoor.HINGE, EnumHingePosition.LEFT).withProperty((IProperty<Comparable>)BlockDoor.POWERED, false).withProperty(BlockDoor.HALF, EnumDoorHalf.LOWER));
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
    public boolean isPassable(final IBlockAccess worldIn, final BlockPos pos) {
        return isOpen(combineMetadata(worldIn, pos));
    }
    
    @Override
    public boolean isFullCube() {
        return false;
    }
    
    @Override
    public AxisAlignedBB getSelectedBoundingBox(final World worldIn, final BlockPos pos) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getSelectedBoundingBox(worldIn, pos);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }
    
    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        this.setBoundBasedOnMeta(combineMetadata(worldIn, pos));
    }
    
    private void setBoundBasedOnMeta(final int combinedMeta) {
        final float f = 0.1875f;
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f);
        final EnumFacing enumfacing = getFacing(combinedMeta);
        final boolean flag = isOpen(combinedMeta);
        final boolean flag2 = isHingeLeft(combinedMeta);
        if (flag) {
            if (enumfacing == EnumFacing.EAST) {
                if (!flag2) {
                    this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, f);
                }
                else {
                    this.setBlockBounds(0.0f, 0.0f, 1.0f - f, 1.0f, 1.0f, 1.0f);
                }
            }
            else if (enumfacing == EnumFacing.SOUTH) {
                if (!flag2) {
                    this.setBlockBounds(1.0f - f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                }
                else {
                    this.setBlockBounds(0.0f, 0.0f, 0.0f, f, 1.0f, 1.0f);
                }
            }
            else if (enumfacing == EnumFacing.WEST) {
                if (!flag2) {
                    this.setBlockBounds(0.0f, 0.0f, 1.0f - f, 1.0f, 1.0f, 1.0f);
                }
                else {
                    this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, f);
                }
            }
            else if (enumfacing == EnumFacing.NORTH) {
                if (!flag2) {
                    this.setBlockBounds(0.0f, 0.0f, 0.0f, f, 1.0f, 1.0f);
                }
                else {
                    this.setBlockBounds(1.0f - f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        else if (enumfacing == EnumFacing.EAST) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, f, 1.0f, 1.0f);
        }
        else if (enumfacing == EnumFacing.SOUTH) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, f);
        }
        else if (enumfacing == EnumFacing.WEST) {
            this.setBlockBounds(1.0f - f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
        else if (enumfacing == EnumFacing.NORTH) {
            this.setBlockBounds(0.0f, 0.0f, 1.0f - f, 1.0f, 1.0f, 1.0f);
        }
    }
    
    @Override
    public boolean onBlockActivated(final World worldIn, final BlockPos pos, IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (this.blockMaterial == Material.iron) {
            return true;
        }
        final BlockPos blockpos = (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) ? pos : pos.down();
        final IBlockState iblockstate = pos.equals(blockpos) ? state : worldIn.getBlockState(blockpos);
        if (iblockstate.getBlock() != this) {
            return false;
        }
        state = iblockstate.cycleProperty((IProperty<Comparable>)BlockDoor.OPEN);
        worldIn.setBlockState(blockpos, state, 2);
        worldIn.markBlockRangeForRenderUpdate(blockpos, pos);
        worldIn.playAuxSFXAtEntity(playerIn, ((boolean)state.getValue((IProperty<Boolean>)BlockDoor.OPEN)) ? 1003 : 1006, pos, 0);
        return true;
    }
    
    public void toggleDoor(final World worldIn, final BlockPos pos, final boolean open) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        if (iblockstate.getBlock() == this) {
            final BlockPos blockpos = (iblockstate.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) ? pos : pos.down();
            final IBlockState iblockstate2 = (pos == blockpos) ? iblockstate : worldIn.getBlockState(blockpos);
            if (iblockstate2.getBlock() == this && iblockstate2.getValue((IProperty<Boolean>)BlockDoor.OPEN) != open) {
                worldIn.setBlockState(blockpos, iblockstate2.withProperty((IProperty<Comparable>)BlockDoor.OPEN, open), 2);
                worldIn.markBlockRangeForRenderUpdate(blockpos, pos);
                worldIn.playAuxSFXAtEntity(null, open ? 1003 : 1006, pos, 0);
            }
        }
    }
    
    @Override
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
            final BlockPos blockpos = pos.down();
            final IBlockState iblockstate = worldIn.getBlockState(blockpos);
            if (iblockstate.getBlock() != this) {
                worldIn.setBlockToAir(pos);
            }
            else if (neighborBlock != this) {
                this.onNeighborBlockChange(worldIn, blockpos, iblockstate, neighborBlock);
            }
        }
        else {
            boolean flag1 = false;
            final BlockPos blockpos2 = pos.up();
            final IBlockState iblockstate2 = worldIn.getBlockState(blockpos2);
            if (iblockstate2.getBlock() != this) {
                worldIn.setBlockToAir(pos);
                flag1 = true;
            }
            if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
                worldIn.setBlockToAir(pos);
                flag1 = true;
                if (iblockstate2.getBlock() == this) {
                    worldIn.setBlockToAir(blockpos2);
                }
            }
            if (flag1) {
                if (!worldIn.isRemote) {
                    this.dropBlockAsItem(worldIn, pos, state, 0);
                }
            }
            else {
                final boolean flag2 = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockpos2);
                if ((flag2 || neighborBlock.canProvidePower()) && neighborBlock != this && flag2 != iblockstate2.getValue((IProperty<Boolean>)BlockDoor.POWERED)) {
                    worldIn.setBlockState(blockpos2, iblockstate2.withProperty((IProperty<Comparable>)BlockDoor.POWERED, flag2), 2);
                    if (flag2 != state.getValue((IProperty<Boolean>)BlockDoor.OPEN)) {
                        worldIn.setBlockState(pos, state.withProperty((IProperty<Comparable>)BlockDoor.OPEN, flag2), 2);
                        worldIn.markBlockRangeForRenderUpdate(pos, pos);
                        worldIn.playAuxSFXAtEntity(null, flag2 ? 1003 : 1006, pos, 0);
                    }
                }
            }
        }
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return (state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) ? null : this.getItem();
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace(final World worldIn, final BlockPos pos, final Vec3 start, final Vec3 end) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.collisionRayTrace(worldIn, pos, start, end);
    }
    
    @Override
    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return pos.getY() < 255 && (World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up()));
    }
    
    @Override
    public int getMobilityFlag() {
        return 1;
    }
    
    public static int combineMetadata(final IBlockAccess worldIn, final BlockPos pos) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        final int i = iblockstate.getBlock().getMetaFromState(iblockstate);
        final boolean flag = isTop(i);
        final IBlockState iblockstate2 = worldIn.getBlockState(pos.down());
        final int j = iblockstate2.getBlock().getMetaFromState(iblockstate2);
        final int k = flag ? j : i;
        final IBlockState iblockstate3 = worldIn.getBlockState(pos.up());
        final int l = iblockstate3.getBlock().getMetaFromState(iblockstate3);
        final int i2 = flag ? i : l;
        final boolean flag2 = (i2 & 0x1) != 0x0;
        final boolean flag3 = (i2 & 0x2) != 0x0;
        return removeHalfBit(k) | (flag ? 8 : 0) | (flag2 ? 16 : 0) | (flag3 ? 32 : 0);
    }
    
    @Override
    public Item getItem(final World worldIn, final BlockPos pos) {
        return this.getItem();
    }
    
    private Item getItem() {
        return (this == Blocks.iron_door) ? Items.iron_door : ((this == Blocks.spruce_door) ? Items.spruce_door : ((this == Blocks.birch_door) ? Items.birch_door : ((this == Blocks.jungle_door) ? Items.jungle_door : ((this == Blocks.acacia_door) ? Items.acacia_door : ((this == Blocks.dark_oak_door) ? Items.dark_oak_door : Items.oak_door)))));
    }
    
    @Override
    public void onBlockHarvested(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer player) {
        final BlockPos blockpos = pos.down();
        if (player.capabilities.isCreativeMode && state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER && worldIn.getBlockState(blockpos).getBlock() == this) {
            worldIn.setBlockToAir(blockpos);
        }
    }
    
    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }
    
    @Override
    public IBlockState getActualState(IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) {
            final IBlockState iblockstate = worldIn.getBlockState(pos.up());
            if (iblockstate.getBlock() == this) {
                state = state.withProperty(BlockDoor.HINGE, (Comparable)iblockstate.getValue((IProperty<V>)BlockDoor.HINGE)).withProperty((IProperty<Comparable>)BlockDoor.POWERED, (Comparable)iblockstate.getValue((IProperty<V>)BlockDoor.POWERED));
            }
        }
        else {
            final IBlockState iblockstate2 = worldIn.getBlockState(pos.down());
            if (iblockstate2.getBlock() == this) {
                state = state.withProperty((IProperty<Comparable>)BlockDoor.FACING, (Comparable)iblockstate2.getValue((IProperty<V>)BlockDoor.FACING)).withProperty((IProperty<Comparable>)BlockDoor.OPEN, (Comparable)iblockstate2.getValue((IProperty<V>)BlockDoor.OPEN));
            }
        }
        return state;
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return ((meta & 0x8) > 0) ? this.getDefaultState().withProperty(BlockDoor.HALF, EnumDoorHalf.UPPER).withProperty(BlockDoor.HINGE, ((meta & 0x1) > 0) ? EnumHingePosition.RIGHT : EnumHingePosition.LEFT).withProperty((IProperty<Comparable>)BlockDoor.POWERED, (meta & 0x2) > 0) : this.getDefaultState().withProperty(BlockDoor.HALF, EnumDoorHalf.LOWER).withProperty((IProperty<Comparable>)BlockDoor.FACING, EnumFacing.getHorizontal(meta & 0x3).rotateYCCW()).withProperty((IProperty<Comparable>)BlockDoor.OPEN, (meta & 0x4) > 0);
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
            i |= 0x8;
            if (state.getValue(BlockDoor.HINGE) == EnumHingePosition.RIGHT) {
                i |= 0x1;
            }
            if (state.getValue((IProperty<Boolean>)BlockDoor.POWERED)) {
                i |= 0x2;
            }
        }
        else {
            i |= state.getValue((IProperty<EnumFacing>)BlockDoor.FACING).rotateY().getHorizontalIndex();
            if (state.getValue((IProperty<Boolean>)BlockDoor.OPEN)) {
                i |= 0x4;
            }
        }
        return i;
    }
    
    protected static int removeHalfBit(final int meta) {
        return meta & 0x7;
    }
    
    public static boolean isOpen(final IBlockAccess worldIn, final BlockPos pos) {
        return isOpen(combineMetadata(worldIn, pos));
    }
    
    public static EnumFacing getFacing(final IBlockAccess worldIn, final BlockPos pos) {
        return getFacing(combineMetadata(worldIn, pos));
    }
    
    public static EnumFacing getFacing(final int combinedMeta) {
        return EnumFacing.getHorizontal(combinedMeta & 0x3).rotateYCCW();
    }
    
    protected static boolean isOpen(final int combinedMeta) {
        return (combinedMeta & 0x4) != 0x0;
    }
    
    protected static boolean isTop(final int meta) {
        return (meta & 0x8) != 0x0;
    }
    
    protected static boolean isHingeLeft(final int combinedMeta) {
        return (combinedMeta & 0x10) != 0x0;
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockDoor.HALF, BlockDoor.FACING, BlockDoor.OPEN, BlockDoor.HINGE, BlockDoor.POWERED });
    }
    
    static {
        FACING = PropertyDirection.create("facing", (Predicate<EnumFacing>)EnumFacing.Plane.HORIZONTAL);
        OPEN = PropertyBool.create("open");
        HINGE = PropertyEnum.create("hinge", EnumHingePosition.class);
        POWERED = PropertyBool.create("powered");
        HALF = PropertyEnum.create("half", EnumDoorHalf.class);
    }
    
    public enum EnumDoorHalf implements IStringSerializable
    {
        UPPER, 
        LOWER;
        
        @Override
        public String toString() {
            return this.getName();
        }
        
        @Override
        public String getName() {
            return (this == EnumDoorHalf.UPPER) ? "upper" : "lower";
        }
    }
    
    public enum EnumHingePosition implements IStringSerializable
    {
        LEFT, 
        RIGHT;
        
        @Override
        public String toString() {
            return this.getName();
        }
        
        @Override
        public String getName() {
            return (this == EnumHingePosition.LEFT) ? "left" : "right";
        }
    }
}
