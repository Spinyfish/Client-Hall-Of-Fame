package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemBucket extends Item {
    /**
     * field for checking if the bucket has been filled.
     */
    private final Block isFull;

    public ItemBucket(final Block containedBlock) {
        this.maxStackSize = 1;
        this.isFull = containedBlock;
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(final ItemStack itemStackIn, final World worldIn, final EntityPlayer playerIn) {
        final boolean flag = this.isFull == Blocks.air;
        final MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, flag);

        if (movingobjectposition == null) {
            return itemStackIn;
        } else {
            if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                final BlockPos blockpos = movingobjectposition.getBlockPos();

                if (!worldIn.isBlockModifiable(playerIn, blockpos)) {
                    return itemStackIn;
                }

                if (flag) {
                    if (!playerIn.canPlayerEdit(blockpos.offset(movingobjectposition.sideHit), movingobjectposition.sideHit, itemStackIn)) {
                        return itemStackIn;
                    }

                    final IBlockState iblockstate = worldIn.getBlockState(blockpos);
                    final Material material = iblockstate.getBlock().getMaterial();

                    if (material == Material.water && iblockstate.getValue(BlockLiquid.LEVEL).intValue() == 0) {
                        worldIn.setBlockToAir(blockpos);
                        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        return this.fillBucket(itemStackIn, playerIn, Items.water_bucket);
                    }

                    if (material == Material.lava && iblockstate.getValue(BlockLiquid.LEVEL).intValue() == 0) {
                        worldIn.setBlockToAir(blockpos);
                        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        return this.fillBucket(itemStackIn, playerIn, Items.lava_bucket);
                    }
                } else {
                    if (this.isFull == Blocks.air) {
                        return new ItemStack(Items.bucket);
                    }

                    final BlockPos blockpos1 = blockpos.offset(movingobjectposition.sideHit);

                    if (!playerIn.canPlayerEdit(blockpos1, movingobjectposition.sideHit, itemStackIn)) {
                        return itemStackIn;
                    }

                    if (this.tryPlaceContainedLiquid(worldIn, blockpos1) && !playerIn.capabilities.isCreativeMode) {
                        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        return new ItemStack(Items.bucket);
                    }
                }
            }

            return itemStackIn;
        }
    }

    private ItemStack fillBucket(final ItemStack emptyBuckets, final EntityPlayer player, final Item fullBucket) {
        if (player.capabilities.isCreativeMode) {
            return emptyBuckets;
        } else if (--emptyBuckets.stackSize <= 0) {
            return new ItemStack(fullBucket);
        } else {
            if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket))) {
                player.dropPlayerItemWithRandomChoice(new ItemStack(fullBucket, 1, 0), false);
            }

            return emptyBuckets;
        }
    }

    public boolean tryPlaceContainedLiquid(final World worldIn, final BlockPos pos) {
        if (this.isFull == Blocks.air) {
            return false;
        } else {
            final Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
            final boolean flag = !material.isSolid();

            if (!worldIn.isAirBlock(pos) && !flag) {
                return false;
            } else {
                if (worldIn.provider.doesWaterVaporize() && this.isFull == Blocks.flowing_water) {
                    final int i = pos.getX();
                    final int j = pos.getY();
                    final int k = pos.getZ();
                    worldIn.playSoundEffect((float) i + 0.5F, (float) j + 0.5F, (float) k + 0.5F, "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                    for (int l = 0; l < 8; ++l) {
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                } else {
                    if (!worldIn.isRemote && flag && !material.isLiquid()) {
                        worldIn.destroyBlock(pos, true);
                    }

                    worldIn.setBlockState(pos, this.isFull.getDefaultState(), 3);
                }

                return true;
            }
        }
    }
}
