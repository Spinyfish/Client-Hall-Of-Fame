package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyEnum<BlockRedstoneComparator.Mode> MODE = PropertyEnum.create("mode", BlockRedstoneComparator.Mode.class);

    public BlockRedstoneComparator(final boolean powered) {
        super(powered);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, Boolean.valueOf(false)).withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE));
        this.isBlockContainer = true;
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName() {
        return StatCollector.translateToLocal("item.comparator.name");
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Items.comparator;
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Items.comparator;
    }

    protected int getDelay(final IBlockState state) {
        return 2;
    }

    protected IBlockState getPoweredState(final IBlockState unpoweredState) {
        final Boolean obool = unpoweredState.getValue(POWERED);
        final BlockRedstoneComparator.Mode blockredstonecomparator$mode = unpoweredState.getValue(MODE);
        final EnumFacing enumfacing = unpoweredState.getValue(FACING);
        return Blocks.powered_comparator.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, obool).withProperty(MODE, blockredstonecomparator$mode);
    }

    protected IBlockState getUnpoweredState(final IBlockState poweredState) {
        final Boolean obool = poweredState.getValue(POWERED);
        final BlockRedstoneComparator.Mode blockredstonecomparator$mode = poweredState.getValue(MODE);
        final EnumFacing enumfacing = poweredState.getValue(FACING);
        return Blocks.unpowered_comparator.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, obool).withProperty(MODE, blockredstonecomparator$mode);
    }

    protected boolean isPowered(final IBlockState state) {
        return this.isRepeaterPowered || state.getValue(POWERED).booleanValue();
    }

    protected int getActiveSignal(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityComparator ? ((TileEntityComparator) tileentity).getOutputSignal() : 0;
    }

    private int calculateOutput(final World worldIn, final BlockPos pos, final IBlockState state) {
        return state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT ? Math.max(this.calculateInputStrength(worldIn, pos, state) - this.getPowerOnSides(worldIn, pos, state), 0) : this.calculateInputStrength(worldIn, pos, state);
    }

    protected boolean shouldBePowered(final World worldIn, final BlockPos pos, final IBlockState state) {
        final int i = this.calculateInputStrength(worldIn, pos, state);

        if (i >= 15) {
            return true;
        } else if (i == 0) {
            return false;
        } else {
            final int j = this.getPowerOnSides(worldIn, pos, state);
            return j == 0 || i >= j;
        }
    }

    protected int calculateInputStrength(final World worldIn, final BlockPos pos, final IBlockState state) {
        int i = super.calculateInputStrength(worldIn, pos, state);
        final EnumFacing enumfacing = state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing);
        Block block = worldIn.getBlockState(blockpos).getBlock();

        if (block.hasComparatorInputOverride()) {
            i = block.getComparatorInputOverride(worldIn, blockpos);
        } else if (i < 15 && block.isNormalCube()) {
            blockpos = blockpos.offset(enumfacing);
            block = worldIn.getBlockState(blockpos).getBlock();

            if (block.hasComparatorInputOverride()) {
                i = block.getComparatorInputOverride(worldIn, blockpos);
            } else if (block.getMaterial() == Material.air) {
                final EntityItemFrame entityitemframe = this.findItemFrame(worldIn, enumfacing, blockpos);

                if (entityitemframe != null) {
                    i = entityitemframe.func_174866_q();
                }
            }
        }

        return i;
    }

    private EntityItemFrame findItemFrame(final World worldIn, final EnumFacing facing, final BlockPos pos) {
        final List<EntityItemFrame> list = worldIn.getEntitiesWithinAABB(EntityItemFrame.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), new Predicate<Entity>() {
            public boolean apply(final Entity p_apply_1_) {
                return p_apply_1_ != null && p_apply_1_.getHorizontalFacing() == facing;
            }
        });
        return list.size() == 1 ? list.get(0) : null;
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (!playerIn.capabilities.allowEdit) {
            return false;
        } else {
            state = state.cycleProperty(MODE);
            worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, "random.click", 0.3F, state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT ? 0.55F : 0.5F);
            worldIn.setBlockState(pos, state, 2);
            this.onStateChange(worldIn, pos, state);
            return true;
        }
    }

    protected void updateState(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (!worldIn.isBlockTickPending(pos, this)) {
            final int i = this.calculateOutput(worldIn, pos, state);
            final TileEntity tileentity = worldIn.getTileEntity(pos);
            final int j = tileentity instanceof TileEntityComparator ? ((TileEntityComparator) tileentity).getOutputSignal() : 0;

            if (i != j || this.isPowered(state) != this.shouldBePowered(worldIn, pos, state)) {
                if (this.isFacingTowardsRepeater(worldIn, pos, state)) {
                    worldIn.updateBlockTick(pos, this, 2, -1);
                } else {
                    worldIn.updateBlockTick(pos, this, 2, 0);
                }
            }
        }
    }

    private void onStateChange(final World worldIn, final BlockPos pos, final IBlockState state) {
        final int i = this.calculateOutput(worldIn, pos, state);
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        int j = 0;

        if (tileentity instanceof TileEntityComparator) {
            final TileEntityComparator tileentitycomparator = (TileEntityComparator) tileentity;
            j = tileentitycomparator.getOutputSignal();
            tileentitycomparator.setOutputSignal(i);
        }

        if (j != i || state.getValue(MODE) == BlockRedstoneComparator.Mode.COMPARE) {
            final boolean flag1 = this.shouldBePowered(worldIn, pos, state);
            final boolean flag = this.isPowered(state);

            if (flag && !flag1) {
                worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 2);
            } else if (!flag && flag1) {
                worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 2);
            }

            this.notifyNeighbors(worldIn, pos, state);
        }
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (this.isRepeaterPowered) {
            worldIn.setBlockState(pos, this.getUnpoweredState(state).withProperty(POWERED, Boolean.valueOf(true)), 4);
        }

        this.onStateChange(worldIn, pos, state);
    }

    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        worldIn.setTileEntity(pos, this.createNewTileEntity(worldIn, 0));
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
        this.notifyNeighbors(worldIn, pos, state);
    }

    /**
     * Called on both Client and Server when World#addBlockEvent is called
     */
    public boolean onBlockEventReceived(final World worldIn, final BlockPos pos, final IBlockState state, final int eventID, final int eventParam) {
        super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(eventID, eventParam);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileEntityComparator();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0)).withProperty(MODE, (meta & 4) > 0 ? BlockRedstoneComparator.Mode.SUBTRACT : BlockRedstoneComparator.Mode.COMPARE);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(POWERED).booleanValue()) {
            i |= 8;
        }

        if (state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT) {
            i |= 4;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, MODE, POWERED);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, Boolean.valueOf(false)).withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE);
    }

    public enum Mode implements IStringSerializable {
        COMPARE("compare"),
        SUBTRACT("subtract");

        private final String name;

        Mode(final String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }
}
