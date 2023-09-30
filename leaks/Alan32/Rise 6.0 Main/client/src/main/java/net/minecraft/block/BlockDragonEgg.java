package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDragonEgg extends Block {
    public BlockDragonEgg() {
        super(Material.dragonEgg, MapColor.blackColor);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
    }

    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        this.checkFall(worldIn, pos);
    }

    private void checkFall(final World worldIn, final BlockPos pos) {
        if (BlockFalling.canFallInto(worldIn, pos.down()) && pos.getY() >= 0) {
            final int i = 32;

            if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                worldIn.spawnEntityInWorld(new EntityFallingBlock(worldIn, (float) pos.getX() + 0.5F, pos.getY(), (float) pos.getZ() + 0.5F, this.getDefaultState()));
            } else {
                worldIn.setBlockToAir(pos);
                BlockPos blockpos;

                for (blockpos = pos; BlockFalling.canFallInto(worldIn, blockpos) && blockpos.getY() > 0; blockpos = blockpos.down()) {
                }

                if (blockpos.getY() > 0) {
                    worldIn.setBlockState(blockpos, this.getDefaultState(), 2);
                }
            }
        }
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        this.teleport(worldIn, pos);
        return true;
    }

    public void onBlockClicked(final World worldIn, final BlockPos pos, final EntityPlayer playerIn) {
        this.teleport(worldIn, pos);
    }

    private void teleport(final World worldIn, final BlockPos pos) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == this) {
            for (int i = 0; i < 1000; ++i) {
                final BlockPos blockpos = pos.add(worldIn.rand.nextInt(16) - worldIn.rand.nextInt(16), worldIn.rand.nextInt(8) - worldIn.rand.nextInt(8), worldIn.rand.nextInt(16) - worldIn.rand.nextInt(16));

                if (worldIn.getBlockState(blockpos).getBlock().blockMaterial == Material.air) {
                    if (worldIn.isRemote) {
                        for (int j = 0; j < 128; ++j) {
                            final double d0 = worldIn.rand.nextDouble();
                            final float f = (worldIn.rand.nextFloat() - 0.5F) * 0.2F;
                            final float f1 = (worldIn.rand.nextFloat() - 0.5F) * 0.2F;
                            final float f2 = (worldIn.rand.nextFloat() - 0.5F) * 0.2F;
                            final double d1 = (double) blockpos.getX() + (double) (pos.getX() - blockpos.getX()) * d0 + (worldIn.rand.nextDouble() - 0.5D) * 1.0D + 0.5D;
                            final double d2 = (double) blockpos.getY() + (double) (pos.getY() - blockpos.getY()) * d0 + worldIn.rand.nextDouble() * 1.0D - 0.5D;
                            final double d3 = (double) blockpos.getZ() + (double) (pos.getZ() - blockpos.getZ()) * d0 + (worldIn.rand.nextDouble() - 0.5D) * 1.0D + 0.5D;
                            worldIn.spawnParticle(EnumParticleTypes.PORTAL, d1, d2, d3, f, f1, f2);
                        }
                    } else {
                        worldIn.setBlockState(blockpos, iblockstate, 2);
                        worldIn.setBlockToAir(pos);
                    }

                    return;
                }
            }
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World worldIn) {
        return 5;
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

    public boolean shouldSideBeRendered(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        return true;
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return null;
    }
}
