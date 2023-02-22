package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFarmland extends Block {
    public static final PropertyInteger MOISTURE = PropertyInteger.create("moisture", 0, 7);

    protected BlockFarmland() {
        super(Material.ground);
        this.setDefaultState(this.blockState.getBaseState().withProperty(MOISTURE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        this.setLightOpacity(255);
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
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

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        final int i = state.getValue(MOISTURE).intValue();

        if (!this.hasWater(worldIn, pos) && !worldIn.canLightningStrike(pos.up())) {
            if (i > 0) {
                worldIn.setBlockState(pos, state.withProperty(MOISTURE, Integer.valueOf(i - 1)), 2);
            } else if (!this.hasCrops(worldIn, pos)) {
                worldIn.setBlockState(pos, Blocks.dirt.getDefaultState());
            }
        } else if (i < 7) {
            worldIn.setBlockState(pos, state.withProperty(MOISTURE, Integer.valueOf(7)), 2);
        }
    }

    /**
     * Block's chance to react to a living entity falling on it.
     *
     * @param fallDistance The distance the entity has fallen before landing
     */
    public void onFallenUpon(final World worldIn, final BlockPos pos, final Entity entityIn, final float fallDistance) {
        if (entityIn instanceof EntityLivingBase) {
            if (!worldIn.isRemote && worldIn.rand.nextFloat() < fallDistance - 0.5F) {
                if (!(entityIn instanceof EntityPlayer) && !worldIn.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                    return;
                }

                worldIn.setBlockState(pos, Blocks.dirt.getDefaultState());
            }

            super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        }
    }

    private boolean hasCrops(final World worldIn, final BlockPos pos) {
        final Block block = worldIn.getBlockState(pos.up()).getBlock();
        return block instanceof BlockCrops || block instanceof BlockStem;
    }

    private boolean hasWater(final World worldIn, final BlockPos pos) {
        for (final BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (worldIn.getBlockState(blockpos$mutableblockpos).getBlock().getMaterial() == Material.water) {
                return true;
            }
        }

        return false;
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);

        if (worldIn.getBlockState(pos.up()).getBlock().getMaterial().isSolid()) {
            worldIn.setBlockState(pos, Blocks.dirt.getDefaultState());
        }
    }

    public boolean shouldSideBeRendered(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        switch (side) {
            case UP:
                return true;

            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                final Block block = worldIn.getBlockState(pos).getBlock();
                return !block.isOpaqueCube() && block != Blocks.farmland;

            default:
                return super.shouldSideBeRendered(worldIn, pos, side);
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Blocks.dirt.getItemDropped(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Item.getItemFromBlock(Blocks.dirt);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(MOISTURE, Integer.valueOf(meta & 7));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(MOISTURE).intValue();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, MOISTURE);
    }
}
