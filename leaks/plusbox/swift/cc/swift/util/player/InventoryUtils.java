package cc.swift.util.player;

import cc.swift.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class InventoryUtils {

    public static float getPlayerRelativeBlockHardness(World worldIn, BlockPos pos, int slot) {
        final Block block = Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
        float f = block.getBlockHardness(worldIn, pos);
        return f < 0.0F ? 0.0F : (!canHeldItemHarvest(block,slot) ? getToolDigEfficiency(block, slot) / f / 100.0F : getToolDigEfficiency(block,slot) / f / 30.0F);
    }

    public static boolean canHeldItemHarvest(final Block blockIn, final int slot) {
        if (blockIn.getMaterial().isToolNotRequired()) {
            return true;
        } else if (Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(slot) != null) {
            final ItemStack itemstack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(slot);
            return itemstack != null && itemstack.canHarvestBlock(blockIn);
        } else
            return false;
    }

    public static float getStrVsBlock(final Block blockIn, final int slot) {
        float f = 1.0F;

        if (Minecraft.getMinecraft().thePlayer.inventory.mainInventory[slot] != null) {
            f *= Minecraft.getMinecraft().thePlayer.inventory.mainInventory[slot].getStrVsBlock(blockIn);
        }
        return f;
    }

    public static ItemStack getCurrentItemInSlot(final int slot) {
        return slot < 9 && slot >= 0 ? Minecraft.getMinecraft().thePlayer.inventory.mainInventory[slot] : null;
    }

    public static float getToolDigEfficiency ( final Block blockIn, final int slot){
        float f = getStrVsBlock(blockIn, slot);

        if (f > 1.0F) {
            final int i = EnchantmentHelper.getEfficiencyModifier(Minecraft.getMinecraft().thePlayer);
            final ItemStack itemstack = getCurrentItemInSlot(slot);

            if (i > 0 && itemstack != null) {
                f += (float) (i * i + 1);
            }
        }

        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.digSpeed)) {
            f *= 1.0F + (float) (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }

        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.digSlowdown)) {
            final float f1;

            switch (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;

                case 1:
                    f1 = 0.09F;
                    break;

                case 2:
                    f1 = 0.0027F;
                    break;

                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (Minecraft.getMinecraft().thePlayer.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(Minecraft.getMinecraft().thePlayer)) {
            f /= 5.0F;
        }

        if (!Minecraft.getMinecraft().thePlayer.onGround) {
            f /= 5.0F;
        }

        return f;
    }
}
