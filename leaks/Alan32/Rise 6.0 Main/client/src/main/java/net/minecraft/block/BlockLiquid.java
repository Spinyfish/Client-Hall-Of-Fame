package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

import java.util.Random;

public abstract class BlockLiquid extends Block {
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

    protected BlockLiquid(final Material materialIn) {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setTickRandomly(true);
    }

    public boolean isPassable(final IBlockAccess worldIn, final BlockPos pos) {
        return this.blockMaterial != Material.lava;
    }

    public int colorMultiplier(final IBlockAccess worldIn, final BlockPos pos, final int renderPass) {
        return this.blockMaterial == Material.water ? BiomeColorHelper.getWaterColorAtPos(worldIn, pos) : 16777215;
    }

    /**
     * Returns the percentage of the liquid block that is air, based on the given flow decay of the liquid
     */
    public static float getLiquidHeightPercent(int meta) {
        if (meta >= 8) {
            meta = 0;
        }

        return (float) (meta + 1) / 9.0F;
    }

    protected int getLevel(final IBlockAccess worldIn, final BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock().getMaterial() == this.blockMaterial ? worldIn.getBlockState(pos).getValue(LEVEL).intValue() : -1;
    }

    protected int getEffectiveFlowDecay(final IBlockAccess worldIn, final BlockPos pos) {
        final int i = this.getLevel(worldIn, pos);
        return i >= 8 ? 0 : i;
    }

    public boolean isFullCube() {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean canCollideCheck(final IBlockState state, final boolean hitIfLiquid) {
        return hitIfLiquid && state.getValue(LEVEL).intValue() == 0;
    }

    /**
     * Whether this Block is solid on the given Side
     */
    public boolean isBlockSolid(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        final Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
        return material != this.blockMaterial && (side == EnumFacing.UP || (material != Material.ice && super.isBlockSolid(worldIn, pos, side)));
    }

    public boolean shouldSideBeRendered(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        return worldIn.getBlockState(pos).getBlock().getMaterial() != this.blockMaterial && (side == EnumFacing.UP || super.shouldSideBeRendered(worldIn, pos, side));
    }

    public boolean func_176364_g(final IBlockAccess blockAccess, final BlockPos pos) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                final IBlockState iblockstate = blockAccess.getBlockState(pos.add(i, 0, j));
                final Block block = iblockstate.getBlock();
                final Material material = block.getMaterial();

                if (material != this.blockMaterial && !block.isFullBlock()) {
                    return true;
                }
            }
        }

        return false;
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        return null;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType() {
        return 1;
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
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random random) {
        return 0;
    }

    protected Vec3 getFlowVector(final IBlockAccess worldIn, final BlockPos pos) {
        Vec3 vec3 = new Vec3(0.0D, 0.0D, 0.0D);
        final int i = this.getEffectiveFlowDecay(worldIn, pos);

        for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            final BlockPos blockpos = pos.offset(enumfacing);
            int j = this.getEffectiveFlowDecay(worldIn, blockpos);

            if (j < 0) {
                if (!worldIn.getBlockState(blockpos).getBlock().getMaterial().blocksMovement()) {
                    j = this.getEffectiveFlowDecay(worldIn, blockpos.down());

                    if (j >= 0) {
                        final int k = j - (i - 8);
                        vec3 = vec3.addVector((blockpos.getX() - pos.getX()) * k, (blockpos.getY() - pos.getY()) * k, (blockpos.getZ() - pos.getZ()) * k);
                    }
                }
            } else if (j >= 0) {
                final int l = j - i;
                vec3 = vec3.addVector((blockpos.getX() - pos.getX()) * l, (blockpos.getY() - pos.getY()) * l, (blockpos.getZ() - pos.getZ()) * l);
            }
        }

        if (worldIn.getBlockState(pos).getValue(LEVEL).intValue() >= 8) {
            for (final EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                final BlockPos blockpos1 = pos.offset(enumfacing1);

                if (this.isBlockSolid(worldIn, blockpos1, enumfacing1) || this.isBlockSolid(worldIn, blockpos1.up(), enumfacing1)) {
                    vec3 = vec3.normalize().addVector(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }

        return vec3.normalize();
    }

    public Vec3 modifyAcceleration(final World worldIn, final BlockPos pos, final Entity entityIn, final Vec3 motion) {
        return motion.add(this.getFlowVector(worldIn, pos));
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World worldIn) {
        return this.blockMaterial == Material.water ? 5 : (this.blockMaterial == Material.lava ? (worldIn.provider.getHasNoSky() ? 10 : 30) : 0);
    }

    public int getMixedBrightnessForBlock(final IBlockAccess worldIn, final BlockPos pos) {
        final int i = worldIn.getCombinedLight(pos, 0);
        final int j = worldIn.getCombinedLight(pos.up(), 0);
        final int k = i & 255;
        final int l = j & 255;
        final int i1 = i >> 16 & 255;
        final int j1 = j >> 16 & 255;
        return (Math.max(k, l)) | (Math.max(i1, j1)) << 16;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return this.blockMaterial == Material.water ? EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.SOLID;
    }

    public void randomDisplayTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        final double d0 = pos.getX();
        final double d1 = pos.getY();
        final double d2 = pos.getZ();

        if (this.blockMaterial == Material.water) {
            final int i = state.getValue(LEVEL);

            if (i > 0 && i < 8) {
                if (rand.nextInt(64) == 0) {
                    worldIn.playSound(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, "liquid.water", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() + 0.5F, false);
                }
            } else if (rand.nextInt(10) == 0) {
                worldIn.spawnParticle(EnumParticleTypes.SUSPENDED, d0 + (double) rand.nextFloat(), d1 + (double) rand.nextFloat(), d2 + (double) rand.nextFloat(), 0.0D, 0.0D, 0.0D);
            }
        }

        if (this.blockMaterial == Material.lava && worldIn.getBlockState(pos.up()).getBlock().getMaterial() == Material.air && !worldIn.getBlockState(pos.up()).getBlock().isOpaqueCube()) {
            if (rand.nextInt(100) == 0) {
                final double d8 = d0 + (double) rand.nextFloat();
                final double d4 = d1 + this.maxY;
                final double d6 = d2 + (double) rand.nextFloat();
                worldIn.spawnParticle(EnumParticleTypes.LAVA, d8, d4, d6, 0.0D, 0.0D, 0.0D);
                worldIn.playSound(d8, d4, d6, "liquid.lavapop", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
            }

            if (rand.nextInt(200) == 0) {
                worldIn.playSound(d0, d1, d2, "liquid.lava", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
            }
        }

        if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
            final Material material = worldIn.getBlockState(pos.down(2)).getBlock().getMaterial();

            if (!material.blocksMovement() && !material.isLiquid()) {
                final double d3 = d0 + (double) rand.nextFloat();
                final double d5 = d1 - 1.05D;
                final double d7 = d2 + (double) rand.nextFloat();

                if (this.blockMaterial == Material.water) {
                    worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d3, d5, d7, 0.0D, 0.0D, 0.0D);
                } else {
                    worldIn.spawnParticle(EnumParticleTypes.DRIP_LAVA, d3, d5, d7, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    public static double getFlowDirection(final IBlockAccess worldIn, final BlockPos pos, final Material materialIn) {
        final Vec3 vec3 = getFlowingBlock(materialIn).getFlowVector(worldIn, pos);
        return vec3.xCoord == 0.0D && vec3.zCoord == 0.0D ? -1000.0D : MathHelper.atan2(vec3.zCoord, vec3.xCoord) - (Math.PI / 2D);
    }

    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.checkForMixing(worldIn, pos, state);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        this.checkForMixing(worldIn, pos, state);
    }

    public boolean checkForMixing(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (this.blockMaterial == Material.lava) {
            boolean flag = false;

            for (final EnumFacing enumfacing : EnumFacing.values()) {
                if (enumfacing != EnumFacing.DOWN && worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getMaterial() == Material.water) {
                    flag = true;
                    break;
                }
            }

            if (flag) {
                final Integer integer = state.getValue(LEVEL);

                if (integer.intValue() == 0) {
                    worldIn.setBlockState(pos, Blocks.obsidian.getDefaultState());
                    this.triggerMixEffects(worldIn, pos);
                    return true;
                }

                if (integer.intValue() <= 4) {
                    worldIn.setBlockState(pos, Blocks.cobblestone.getDefaultState());
                    this.triggerMixEffects(worldIn, pos);
                    return true;
                }
            }
        }

        return false;
    }

    protected void triggerMixEffects(final World worldIn, final BlockPos pos) {
        final double d0 = pos.getX();
        final double d1 = pos.getY();
        final double d2 = pos.getZ();
        worldIn.playSoundEffect(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i) {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(LEVEL, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(LEVEL).intValue();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, LEVEL);
    }

    public static BlockDynamicLiquid getFlowingBlock(final Material materialIn) {
        if (materialIn == Material.water) {
            return Blocks.flowing_water;
        } else if (materialIn == Material.lava) {
            return Blocks.flowing_lava;
        } else {
            throw new IllegalArgumentException("Invalid material");
        }
    }

    public static BlockStaticLiquid getStaticBlock(final Material materialIn) {
        if (materialIn == Material.water) {
            return Blocks.water;
        } else if (materialIn == Material.lava) {
            return Blocks.lava;
        } else {
            throw new IllegalArgumentException("Invalid material");
        }
    }
}
