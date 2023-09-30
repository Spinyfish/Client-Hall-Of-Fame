package net.minecraft.block;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCrops extends BlockBush implements IGrowable {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

    protected BlockCrops() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        final float f = 0.5F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab(null);
        this.setHardness(0.0F);
        this.setStepSound(soundTypeGrass);
        this.disableStats();
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(final Block ground) {
        return ground == Blocks.farmland;
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        super.updateTick(worldIn, pos, state, rand);

        if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
            final int i = state.getValue(AGE).intValue();

            if (i < 7) {
                final float f = getGrowthChance(this, worldIn, pos);

                if (rand.nextInt((int) (25.0F / f) + 1) == 0) {
                    worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i + 1)), 2);
                }
            }
        }
    }

    public void grow(final World worldIn, final BlockPos pos, final IBlockState state) {
        int i = state.getValue(AGE).intValue() + MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);

        if (i > 7) {
            i = 7;
        }

        worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i)), 2);
    }

    protected static float getGrowthChance(final Block blockIn, final World worldIn, final BlockPos pos) {
        float f = 1.0F;
        final BlockPos blockpos = pos.down();

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float f1 = 0.0F;
                final IBlockState iblockstate = worldIn.getBlockState(blockpos.add(i, 0, j));

                if (iblockstate.getBlock() == Blocks.farmland) {
                    f1 = 1.0F;

                    if (iblockstate.getValue(BlockFarmland.MOISTURE).intValue() > 0) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        final BlockPos blockpos1 = pos.north();
        final BlockPos blockpos2 = pos.south();
        final BlockPos blockpos3 = pos.west();
        final BlockPos blockpos4 = pos.east();
        final boolean flag = blockIn == worldIn.getBlockState(blockpos3).getBlock() || blockIn == worldIn.getBlockState(blockpos4).getBlock();
        final boolean flag1 = blockIn == worldIn.getBlockState(blockpos1).getBlock() || blockIn == worldIn.getBlockState(blockpos2).getBlock();

        if (flag && flag1) {
            f /= 2.0F;
        } else {
            final boolean flag2 = blockIn == worldIn.getBlockState(blockpos3.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.south()).getBlock() || blockIn == worldIn.getBlockState(blockpos3.south()).getBlock();

            if (flag2) {
                f /= 2.0F;
            }
        }

        return f;
    }

    public boolean canBlockStay(final World worldIn, final BlockPos pos, final IBlockState state) {
        return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
    }

    protected Item getSeed() {
        return Items.wheat_seeds;
    }

    protected Item getCrop() {
        return Items.wheat;
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *
     * @param chance  The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);

        if (!worldIn.isRemote) {
            final int i = state.getValue(AGE).intValue();

            if (i >= 7) {
                final int j = 3 + fortune;

                for (int k = 0; k < j; ++k) {
                    if (worldIn.rand.nextInt(15) <= i) {
                        spawnAsEntity(worldIn, pos, new ItemStack(this.getSeed(), 1, 0));
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
        return state.getValue(AGE).intValue() == 7 ? this.getCrop() : this.getSeed();
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return this.getSeed();
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(final World worldIn, final BlockPos pos, final IBlockState state, final boolean isClient) {
        return state.getValue(AGE).intValue() < 7;
    }

    public boolean canUseBonemeal(final World worldIn, final Random rand, final BlockPos pos, final IBlockState state) {
        return true;
    }

    public void grow(final World worldIn, final Random rand, final BlockPos pos, final IBlockState state) {
        this.grow(worldIn, pos, state);
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
        return new BlockState(this, AGE);
    }
}
