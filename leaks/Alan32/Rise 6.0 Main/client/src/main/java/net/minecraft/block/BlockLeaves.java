package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

import java.util.Random;

public abstract class BlockLeaves extends BlockLeavesBase {
    public static final PropertyBool DECAYABLE = PropertyBool.create("decayable");
    public static final PropertyBool CHECK_DECAY = PropertyBool.create("check_decay");
    int[] surroundings;
    protected int iconIndex;
    protected boolean isTransparent;

    public BlockLeaves() {
        super(Material.leaves, false);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(0.2F);
        this.setLightOpacity(1);
        this.setStepSound(soundTypeGrass);
    }

    public int getBlockColor() {
        return ColorizerFoliage.getFoliageColor(0.5D, 1.0D);
    }

    public int getRenderColor(final IBlockState state) {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    public int colorMultiplier(final IBlockAccess worldIn, final BlockPos pos, final int renderPass) {
        return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final int i = 1;
        final int j = i + 1;
        final int k = pos.getX();
        final int l = pos.getY();
        final int i1 = pos.getZ();

        if (worldIn.isAreaLoaded(new BlockPos(k - j, l - j, i1 - j), new BlockPos(k + j, l + j, i1 + j))) {
            for (int j1 = -i; j1 <= i; ++j1) {
                for (int k1 = -i; k1 <= i; ++k1) {
                    for (int l1 = -i; l1 <= i; ++l1) {
                        final BlockPos blockpos = pos.add(j1, k1, l1);
                        final IBlockState iblockstate = worldIn.getBlockState(blockpos);

                        if (iblockstate.getBlock().getMaterial() == Material.leaves && !iblockstate.getValue(CHECK_DECAY).booleanValue()) {
                            worldIn.setBlockState(blockpos, iblockstate.withProperty(CHECK_DECAY, Boolean.valueOf(true)), 4);
                        }
                    }
                }
            }
        }
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (!worldIn.isRemote) {
            if (state.getValue(CHECK_DECAY).booleanValue() && state.getValue(DECAYABLE).booleanValue()) {
                final int i = 4;
                final int j = i + 1;
                final int k = pos.getX();
                final int l = pos.getY();
                final int i1 = pos.getZ();
                final int j1 = 32;
                final int k1 = j1 * j1;
                final int l1 = j1 / 2;

                if (this.surroundings == null) {
                    this.surroundings = new int[j1 * j1 * j1];
                }

                if (worldIn.isAreaLoaded(new BlockPos(k - j, l - j, i1 - j), new BlockPos(k + j, l + j, i1 + j))) {
                    final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                    for (int i2 = -i; i2 <= i; ++i2) {
                        for (int j2 = -i; j2 <= i; ++j2) {
                            for (int k2 = -i; k2 <= i; ++k2) {
                                final Block block = worldIn.getBlockState(blockpos$mutableblockpos.func_181079_c(k + i2, l + j2, i1 + k2)).getBlock();

                                if (block != Blocks.log && block != Blocks.log2) {
                                    if (block.getMaterial() == Material.leaves) {
                                        this.surroundings[(i2 + l1) * k1 + (j2 + l1) * j1 + k2 + l1] = -2;
                                    } else {
                                        this.surroundings[(i2 + l1) * k1 + (j2 + l1) * j1 + k2 + l1] = -1;
                                    }
                                } else {
                                    this.surroundings[(i2 + l1) * k1 + (j2 + l1) * j1 + k2 + l1] = 0;
                                }
                            }
                        }
                    }

                    for (int i3 = 1; i3 <= 4; ++i3) {
                        for (int j3 = -i; j3 <= i; ++j3) {
                            for (int k3 = -i; k3 <= i; ++k3) {
                                for (int l3 = -i; l3 <= i; ++l3) {
                                    if (this.surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + l3 + l1] == i3 - 1) {
                                        if (this.surroundings[(j3 + l1 - 1) * k1 + (k3 + l1) * j1 + l3 + l1] == -2) {
                                            this.surroundings[(j3 + l1 - 1) * k1 + (k3 + l1) * j1 + l3 + l1] = i3;
                                        }

                                        if (this.surroundings[(j3 + l1 + 1) * k1 + (k3 + l1) * j1 + l3 + l1] == -2) {
                                            this.surroundings[(j3 + l1 + 1) * k1 + (k3 + l1) * j1 + l3 + l1] = i3;
                                        }

                                        if (this.surroundings[(j3 + l1) * k1 + (k3 + l1 - 1) * j1 + l3 + l1] == -2) {
                                            this.surroundings[(j3 + l1) * k1 + (k3 + l1 - 1) * j1 + l3 + l1] = i3;
                                        }

                                        if (this.surroundings[(j3 + l1) * k1 + (k3 + l1 + 1) * j1 + l3 + l1] == -2) {
                                            this.surroundings[(j3 + l1) * k1 + (k3 + l1 + 1) * j1 + l3 + l1] = i3;
                                        }

                                        if (this.surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + (l3 + l1 - 1)] == -2) {
                                            this.surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + (l3 + l1 - 1)] = i3;
                                        }

                                        if (this.surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + l3 + l1 + 1] == -2) {
                                            this.surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + l3 + l1 + 1] = i3;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                final int l2 = this.surroundings[l1 * k1 + l1 * j1 + l1];

                if (l2 >= 0) {
                    worldIn.setBlockState(pos, state.withProperty(CHECK_DECAY, Boolean.valueOf(false)), 4);
                } else {
                    this.destroy(worldIn, pos);
                }
            }
        }
    }

    public void randomDisplayTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (worldIn.canLightningStrike(pos.up()) && !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && rand.nextInt(15) == 1) {
            final double d0 = (float) pos.getX() + rand.nextFloat();
            final double d1 = (double) pos.getY() - 0.05D;
            final double d2 = (float) pos.getZ() + rand.nextFloat();
            worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    private void destroy(final World worldIn, final BlockPos pos) {
        this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
        worldIn.setBlockToAir(pos);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random random) {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemFromBlock(Blocks.sapling);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *
     * @param chance  The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        if (!worldIn.isRemote) {
            int i = this.getSaplingDropChance(state);

            if (fortune > 0) {
                i -= 2 << fortune;

                if (i < 10) {
                    i = 10;
                }
            }

            if (worldIn.rand.nextInt(i) == 0) {
                final Item item = this.getItemDropped(state, worldIn.rand, fortune);
                spawnAsEntity(worldIn, pos, new ItemStack(item, 1, this.damageDropped(state)));
            }

            i = 200;

            if (fortune > 0) {
                i -= 10 << fortune;

                if (i < 40) {
                    i = 40;
                }
            }

            this.dropApple(worldIn, pos, state, i);
        }
    }

    protected void dropApple(final World worldIn, final BlockPos pos, final IBlockState state, final int chance) {
    }

    protected int getSaplingDropChance(final IBlockState state) {
        return 20;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return !this.fancyGraphics;
    }

    /**
     * Pass true to draw this block using fancy graphics, or false for fast graphics.
     */
    public void setGraphicsLevel(final boolean fancy) {
        this.isTransparent = fancy;
        this.fancyGraphics = fancy;
        this.iconIndex = fancy ? 0 : 1;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return this.isTransparent ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
    }

    public boolean isVisuallyOpaque() {
        return false;
    }

    public abstract BlockPlanks.EnumType getWoodType(int meta);
}
