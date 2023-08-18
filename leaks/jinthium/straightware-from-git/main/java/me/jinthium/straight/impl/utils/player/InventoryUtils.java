package me.jinthium.straight.impl.utils.player;

import me.jinthium.straight.api.util.MinecraftInstance;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class InventoryUtils implements MinecraftInstance {

    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int ONLY_HOT_BAR_BEGIN = 36;
    public static final int END = 45;

    public static Item getHeldItem() {
        if (mc.thePlayer == null || mc.thePlayer.getCurrentEquippedItem() == null) return null;
        return mc.thePlayer.getCurrentEquippedItem().getItem();
    }

    public static float getSwordStrength(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword sword) {
            float sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
            float fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1.5F;
            return sword.getDamageVsEntity() + sharpness + fireAspect;
        }
        return 0;
    }

    public static int findItem(final Item item) {
        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack == null) {
                if (item == null) {
                    return i;
                }
                continue;
            }

            if (itemStack.getItem() == item) {
                return i;
            }
        }

        return -1;
    }


    public static int findItemInstOf(Class<? extends Item> item) {
        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack == null) {
                if (item == null) {
                    return i;
                }
                continue;
            }

            if (itemStack.getItem() instanceof ItemBlock) {
                return i;
            }
        }

        return -1;
    }

    public static float getPlayerRelativeBlockHardness(final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final int slot) {
        final Block block = mc.theWorld.getBlockState(pos).getBlock();
        final float f = block.getBlockHardness(worldIn, pos);
        return f < 0.0F ? 0.0F : (!canHeldItemHarvest(block, slot) ? getToolDigEfficiency(block, slot) / f / 100.0F : getToolDigEfficiency(block, slot) / f / 30.0F);
    }

    public static float getStrVsBlock(final Block blockIn, final int slot) {
        float f = 1.0F;

        if (mc.thePlayer.inventory.mainInventory[slot] != null) {
            f *= mc.thePlayer.inventory.mainInventory[slot].getStrVsBlock(blockIn);
        }
        return f;
    }

    public static boolean canHeldItemHarvest(final Block blockIn, final int slot) {
        if (blockIn.getMaterial().isToolNotRequired()) {
            return true;
        } else {
            final ItemStack itemstack = mc.thePlayer.inventory.getStackInSlot(slot);
            return itemstack != null && itemstack.canHarvestBlock(blockIn);
        }
    }

    public static float getToolDigEfficiency(final Block blockIn, final int slot) {
        float f = getStrVsBlock(blockIn, slot);

        if (f > 1.0F) {
            final int i = EnchantmentHelper.getEfficiencyModifier(mc.thePlayer);
            final ItemStack itemstack = getCurrentItemInSlot(slot);

            if (i > 0 && itemstack != null) {
                f += (float) (i * i + 1);
            }
        }

        if (mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            f *= 1.0F + (float) (mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }

        if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            final float f1 = switch (mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };

            f *= f1;
        }

        if (mc.thePlayer.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(mc.thePlayer)) {
            f /= 5.0F;
        }

        if (!mc.thePlayer.onGround) {
            f /= 5.0F;
        }

        return f;
    }

    public static ItemStack getCurrentItemInSlot(final int slot) {
        return slot < 9 && slot >= 0 ? mc.thePlayer.inventory.mainInventory[slot] : null;
    }

    public static boolean isStackValidToPlace(final ItemStack stack) {
        return stack.stackSize >= 1 && validateBlock(Block.getBlockFromItem(stack.getItem()), BlockAction.PLACE);
    }

    public enum BlockAction {
        PLACE, REPLACE, PLACE_ON
    }

    public static boolean validateBlock(final Block block, final BlockAction action) {
        if (block instanceof BlockContainer) return false;
        final Material material = block.getMaterial();

        return switch (action) {
            case PLACE -> !(block instanceof BlockFalling) && block.isFullBlock() && block.isFullCube();
            case REPLACE -> material.isReplaceable();
            case PLACE_ON -> !(block instanceof BlockAir);
        };
    }

    public static void click(int slot, int mouseButton, boolean shiftClick) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, mouseButton, shiftClick ? 1 : 0, mc.thePlayer);
    }

    public static void drop(int slot) {
        mc.playerController.windowClick(0, slot, 1, 4, mc.thePlayer);
    }

    public static void swap(int slot, int hSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hSlot, 2, mc.thePlayer);
    }

    public static boolean isHoldingSword() {
        return getHeldItem() instanceof ItemSword;
    }
}
