/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBoat
extends Item {
    public ItemBoat() {
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        float f8;
        float f6;
        double d3;
        float f5;
        float f = 1.0f;
        float f1 = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * f;
        float f2 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * f;
        double d0 = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX) * (double)f;
        double d1 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) * (double)f + (double)playerIn.getEyeHeight();
        double d2 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ) * (double)f;
        Vec3 vec3 = new Vec3(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * ((float)Math.PI / 180) - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * ((float)Math.PI / 180) - (float)Math.PI);
        float f7 = f4 * (f5 = -MathHelper.cos(-f1 * ((float)Math.PI / 180)));
        Vec3 vec31 = vec3.addVector((double)f7 * (d3 = 5.0), (double)(f6 = MathHelper.sin(-f1 * ((float)Math.PI / 180))) * d3, (double)(f8 = f3 * f5) * d3);
        MovingObjectPosition movingobjectposition = worldIn.rayTraceBlocks(vec3, vec31, true);
        if (movingobjectposition == null) {
            return itemStackIn;
        }
        Vec3 vec32 = playerIn.getLook(f);
        boolean flag = false;
        float f9 = 1.0f;
        List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.getEntityBoundingBox().addCoord(vec32.xCoord * d3, vec32.yCoord * d3, vec32.zCoord * d3).expand(f9, f9, f9));
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (!entity.canBeCollidedWith()) continue;
            float f10 = entity.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(f10, f10, f10);
            if (!axisalignedbb.isVecInside(vec3)) continue;
            flag = true;
        }
        if (flag) {
            return itemStackIn;
        }
        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockpos = movingobjectposition.getBlockPos();
            if (worldIn.getBlockState(blockpos).getBlock() == Blocks.snow_layer) {
                blockpos = blockpos.down();
            }
            EntityBoat entityboat = new EntityBoat(worldIn, (float)blockpos.getX() + 0.5f, (float)blockpos.getY() + 1.0f, (float)blockpos.getZ() + 0.5f);
            entityboat.rotationYaw = ((MathHelper.floor_double((double)(playerIn.rotationYaw * 4.0f / 360.0f) + 0.5) & 3) - 1) * 90;
            if (!worldIn.getCollidingBoundingBoxes(entityboat, entityboat.getEntityBoundingBox().expand(-0.1, -0.1, -0.1)).isEmpty()) {
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

