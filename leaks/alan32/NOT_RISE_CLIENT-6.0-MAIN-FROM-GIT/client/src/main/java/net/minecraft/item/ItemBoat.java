package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class ItemBoat extends Item {
    public ItemBoat() {
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(final ItemStack itemStackIn, final World worldIn, final EntityPlayer playerIn) {
        final float f = 1.0F;
        final float f1 = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * f;
        final float f2 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * f;
        final double d0 = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX) * (double) f;
        final double d1 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) * (double) f + (double) playerIn.getEyeHeight();
        final double d2 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ) * (double) f;
        final Vec3 vec3 = new Vec3(d0, d1, d2);
        final float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        final float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        final float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        final float f6 = MathHelper.sin(-f1 * 0.017453292F);
        final float f7 = f4 * f5;
        final float f8 = f3 * f5;
        final double d3 = 5.0D;
        final Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        final MovingObjectPosition movingobjectposition = worldIn.rayTraceBlocks(vec3, vec31, true);

        if (movingobjectposition == null) {
            return itemStackIn;
        } else {
            final Vec3 vec32 = playerIn.getLook(f);
            boolean flag = false;
            final float f9 = 1.0F;
            final List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.getEntityBoundingBox().addCoord(vec32.xCoord * d3, vec32.yCoord * d3, vec32.zCoord * d3).expand(f9, f9, f9));

            for (int i = 0; i < list.size(); ++i) {
                final Entity entity = list.get(i);

                if (entity.canBeCollidedWith()) {
                    final float f10 = entity.getCollisionBorderSize();
                    final AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(f10, f10, f10);

                    if (axisalignedbb.isVecInside(vec3)) {
                        flag = true;
                    }
                }
            }

            if (flag) {
                return itemStackIn;
            } else {
                if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    BlockPos blockpos = movingobjectposition.getBlockPos();

                    if (worldIn.getBlockState(blockpos).getBlock() == Blocks.snow_layer) {
                        blockpos = blockpos.down();
                    }

                    final EntityBoat entityboat = new EntityBoat(worldIn, (float) blockpos.getX() + 0.5F, (float) blockpos.getY() + 1.0F, (float) blockpos.getZ() + 0.5F);
                    entityboat.rotationYaw = (float) (((MathHelper.floor_double((double) (playerIn.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) - 1) * 90);

                    if (!worldIn.getCollidingBoundingBoxes(entityboat, entityboat.getEntityBoundingBox().expand(-0.1D, -0.1D, -0.1D)).isEmpty()) {
                        return itemStackIn;
                    }

                    if (!worldIn.isRemote) {
                        worldIn.spawnEntityInWorld(entityboat);
                    }

                    if (!playerIn.capabilities.isCreativeMode) {
                        --itemStackIn.stackSize;
                    }

                    playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                }

                return itemStackIn;
            }
        }
    }
}
