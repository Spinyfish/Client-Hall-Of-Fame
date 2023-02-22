package net.minecraft.item;

import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class ItemMonsterPlacer extends Item {
    public ItemMonsterPlacer() {
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    public String getItemStackDisplayName(final ItemStack stack) {
        String s = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        final String s1 = EntityList.getStringFromID(stack.getMetadata());

        if (s1 != null) {
            s = s + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
        }

        return s;
    }

    public int getColorFromItemStack(final ItemStack stack, final int renderPass) {
        final EntityList.EntityEggInfo entitylist$entityegginfo = EntityList.entityEggs.get(Integer.valueOf(stack.getMetadata()));
        return entitylist$entityegginfo != null ? (renderPass == 0 ? entitylist$entityegginfo.primaryColor : entitylist$entityegginfo.secondaryColor) : 16777215;
    }

    /**
     * Called when a Block is right-clicked with this Item
     *
     * @param pos  The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
            return false;
        } else {
            final IBlockState iblockstate = worldIn.getBlockState(pos);

            if (iblockstate.getBlock() == Blocks.mob_spawner) {
                final TileEntity tileentity = worldIn.getTileEntity(pos);

                if (tileentity instanceof TileEntityMobSpawner) {
                    final MobSpawnerBaseLogic mobspawnerbaselogic = ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic();
                    mobspawnerbaselogic.setEntityName(EntityList.getStringFromID(stack.getMetadata()));
                    tileentity.markDirty();
                    worldIn.markBlockForUpdate(pos);

                    if (!playerIn.capabilities.isCreativeMode) {
                        --stack.stackSize;
                    }

                    return true;
                }
            }

            pos = pos.offset(side);
            double d0 = 0.0D;

            if (side == EnumFacing.UP && iblockstate instanceof BlockFence) {
                d0 = 0.5D;
            }

            final Entity entity = spawnCreature(worldIn, stack.getMetadata(), (double) pos.getX() + 0.5D, (double) pos.getY() + d0, (double) pos.getZ() + 0.5D);

            if (entity != null) {
                if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
                    entity.setCustomNameTag(stack.getDisplayName());
                }

                if (!playerIn.capabilities.isCreativeMode) {
                    --stack.stackSize;
                }
            }

            return true;
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(final ItemStack itemStackIn, final World worldIn, final EntityPlayer playerIn) {
        if (worldIn.isRemote) {
            return itemStackIn;
        } else {
            final MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, true);

            if (movingobjectposition == null) {
                return itemStackIn;
            } else {
                if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    final BlockPos blockpos = movingobjectposition.getBlockPos();

                    if (!worldIn.isBlockModifiable(playerIn, blockpos)) {
                        return itemStackIn;
                    }

                    if (!playerIn.canPlayerEdit(blockpos, movingobjectposition.sideHit, itemStackIn)) {
                        return itemStackIn;
                    }

                    if (worldIn.getBlockState(blockpos).getBlock() instanceof BlockLiquid) {
                        final Entity entity = spawnCreature(worldIn, itemStackIn.getMetadata(), (double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D);

                        if (entity != null) {
                            if (entity instanceof EntityLivingBase && itemStackIn.hasDisplayName()) {
                                entity.setCustomNameTag(itemStackIn.getDisplayName());
                            }

                            if (!playerIn.capabilities.isCreativeMode) {
                                --itemStackIn.stackSize;
                            }

                            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        }
                    }
                }

                return itemStackIn;
            }
        }
    }

    /**
     * Spawns the creature specified by the egg's type in the location specified by the last three parameters.
     * Parameters: world, entityID, x, y, z.
     */
    public static Entity spawnCreature(final World worldIn, final int entityID, final double x, final double y, final double z) {
        if (!EntityList.entityEggs.containsKey(Integer.valueOf(entityID))) {
            return null;
        } else {
            Entity entity = null;

            for (int i = 0; i < 1; ++i) {
                entity = EntityList.createEntityByID(entityID, worldIn);

                if (entity instanceof EntityLivingBase) {
                    final EntityLiving entityliving = (EntityLiving) entity;
                    entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(worldIn.rand.nextFloat() * 360.0F), 0.0F);
                    entityliving.rotationYawHead = entityliving.rotationYaw;
                    entityliving.renderYawOffset = entityliving.rotationYaw;
                    entityliving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityliving)), null);
                    worldIn.spawnEntityInWorld(entity);
                    entityliving.playLivingSound();
                }
            }

            return entity;
        }
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     *
     * @param subItems The List of sub-items. This is a List of ItemStacks.
     */
    public void getSubItems(final Item itemIn, final CreativeTabs tab, final List<ItemStack> subItems) {
        for (final EntityList.EntityEggInfo entitylist$entityegginfo : EntityList.entityEggs.values()) {
            subItems.add(new ItemStack(itemIn, 1, entitylist$entityegginfo.spawnedID));
        }
    }
}
