/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemSpade
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.world.World
 */
package vip.astroline.client.storage.utils.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

public class InvUtils {
    private static Minecraft mc = Minecraft.getMinecraft();
    public static int pickaxeSlot = 37;
    public static int axeSlot = 38;
    public static int shovelSlot = 39;

    public static int findEmptySlot() {
        for (int i = 0; i < 8; ++i) {
            if (InvUtils.mc.thePlayer.inventory.mainInventory[i] != null) continue;
            return i;
        }
        return InvUtils.mc.thePlayer.inventory.currentItem + (InvUtils.mc.thePlayer.inventory.getCurrentItem() == null ? 0 : (InvUtils.mc.thePlayer.inventory.currentItem < 8 ? 4 : -1));
    }

    public static int findEmptySlot(int priority) {
        if (InvUtils.mc.thePlayer.inventory.mainInventory[priority] != null) return InvUtils.findEmptySlot();
        return priority;
    }

    public static void swapShift(int slot) {
        InvUtils.mc.playerController.windowClick(InvUtils.mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, (EntityPlayer)InvUtils.mc.thePlayer);
    }

    public static void swap(int slot, int hotbarNum) {
        InvUtils.mc.playerController.windowClick(InvUtils.mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, (EntityPlayer)InvUtils.mc.thePlayer);
    }

    public static boolean isFull() {
        return !Arrays.asList(InvUtils.mc.thePlayer.inventory.mainInventory).contains(null);
    }

    public static int armorSlotToNormalSlot(int armorSlot) {
        return 8 - armorSlot;
    }

    public static void block() {
        InvUtils.mc.playerController.sendUseItem((EntityPlayer)InvUtils.mc.thePlayer, (World)InvUtils.mc.theWorld, InvUtils.mc.thePlayer.inventory.getCurrentItem());
    }

    public static ItemStack getCurrentItem() {
        return InvUtils.mc.thePlayer.getCurrentEquippedItem() == null ? new ItemStack(Blocks.air) : InvUtils.mc.thePlayer.getCurrentEquippedItem();
    }

    public static ItemStack getItemBySlot(int slot) {
        return InvUtils.mc.thePlayer.inventory.mainInventory[slot] == null ? new ItemStack(Blocks.air) : InvUtils.mc.thePlayer.inventory.mainInventory[slot];
    }

    public static List<ItemStack> getHotbarContent() {
        ArrayList<ItemStack> result = new ArrayList<ItemStack>();
        result.addAll(Arrays.asList(InvUtils.mc.thePlayer.inventory.mainInventory).subList(0, 9));
        return result;
    }

    public static List<ItemStack> getAllInventoryContent() {
        ArrayList<ItemStack> result = new ArrayList<ItemStack>();
        result.addAll(Arrays.asList(InvUtils.mc.thePlayer.inventory.mainInventory).subList(0, 35));
        int i = 0;
        while (i < 4) {
            result.add(InvUtils.mc.thePlayer.inventory.armorItemInSlot(i));
            ++i;
        }
        return result;
    }

    public static List<ItemStack> getInventoryContent() {
        ArrayList<ItemStack> result = new ArrayList<ItemStack>();
        result.addAll(Arrays.asList(InvUtils.mc.thePlayer.inventory.mainInventory).subList(9, 35));
        return result;
    }

    public static int getEmptySlotInHotbar() {
        int i = 0;
        while (i < 9) {
            if (InvUtils.mc.thePlayer.inventory.mainInventory[i] == null) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static float getArmorScore(ItemStack itemStack) {
        if (itemStack == null) return -1.0f;
        if (!(itemStack.getItem() instanceof ItemArmor)) {
            return -1.0f;
        }
        ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
        float score = 0.0f;
        score += (float)itemArmor.damageReduceAmount;
        if (EnchantmentHelper.getEnchantments((ItemStack)itemStack).size() <= 0) {
            score = (float)((double)score - 0.1);
        }
        int protection = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.protection.effectId, (ItemStack)itemStack);
        score = (float)((double)score + (double)protection * 0.2);
        return score;
    }

    public static boolean hasWeapon() {
        if (InvUtils.mc.thePlayer.inventory.getCurrentItem() == null) return InvUtils.mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemAxe || InvUtils.mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
        return false;
    }

    public static boolean isHeldingSword() {
        return InvUtils.mc.thePlayer.getHeldItem() != null && InvUtils.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public static void getBestPickaxe() {
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && InvUtils.isBestPickaxe(is = InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack()) && pickaxeSlot != i && !InvUtils.isBestWeapon(is)) {
                if (!InvUtils.mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getHasStack()) {
                    InvUtils.swap(i, pickaxeSlot - 36);
                } else if (!InvUtils.isBestPickaxe(InvUtils.mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack())) {
                    InvUtils.swap(i, pickaxeSlot - 36);
                }
            }
            ++i;
        }
    }

    public static void getBestShovel() {
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && InvUtils.isBestShovel(is = InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack()) && shovelSlot != i && !InvUtils.isBestWeapon(is)) {
                if (!InvUtils.mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getHasStack()) {
                    InvUtils.swap(i, shovelSlot - 36);
                } else if (!InvUtils.isBestShovel(InvUtils.mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack())) {
                    InvUtils.swap(i, shovelSlot - 36);
                }
            }
            ++i;
        }
    }

    public static void getBestAxe() {
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && InvUtils.isBestAxe(is = InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack()) && axeSlot != i && !InvUtils.isBestWeapon(is)) {
                if (!InvUtils.mc.thePlayer.inventoryContainer.getSlot(axeSlot).getHasStack()) {
                    InvUtils.swap(i, axeSlot - 36);
                } else if (!InvUtils.isBestAxe(InvUtils.mc.thePlayer.inventoryContainer.getSlot(axeSlot).getStack())) {
                    InvUtils.swap(i, axeSlot - 36);
                }
            }
            ++i;
        }
    }

    public static boolean isBestPickaxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemPickaxe)) {
            return false;
        }
        float value = InvUtils.getToolEffect(stack);
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && InvUtils.getToolEffect(is = InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > value && is.getItem() instanceof ItemPickaxe) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static boolean isBestShovel(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemSpade)) {
            return false;
        }
        float value = InvUtils.getToolEffect(stack);
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && InvUtils.getToolEffect(is = InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > value && is.getItem() instanceof ItemSpade) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static boolean isBestAxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemAxe)) {
            return false;
        }
        float value = InvUtils.getToolEffect(stack);
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && InvUtils.getToolEffect(is = InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > value && is.getItem() instanceof ItemAxe && !InvUtils.isBestWeapon(stack)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static float getToolEffect(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemTool)) {
            return 0.0f;
        }
        String name = item.getUnlocalizedName();
        ItemTool tool = (ItemTool)item;
        float value = 1.0f;
        if (item instanceof ItemPickaxe) {
            value = tool.getStrVsBlock(stack, Blocks.stone);
            if (name.toLowerCase().contains("gold")) {
                value -= 5.0f;
            }
        } else if (item instanceof ItemSpade) {
            value = tool.getStrVsBlock(stack, Blocks.dirt);
            if (name.toLowerCase().contains("gold")) {
                value -= 5.0f;
            }
        } else {
            if (!(item instanceof ItemAxe)) return 1.0f;
            value = tool.getStrVsBlock(stack, Blocks.log);
            if (name.toLowerCase().contains("gold")) {
                value -= 5.0f;
            }
        }
        value = (float)((double)value + (double)EnchantmentHelper.getEnchantmentLevel((int)Enchantment.efficiency.effectId, (ItemStack)stack) * 0.0075);
        value = (float)((double)value + (double)EnchantmentHelper.getEnchantmentLevel((int)Enchantment.unbreaking.effectId, (ItemStack)stack) / 100.0);
        return value;
    }

    public static boolean isBestWeapon(ItemStack stack) {
        float damage = InvUtils.getDamage(stack);
        int i = 9;
        while (true) {
            ItemStack is;
            if (i >= 45) {
                if (!(stack.getItem() instanceof ItemSword)) return false;
                return true;
            }
            if (InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && InvUtils.getDamage(is = InvUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > damage && is.getItem() instanceof ItemSword) {
                return false;
            }
            ++i;
        }
    }

    public static float getDamage(ItemStack stack) {
        float damage = 0.0f;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            ItemTool tool = (ItemTool)item;
            damage += tool.damageVsEntity;
        }
        if (!(item instanceof ItemSword)) return damage += (float)EnchantmentHelper.getEnchantmentLevel((int)Enchantment.sharpness.effectId, (ItemStack)stack) * 1.25f + (float)EnchantmentHelper.getEnchantmentLevel((int)Enchantment.fireAspect.effectId, (ItemStack)stack) * 0.01f;
        ItemSword sword = (ItemSword)item;
        damage += sword.getDamageVsEntity();
        return damage += (float)EnchantmentHelper.getEnchantmentLevel((int)Enchantment.sharpness.effectId, (ItemStack)stack) * 1.25f + (float)EnchantmentHelper.getEnchantmentLevel((int)Enchantment.fireAspect.effectId, (ItemStack)stack) * 0.01f;
    }

    public static boolean isItemEmpty(Item item) {
        return item == null || Item.getIdFromItem((Item)item) == 0;
    }
}
