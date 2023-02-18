/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockContainer
 *  net.minecraft.block.BlockFalling
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemAppleGold
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemEnderPearl
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemPotion
 *  net.minecraft.item.ItemSpade
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  vip.astroline.client.service.module.impl.player.InvManager
 *  vip.astroline.client.storage.utils.other.InventoryUtils$1
 *  vip.astroline.client.storage.utils.other.InventoryUtils$BlockAction
 *  vip.astroline.client.storage.utils.other.InventoryUtils$ClickType
 *  vip.astroline.client.storage.utils.other.InventoryUtils$Tool
 */
package vip.astroline.client.storage.utils.other;

import com.google.common.collect.Multimap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import vip.astroline.client.service.module.impl.player.InvManager;
import vip.astroline.client.storage.utils.other.InventoryUtils;

public class InventoryUtils {
    public static final int INCLUDE_ARMOR_BEGIN;
    public static final int EXCLUDE_ARMOR_BEGIN;
    public static final int ONLY_HOT_BAR_BEGIN;
    public static final int END;
    private static InvManager inventoryManager;

    public static int findSlotMatching(EntityPlayerSP player, Predicate<ItemStack> cond) {
        int i = 44;
        while (i >= 9) {
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (cond.test(stack)) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public static boolean hasFreeSlots(EntityPlayerSP player) {
        int i = 9;
        while (i < 45) {
            if (!player.inventoryContainer.getSlot(i).getHasStack()) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public static boolean isValidStack(EntityPlayerSP player, ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof ItemSword) {
            return InventoryUtils.isBestSword(player, stack);
        }
        if (item instanceof ItemArmor) {
            return InventoryUtils.isBestArmor(player, stack);
        }
        if (item instanceof ItemTool) {
            return InventoryUtils.isBestTool(player, stack);
        }
        if (item instanceof ItemBow) {
            return InventoryUtils.isBestBow(player, stack);
        }
        if (item instanceof ItemFood) {
            return InventoryUtils.isGoodFood(stack);
        }
        if (item instanceof ItemBlock) {
            return InventoryUtils.isStackValidToPlace(stack);
        }
        if (!(item instanceof ItemPotion)) return InventoryUtils.isGoodItem(item);
        return InventoryUtils.isBuffPotion(stack);
    }

    public static boolean isGoodItem(Item item) {
        return item instanceof ItemEnderPearl || item == Items.arrow;
    }

    public static boolean isBestSword(EntityPlayerSP player, ItemStack itemStack) {
        double damage = 0.0;
        ItemStack bestStack = null;
        for (int i = 9; i < 45; ++i) {
            double newDamage;
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack == null || !(stack.getItem() instanceof ItemSword) || !((newDamage = InventoryUtils.getItemDamage(stack)) > damage)) continue;
            damage = newDamage;
            bestStack = stack;
        }
        return bestStack == itemStack || InventoryUtils.getItemDamage(itemStack) > damage;
    }

    public static boolean isBestArmor(EntityPlayerSP player, ItemStack itemStack) {
        ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
        double reduction = 0.0;
        ItemStack bestStack = null;
        for (int i = 5; i < 45; ++i) {
            double newReduction;
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack == null || !(stack.getItem() instanceof ItemArmor)) continue;
            ItemArmor stackArmor = (ItemArmor)stack.getItem();
            if (stackArmor.armorType != itemArmor.armorType || !((newReduction = InventoryUtils.getDamageReduction(stack)) > reduction)) continue;
            reduction = newReduction;
            bestStack = stack;
        }
        return bestStack == itemStack || InventoryUtils.getDamageReduction(itemStack) > reduction;
    }

    public static int getToolType(ItemStack stack) {
        ItemTool tool = (ItemTool)stack.getItem();
        if (tool instanceof ItemPickaxe) {
            return 0;
        }
        if (tool instanceof ItemAxe) {
            return 1;
        }
        if (!(tool instanceof ItemSpade)) return -1;
        return 2;
    }

    public static boolean isBestTool(EntityPlayerSP player, ItemStack itemStack) {
        int type = InventoryUtils.getToolType(itemStack);
        Tool bestTool = new Tool(-1, -1.0, null);
        for (int i = 9; i < 45; ++i) {
            double efficiency;
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack == null || !(stack.getItem() instanceof ItemTool) || type != InventoryUtils.getToolType(stack) || !((efficiency = (double)InventoryUtils.getToolEfficiency(stack)) > bestTool.getEfficiency())) continue;
            bestTool = new Tool(i, efficiency, stack);
        }
        return bestTool.getStack() == itemStack || (double)InventoryUtils.getToolEfficiency(itemStack) > bestTool.getEfficiency();
    }

    public static boolean isBestBow(EntityPlayerSP player, ItemStack itemStack) {
        double bestBowDmg = -1.0;
        ItemStack bestBow = null;
        for (int i = 9; i < 45; ++i) {
            double damage;
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack == null || !(stack.getItem() instanceof ItemBow) || !((damage = InventoryUtils.getBowDamage(stack)) > bestBowDmg)) continue;
            bestBow = stack;
            bestBowDmg = damage;
        }
        return itemStack == bestBow || InventoryUtils.getBowDamage(itemStack) > bestBowDmg;
    }

    public static double getDamageReduction(ItemStack stack) {
        double reduction = 0.0;
        ItemArmor armor = (ItemArmor)stack.getItem();
        if (!stack.isItemEnchanted()) return reduction += (double)armor.damageReduceAmount;
        reduction += (double)EnchantmentHelper.getEnchantmentLevel((int)Enchantment.protection.effectId, (ItemStack)stack) * 0.25;
        return reduction += (double)armor.damageReduceAmount;
    }

    public static boolean isBuffPotion(ItemStack stack) {
        PotionEffect effect;
        ItemPotion potion = (ItemPotion)stack.getItem();
        List effects = potion.getEffects(stack);
        Iterator iterator = effects.iterator();
        do {
            if (!iterator.hasNext()) return true;
        } while (!Potion.potionTypes[(effect = (PotionEffect)iterator.next()).getPotionID()].isBadEffect());
        return false;
    }

    public static double getBowDamage(ItemStack stack) {
        double damage = 0.0;
        if (!(stack.getItem() instanceof ItemBow)) return damage;
        if (!stack.isItemEnchanted()) return damage;
        damage += (double)EnchantmentHelper.getEnchantmentLevel((int)Enchantment.power.effectId, (ItemStack)stack);
        return damage;
    }

    public static boolean isGoodFood(ItemStack stack) {
        ItemFood food = (ItemFood)stack.getItem();
        if (!(food instanceof ItemAppleGold)) return food.getHealAmount(stack) >= 4 && food.getSaturationModifier(stack) >= 0.3f;
        return true;
    }

    public static float getToolEfficiency(ItemStack itemStack) {
        ItemTool tool = (ItemTool)itemStack.getItem();
        float efficiency = tool.getEfficiencyOnProperMaterial();
        int lvl = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.efficiency.effectId, (ItemStack)itemStack);
        if (!(efficiency > 1.0f)) return efficiency;
        if (lvl <= 0) return efficiency;
        efficiency += (float)(lvl * lvl + 1);
        return efficiency;
    }

    public static double getItemDamage(ItemStack stack) {
        double damage = 0.0;
        Multimap attributeModifierMap = stack.getAttributeModifiers();
        for (String attributeName : attributeModifierMap.keySet()) {
            if (!attributeName.equals("generic.attackDamage")) continue;
            Iterator attributeModifiers = attributeModifierMap.get(attributeName).iterator();
            if (!attributeModifiers.hasNext()) break;
            damage += ((AttributeModifier)attributeModifiers.next()).getAmount();
            break;
        }
        if (!stack.isItemEnchanted()) return damage;
        damage += (double)EnchantmentHelper.getEnchantmentLevel((int)Enchantment.fireAspect.effectId, (ItemStack)stack);
        damage += (double)EnchantmentHelper.getEnchantmentLevel((int)Enchantment.sharpness.effectId, (ItemStack)stack) * 1.25;
        return damage;
    }

    public static void windowClick(Minecraft mc, int windowId, int slotId, int mouseButtonClicked, ClickType mode) {
        mc.playerController.windowClick(windowId, slotId, mouseButtonClicked, mode.ordinal(), (EntityPlayer)mc.thePlayer);
    }

    public static void windowClick(Minecraft mc, int slotId, int mouseButtonClicked, ClickType mode) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotId, mouseButtonClicked, mode.ordinal(), (EntityPlayer)mc.thePlayer);
    }

    public static boolean isStackValidToPlace(ItemStack stack) {
        return stack.stackSize >= 1 && InventoryUtils.validateBlock(Block.getBlockFromItem((Item)stack.getItem()), BlockAction.PLACE);
    }

    public static boolean validateBlock(Block block, BlockAction action) {
        if (block instanceof BlockContainer) {
            return false;
        }
        Material material = block.getMaterial();
        switch (1.$SwitchMap$vip$astroline$client$storage$utils$other$InventoryUtils$BlockAction[action.ordinal()]) {
            case 1: {
                return !(block instanceof BlockFalling) && block.isFullBlock() && block.isFullCube();
            }
            case 2: {
                return material.isReplaceable();
            }
            case 3: {
                return !(block instanceof BlockAir);
            }
        }
        return true;
    }

    static {
        EXCLUDE_ARMOR_BEGIN = 9;
        INCLUDE_ARMOR_BEGIN = 5;
        ONLY_HOT_BAR_BEGIN = 36;
        END = 45;
    }
}
