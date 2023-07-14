/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemLead
extends Item {
    public ItemLead() {
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        Block block = worldIn.getBlockState(pos).getBlock();
        if (block instanceof BlockFence) {
            if (worldIn.isRemote) {
                return true;
            }
            ItemLead.attachToFence(playerIn, worldIn, pos);
            return true;
        }
        return false;
    }

    public static boolean attachToFence(EntityPlayer player, World worldIn, BlockPos fence) {
        EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForPosition(worldIn, fence);
        boolean flag = false;
        double d0 = 7.0;
        int i = fence.getX();
        int j = fence.getY();
        int k = fence.getZ();
        for (EntityLiving entityliving : worldIn.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB((double)i - d0, (double)j - d0, (double)k - d0, (double)i + d0, (double)j + d0, (double)k + d0))) {
            if (!entityliving.getLeashed() || entityliving.getLeashedToEntity() != player) continue;
            if (entityleashknot == null) {
                entityleashknot = EntityLeashKnot.createKnot(worldIn, fence);
            }
            entityliving.setLeashedToEntity(entityleashknot, true);
            flag = true;
        }
        return flag;
    }
}

