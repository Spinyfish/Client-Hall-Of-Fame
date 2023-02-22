package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCommandBlock extends BlockContainer {
    public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");

    public BlockCommandBlock() {
        super(Material.iron, MapColor.adobeColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TRIGGERED, Boolean.valueOf(false)));
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileEntityCommandBlock();
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!worldIn.isRemote) {
            final boolean flag = worldIn.isBlockPowered(pos);
            final boolean flag1 = state.getValue(TRIGGERED).booleanValue();

            if (flag && !flag1) {
                worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(true)), 4);
                worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            } else if (!flag && flag1) {
                worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(false)), 4);
            }
        }
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityCommandBlock) {
            ((TileEntityCommandBlock) tileentity).getCommandBlockLogic().trigger(worldIn);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World worldIn) {
        return 1;
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityCommandBlock && ((TileEntityCommandBlock) tileentity).getCommandBlockLogic().tryOpenEditCommandBlock(playerIn);
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(final World worldIn, final BlockPos pos) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock) tileentity).getCommandBlockLogic().getSuccessCount() : 0;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityCommandBlock) {
            final CommandBlockLogic commandblocklogic = ((TileEntityCommandBlock) tileentity).getCommandBlockLogic();

            if (stack.hasDisplayName()) {
                commandblocklogic.setName(stack.getDisplayName());
            }

            if (!worldIn.isRemote) {
                commandblocklogic.setTrackOutput(worldIn.getGameRules().getGameRuleBooleanValue("sendCommandFeedback"));
            }
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random random) {
        return 0;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType() {
        return 3;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(TRIGGERED, Boolean.valueOf((meta & 1) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;

        if (state.getValue(TRIGGERED).booleanValue()) {
            i |= 1;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, TRIGGERED);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return this.getDefaultState().withProperty(TRIGGERED, Boolean.valueOf(false));
    }
}
