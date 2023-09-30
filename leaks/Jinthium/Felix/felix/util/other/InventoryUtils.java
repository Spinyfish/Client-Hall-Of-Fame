package felix.util.other;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public final class InventoryUtils {

    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int ONLY_HOT_BAR_BEGIN = 36;

    public static final int END = 45;

    private InventoryUtils() {
    }

    public static int findInHotBar(final Predicate<ItemStack> cond) {
        for (int i = 36; i < 45; i++) {
            final ItemStack stack = getStackInSlot(i);

            if (cond.test(stack))
                return i - 36;
        }

        return -1;
    }

    public static ItemStack getStackInSlot(int index) {
        return PlayerUtil.getLocalPlayer().inventoryContainer.getSlot(index).getStack();
    }

    public static boolean isInventoryEmpty(final IInventory inventory, final boolean archery) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (InventoryUtils.isValid(inventory.getStackInSlot(i), archery))
                return false;
        }

        return true;
    }

    public static void openInventory() {
        Minecraft.getMinecraft().getNetHandler().addToSendQueueNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
    }

    public static void closeInventory() {
        Minecraft.getMinecraft().getNetHandler().addToSendQueueNoEvent(new C0DPacketCloseWindow(PlayerUtil.getLocalPlayer().inventoryContainer.windowId));
    }

    public static int getDepthStriderLevel() {
        return EnchantmentHelper.getDepthStriderModifier(PlayerUtil.getLocalPlayer());
    }

    /**
     * @param slotId             The inventory slot you are clicking.
     *                           Armor slots:
     *                           Helmet is 5 and chest plate is 8
     *                           First slot of inventory is 9 (top left)
     *                           Last slot of inventory is 44 (bottom right)
     * @param mouseButtonClicked Hot bar slot
     * @param mode               The type of click
     */
    public static void windowClick(int windowId, int slotId, int mouseButtonClicked, ClickType mode) {
        Minecraft.getMinecraft().playerController.windowClick(windowId, slotId, mouseButtonClicked, mode.ordinal(), PlayerUtil.getLocalPlayer());
    }

    public static void equipArmor(int slot) {
//        if (slot >= 36) {
//
//        } else {
            InventoryUtils.windowClick(slot, 0, InventoryUtils.ClickType.SHIFT_CLICK);
//        }
    }

    public static void windowClick(int slotId, int mouseButtonClicked, ClickType mode) {
        Minecraft.getMinecraft().playerController.windowClick(PlayerUtil.getLocalPlayer().inventoryContainer.windowId, slotId,
                mouseButtonClicked, mode.ordinal(), PlayerUtil.getLocalPlayer());
    }

    public static double getDamageReduction(ItemStack stack) {
        double reduction = 0.0;

        ItemArmor armor = (ItemArmor) stack.getItem();

        reduction += armor.damageReduceAmount;

        if (stack.isItemEnchanted())
            reduction += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.25D;

        return reduction;
    }

    public static boolean isValid(ItemStack stack, boolean archery) {
        if (stack == null) return false;
        if (stack.getItem() instanceof ItemBlock) {
            return isGoodBlockStack(stack);
        } else if (stack.getItem() instanceof ItemSword) {
            return isBestSword(stack);
        } else if (stack.getItem() instanceof ItemTool) {
            return isBestTool(stack);
        } else if (stack.getItem() instanceof ItemArmor) {
            return isBestArmor(stack);
        } else if (stack.getItem() instanceof ItemPotion) {
            return isBuffPotion(stack);
        } else if (stack.getItem() instanceof ItemFood) {
            return isGoodFood(stack);
        } else if (stack.getItem() instanceof ItemBow && archery) {
            return isBestBow(stack);
        } else if (archery && stack.getItem().getUnlocalizedName().equals("item.arrow")) {
            return true;
        } else if (stack.getItem() instanceof ItemEnderPearl) {
            return true;
        } else return isGoodItem(stack);
    }

    public static boolean isBestBow(ItemStack itemStack) {
        double bestBowDmg = -1.0D;
        ItemStack bestBow = null;

        for (int i = InventoryUtils.EXCLUDE_ARMOR_BEGIN; i < InventoryUtils.END; i++) {
            final ItemStack stack = getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBow) {
                double damage = getBowDamage(stack);

                if (damage > bestBowDmg) {
                    bestBow = stack;
                    bestBowDmg = damage;
                }
            }
        }

        return itemStack == bestBow || getBowDamage(itemStack) > bestBowDmg;
    }

    public static double getBowDamage(ItemStack stack) {
        double damage = 0.0D;

        if (stack.getItem() instanceof ItemBow && stack.isItemEnchanted())
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

        return damage;
    }

    public static boolean isGoodItem(ItemStack stack) {
        final Item item = stack.getItem();
        if (item instanceof ItemBucket)
            if (((ItemBucket) item).isFull != Blocks.flowing_water)
                return false;

        return !(item instanceof ItemExpBottle) && !(item instanceof ItemFishingRod) &&
                !(item instanceof ItemEgg) && !(item instanceof ItemSnowball) &&
                !(item instanceof ItemSkull) && !(item instanceof ItemBucket);
    }

    public static boolean isBuffPotion(ItemStack stack) {
        ItemPotion potion = (ItemPotion) stack.getItem();
        List<PotionEffect> effects = potion.getEffects(stack);
        for (PotionEffect effect : effects)
            if (Potion.potionTypes[effect.getPotionID()].isBadEffect())
                return false;

        return true;
    }

    public static boolean isGoodFood(ItemStack stack) {
        ItemFood food = (ItemFood) stack.getItem();

        if (food instanceof ItemAppleGold)
            return true;

        return food.getHealAmount(stack) >= 4 && food.getSaturationModifier(stack) >= 0.3F;
    }

    public static boolean isBestSword(ItemStack itemStack) {
        double damage = 0.0;
        ItemStack bestStack = null;

        for (int i = InventoryUtils.EXCLUDE_ARMOR_BEGIN; i < InventoryUtils.END; i++) {
            final ItemStack stack = getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof ItemSword) {
                double newDamage = getItemDamage(stack);

                if (newDamage > damage) {
                    damage = newDamage;
                    bestStack = stack;
                }
            }
        }

        return bestStack == itemStack || getItemDamage(itemStack) > damage;
    }

    /**
     * @param stack The tool
     * @return Returns an arbitrary value representing a tool,
     * 0 = Pickaxe,
     * 1 = Axe,
     * 2 = Spade
     */
    public static int getToolType(ItemStack stack) {
        ItemTool tool = (ItemTool) stack.getItem();
        if (tool instanceof ItemPickaxe)
            return 0;
        else if (tool instanceof ItemAxe)
            return 1;
        else if (tool instanceof ItemSpade)
            return 2;
        else
            return -1;
    }

    public static boolean isBestTool(ItemStack itemStack) {
        final int type = getToolType(itemStack);

        Tool bestTool = new Tool(-1, -1, null);

        for (int i = InventoryUtils.EXCLUDE_ARMOR_BEGIN; i < InventoryUtils.END; i++) {
            final ItemStack stack = getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof ItemTool && type == getToolType(stack)) {
                double efficiency = getToolEfficiency(stack);
                if (efficiency > bestTool.getEfficiency())
                    bestTool = new Tool(i, efficiency, stack);
            }
        }

        return bestTool.getStack() == itemStack ||
                getToolEfficiency(itemStack) > bestTool.getEfficiency();
    }

    public static float getToolEfficiency(ItemStack itemStack) {
        ItemTool tool = (ItemTool) itemStack.getItem();

        float efficiency = tool.getEfficiencyOnProperMaterial();

        int lvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);

        if (efficiency > 1.0F && lvl > 0)
            efficiency += lvl * lvl + 1;

        return efficiency;
    }

    public static boolean isBestArmor(ItemStack itemStack) {
        ItemArmor itemArmor = (ItemArmor) itemStack.getItem();
        double reduction = 0.0;
        ItemStack bestStack = null;


        for (int i = InventoryUtils.INCLUDE_ARMOR_BEGIN; i < InventoryUtils.END; i++) {
            final ItemStack stack = getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor stackArmor = (ItemArmor) stack.getItem();
                if (stackArmor.armorType == itemArmor.armorType) {
                    double newReduction = getDamageReduction(stack);

                    if (newReduction > reduction) {
                        reduction = newReduction;
                        bestStack = stack;
                    }
                }
            }
        }

        return bestStack == itemStack || getDamageReduction(itemStack) > reduction;
    }

    public static boolean isGoodBlockStack(ItemStack stack) {
        if (stack.stackSize < 1) return false;
        return isValidBlock(Block.getBlockFromItem(stack.getItem()), true);
    }

    public static boolean isValidBlock(Block block, boolean toPlace) {
        if (block instanceof BlockContainer)
            return false;
        if (toPlace) {
            return !(block instanceof BlockFalling) && block.isFullBlock() && block.isFullCube();
        } else {
            final Material material = block.getMaterial();
            return !material.isReplaceable() && !material.isLiquid();
        }
    }

    public static double getItemDamage(ItemStack stack) {
        double damage = 0.0;

        final Multimap<String, AttributeModifier> attributeModifierMap = stack.getAttributeModifiers();

        for (String attributeName : attributeModifierMap.keySet()) {
            if (attributeName.equals("generic.attackDamage")) {
                Iterator<AttributeModifier> attributeModifiers = attributeModifierMap.get(attributeName).iterator();
                if (attributeModifiers.hasNext())
                    damage += attributeModifiers.next().getAmount();
                break;
            }
        }

        if (stack.isItemEnchanted()) {
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25;
        }

        return damage;
    }

    public enum ClickType {
        // if mouseButtonClicked is 0 `DROP_ITEM` will drop 1
        // item from the stack else if it is 1 it will drop the entire stack
        CLICK, SHIFT_CLICK, SWAP_WITH_HOT_BAR_SLOT, PLACEHOLDER, DROP_ITEM
    }

    private static class Tool {
        private final int slot;
        private final double efficiency;
        private final ItemStack stack;

        public Tool(int slot, double efficiency, ItemStack stack) {
            this.slot = slot;
            this.efficiency = efficiency;
            this.stack = stack;
        }

        public int getSlot() {
            return slot;
        }

        public double getEfficiency() {
            return efficiency;
        }

        public ItemStack getStack() {
            return stack;
        }
    }
}
