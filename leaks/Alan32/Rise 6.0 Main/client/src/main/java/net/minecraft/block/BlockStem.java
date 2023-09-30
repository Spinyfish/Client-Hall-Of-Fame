package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockStem extends BlockBush implements IGrowable {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
    public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
        public boolean apply(final EnumFacing p_apply_1_) {
            return p_apply_1_ != EnumFacing.DOWN;
        }
    });
    private final Block crop;

    protected BlockStem(final Block crop) {
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)).withProperty(FACING, EnumFacing.UP));
        this.crop = crop;
        this.setTickRandomly(true);
        final float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab(null);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        state = state.withProperty(FACING, EnumFacing.UP);

        for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() == this.crop) {
                state = state.withProperty(FACING, enumfacing);
                break;
            }
        }

        return state;
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(final Block ground) {
        return ground == Blocks.farmland;
    }

    public void updateTick(final World worldIn, BlockPos pos, IBlockState state, final Random rand) {
        super.updateTick(worldIn, pos, state, rand);

        if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
            final float f = BlockCrops.getGrowthChance(this, worldIn, pos);

            if (rand.nextInt((int) (25.0F / f) + 1) == 0) {
                final int i = state.getValue(AGE).intValue();

                if (i < 7) {
                    state = state.withProperty(AGE, Integer.valueOf(i + 1));
                    worldIn.setBlockState(pos, state, 2);
                } else {
                    for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                        if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() == this.crop) {
                            return;
                        }
                    }

                    pos = pos.offset(EnumFacing.Plane.HORIZONTAL.random(rand));
                    final Block block = worldIn.getBlockState(pos.down()).getBlock();

                    if (worldIn.getBlockState(pos).getBlock().blockMaterial == Material.air && (block == Blocks.farmland || block == Blocks.dirt || block == Blocks.grass)) {
                        worldIn.setBlockState(pos, this.crop.getDefaultState());
                    }
                }
            }
        }
    }

    public void growStem(final World worldIn, final BlockPos pos, final IBlockState state) {
        final int i = state.getValue(AGE).intValue() + MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);
        worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(Math.min(7, i))), 2);
    }

    public int getRenderColor(final IBlockState state) {
        if (state.getBlock() != this) {
            return super.getRenderColor(state);
        } else {
            final int i = state.getValue(AGE).intValue();
            final int j = i * 32;
            final int k = 255 - i * 8;
            final int l = i * 4;
            return j << 16 | k << 8 | l;
        }
    }

    public int colorMultiplier(final IBlockAccess worldIn, final BlockPos pos, final int renderPass) {
        return this.getRenderColor(worldIn.getBlockState(pos));
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        final float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        this.maxY = (float) (worldIn.getBlockState(pos).getValue(AGE).intValue() * 2 + 2) / 16.0F;
        final float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (float) this.maxY, 0.5F + f);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *
     * @param chance  The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);

        if (!worldIn.isRemote) {
            final Item item = this.getSeedItem();

            if (item != null) {
                final int i = state.getValue(AGE).intValue();

                for (int j = 0; j < 3; ++j) {
                    if (worldIn.rand.nextInt(15) <= i) {
                        spawnAsEntity(worldIn, pos, new ItemStack(item));
                    }
                }
            }
        }
    }

    protected Item getSeedItem() {
        return this.crop == Blocks.pumpkin ? Items.pumpkin_seeds : (this.crop == Blocks.melon_block ? Items.melon_seeds : null);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return null;
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        final Item item = this.getSeedItem();
        return item;
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(final World worldIn, final BlockPos pos, final IBlockState state, final boolean isClient) {
        return state.getValue(AGE).intValue() != 7;
    }

    public boolean canUseBonemeal(final World worldIn, final Random rand, final BlockPos pos, final IBlockState state) {
        return true;
    }

    public void grow(final World worldIn, final Random rand, final BlockPos pos, final IBlockState state) {
        this.growStem(worldIn, pos, state);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(AGE).intValue();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, AGE, FACING);
    }
}
