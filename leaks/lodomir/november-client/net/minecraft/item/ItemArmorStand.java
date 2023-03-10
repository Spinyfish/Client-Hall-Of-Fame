/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Rotations;
import net.minecraft.world.World;

public class ItemArmorStand
extends Item {
    public ItemArmorStand() {
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        double d2;
        double d1;
        BlockPos blockpos;
        if (side == EnumFacing.DOWN) {
            return false;
        }
        boolean flag = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
        BlockPos blockPos = blockpos = flag ? pos : pos.offset(side);
        if (!playerIn.canPlayerEdit(blockpos, side, stack)) {
            return false;
        }
        BlockPos blockpos1 = blockpos.up();
        boolean flag1 = !worldIn.isAirBlock(blockpos) && !worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos);
        if (flag1 |= !worldIn.isAirBlock(blockpos1) && !worldIn.getBlockState(blockpos1).getBlock().isReplaceable(worldIn, blockpos1)) {
            return false;
        }
        double d0 = blockpos.getX();
        List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.fromBounds(d0, d1 = (double)blockpos.getY(), d2 = (double)blockpos.getZ(), d0 + 1.0, d1 + 2.0, d2 + 1.0));
        if (list.size() > 0) {
            return false;
        }
        if (!worldIn.isRemote) {
            worldIn.setBlockToAir(blockpos);
            worldIn.setBlockToAir(blockpos1);
            EntityArmorStand entityarmorstand = new EntityArmorStand(worldIn, d0 + 0.5, d1, d2 + 0.5);
            float f = (float)MathHelper.floor_float((MathHelper.wrapAngleTo180_float(playerIn.rotationYaw - 180.0f) + 22.5f) / 45.0f) * 45.0f;
            entityarmorstand.setLocationAndAngles(d0 + 0.5, d1, d2 + 0.5, f, 0.0f);
            this.applyRandomRotations(entityarmorstand, worldIn.rand);
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            if (nbttagcompound != null && nbttagcompound.hasKey("EntityTag", 10)) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                entityarmorstand.writeToNBTOptional(nbttagcompound1);
                nbttagcompound1.merge(nbttagcompound.getCompoundTag("EntityTag"));
                entityarmorstand.readFromNBT(nbttagcompound1);
            }
            worldIn.spawnEntityInWorld(entityarmorstand);
        }
        --stack.stackSize;
        return true;
    }

    private void applyRandomRotations(EntityArmorStand armorStand, Random rand) {
        Rotations rotations = armorStand.getHeadRotation();
        float f = rand.nextFloat() * 5.0f;
        float f1 = rand.nextFloat() * 20.0f - 10.0f;
        Rotations rotations1 = new Rotations(rotations.getX() + f, rotations.getY() + f1, rotations.getZ());
        armorStand.setHeadRotation(rotations1);
        rotations = armorStand.getBodyRotation();
        f = rand.nextFloat() * 10.0f - 5.0f;
        rotations1 = new Rotations(rotations.getX(), rotations.getY() + f, rotations.getZ());
        armorStand.setBodyRotation(rotations1);
    }
}

