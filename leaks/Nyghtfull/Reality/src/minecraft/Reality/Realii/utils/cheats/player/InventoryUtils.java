/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.utils.cheats.player;

import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.util.DamageSource;

public class InventoryUtils {
    public static Minecraft mc = Minecraft.getMinecraft();

    public void dropSlot(int slot) {
        int windowId = new GuiInventory((EntityPlayer)InventoryUtils.mc.thePlayer).inventorySlots.windowId;
        InventoryUtils.mc.playerController.windowClick(windowId, slot, 1, 4, InventoryUtils.mc.thePlayer);
    }

    public static void updateInventory() {
        int index = 0;
        while (index < 44) {
            try {
                int offset = index < 9 ? 36 : 0;
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(index + offset, Minecraft.getMinecraft().thePlayer.inventory.mainInventory[index]));
            }
            catch (Exception offset) {
                // empty catch block
            }
            ++index;
        }
    }

    public static ItemStack getStackInSlot(int slot) {
        return InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static boolean isBestArmorOfTypeInInv(ItemStack is) {
        try {
            int otherProt;
            ItemStack stack;
            ItemArmor otherArmor;
            if (is == null) {
                return false;
            }
            if (is.getItem() == null) {
                return false;
            }
            if (is.getItem() != null && !(is.getItem() instanceof ItemArmor)) {
                return false;
            }
            ItemArmor ia = (ItemArmor)is.getItem();
            int prot = InventoryUtils.getArmorProt(is);
            int i = 0;
            while (i < 4) {
                stack = InventoryUtils.mc.thePlayer.inventory.armorInventory[i];
                if (stack != null) {
                    otherArmor = (ItemArmor)stack.getItem();
                    if (otherArmor.armorType == ia.armorType && (otherProt = InventoryUtils.getArmorProt(stack)) >= prot) {
                        return false;
                    }
                }
                ++i;
            }
            i = 0;
            while (i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory() - 4) {
                stack = InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemArmor) {
                    otherArmor = (ItemArmor)stack.getItem();
                    if (otherArmor.armorType == ia.armorType && otherArmor != ia && (otherProt = InventoryUtils.getArmorProt(stack)) >= prot) {
                        return false;
                    }
                }
                ++i;
            }
        }
        catch (Exception ia) {
            // empty catch block
        }
        return true;
    }

    public static boolean hotbarHas(Item item) {
        int index = 0;
        while (index <= 36) {
            ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
            if (stack != null && stack.getItem() == item) {
                return true;
            }
            ++index;
        }
        return false;
    }

    public static boolean hotbarHas(Item item, int slotID) {
        int index = 0;
        while (index <= 36) {
            ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
            if (stack != null && stack.getItem() == item && InventoryUtils.getSlotID(stack.getItem()) == slotID) {
                return true;
            }
            ++index;
        }
        return false;
    }

    public static int getSlotID(Item item) {
        int index = 0;
        while (index <= 36) {
            ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
            if (stack != null && stack.getItem() == item) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    public static ItemStack getItemBySlotID(int slotID) {
        int index = 0;
        while (index <= 36) {
            ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
            if (stack != null && InventoryUtils.getSlotID(stack.getItem()) == slotID) {
                return stack;
            }
            ++index;
        }
        return null;
    }

    public static int getArmorProt(ItemStack i) {
        int armorprot = -1;
        if (i != null && i.getItem() != null && i.getItem() instanceof ItemArmor) {
            armorprot = ((ItemArmor)i.getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(i)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{i}, DamageSource.generic);
        }
        return armorprot;
    }

    public static int getBestSwordSlotID(ItemStack item, double damage) {
        int index = 0;
        while (index <= 36) {
            ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
            if (stack != null && stack == item && InventoryUtils.getSwordDamage(stack) == InventoryUtils.getSwordDamage(item)) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    private static double getSwordDamage(ItemStack itemStack) {
        double damage = 0.0;
        Optional attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();
        if (attributeModifier.isPresent()) {
            damage = ((AttributeModifier)attributeModifier.get()).getAmount();
        }
        return damage += (double)EnchantmentHelper.func_152377_a(itemStack, EnumCreatureAttribute.UNDEFINED);
    }

    public boolean isBestChest(int slot) {
        if (InventoryUtils.getStackInSlot(slot) != null && InventoryUtils.getStackInSlot(slot).getItem() != null && InventoryUtils.getStackInSlot(slot).getItem() instanceof ItemArmor) {
            ItemArmor ia1;
            int slotProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot)}, DamageSource.generic);
            if (InventoryUtils.mc.thePlayer.inventory.armorInventory[2] != null) {
                ItemArmor ia = (ItemArmor)InventoryUtils.mc.thePlayer.inventory.armorInventory[2].getItem();
                ItemStack is = InventoryUtils.mc.thePlayer.inventory.armorInventory[2];
                ia1 = (ItemArmor)InventoryUtils.getStackInSlot(slot).getItem();
                int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{is}, DamageSource.generic);
                if (otherProtection > slotProtection || otherProtection == slotProtection) {
                    return false;
                }
            }
            int i = 0;
            while (i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory()) {
                if (InventoryUtils.getStackInSlot(i) != null && InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
                    int otherProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i)}, DamageSource.generic);
                    ia1 = (ItemArmor)InventoryUtils.getStackInSlot(slot).getItem();
                    ItemArmor ia2 = (ItemArmor)InventoryUtils.getStackInSlot(i).getItem();
                    if (ia1.armorType == 1 && ia2.armorType == 1 && otherProtection > slotProtection) {
                        return false;
                    }
                }
                ++i;
            }
        }
        return true;
    }

    public boolean isBestHelmet(int slot) {
        if (InventoryUtils.getStackInSlot(slot) != null && InventoryUtils.getStackInSlot(slot).getItem() != null && InventoryUtils.getStackInSlot(slot).getItem() instanceof ItemArmor) {
            ItemArmor ia1;
            int slotProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot)}, DamageSource.generic);
            if (InventoryUtils.mc.thePlayer.inventory.armorInventory[3] != null) {
                ItemArmor ia = (ItemArmor)InventoryUtils.mc.thePlayer.inventory.armorInventory[3].getItem();
                ItemStack is = InventoryUtils.mc.thePlayer.inventory.armorInventory[3];
                ia1 = (ItemArmor)InventoryUtils.getStackInSlot(slot).getItem();
                int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{is}, DamageSource.generic);
                if (otherProtection > slotProtection || otherProtection == slotProtection) {
                    return false;
                }
            }
            int i = 0;
            while (i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory()) {
                if (InventoryUtils.getStackInSlot(i) != null && InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
                    int otherProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i)}, DamageSource.generic);
                    ia1 = (ItemArmor)InventoryUtils.getStackInSlot(slot).getItem();
                    ItemArmor ia2 = (ItemArmor)InventoryUtils.getStackInSlot(i).getItem();
                    if (ia1.armorType == 0 && ia2.armorType == 0 && otherProtection > slotProtection) {
                        return false;
                    }
                }
                ++i;
            }
        }
        return true;
    }

    public boolean isBestLeggings(int slot) {
        if (InventoryUtils.getStackInSlot(slot) != null && InventoryUtils.getStackInSlot(slot).getItem() != null && InventoryUtils.getStackInSlot(slot).getItem() instanceof ItemArmor) {
            ItemArmor ia1;
            int slotProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot)}, DamageSource.generic);
            if (InventoryUtils.mc.thePlayer.inventory.armorInventory[1] != null) {
                ItemArmor ia = (ItemArmor)InventoryUtils.mc.thePlayer.inventory.armorInventory[1].getItem();
                ItemStack is = InventoryUtils.mc.thePlayer.inventory.armorInventory[1];
                ia1 = (ItemArmor)InventoryUtils.getStackInSlot(slot).getItem();
                int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{is}, DamageSource.generic);
                if (otherProtection > slotProtection || otherProtection == slotProtection) {
                    return false;
                }
            }
            int i = 0;
            while (i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory()) {
                if (InventoryUtils.getStackInSlot(i) != null && InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
                    int otherProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i)}, DamageSource.generic);
                    ia1 = (ItemArmor)InventoryUtils.getStackInSlot(slot).getItem();
                    ItemArmor ia2 = (ItemArmor)InventoryUtils.getStackInSlot(i).getItem();
                    if (ia1.armorType == 2 && ia2.armorType == 2 && otherProtection > slotProtection) {
                        return false;
                    }
                }
                ++i;
            }
        }
        return true;
    }

    public boolean isBestBoots(int slot) {
        if (InventoryUtils.getStackInSlot(slot) != null && InventoryUtils.getStackInSlot(slot).getItem() != null && InventoryUtils.getStackInSlot(slot).getItem() instanceof ItemArmor) {
            ItemArmor ia1;
            int slotProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot)}, DamageSource.generic);
            if (InventoryUtils.mc.thePlayer.inventory.armorInventory[0] != null) {
                ItemArmor ia = (ItemArmor)InventoryUtils.mc.thePlayer.inventory.armorInventory[0].getItem();
                ItemStack is = InventoryUtils.mc.thePlayer.inventory.armorInventory[0];
                ia1 = (ItemArmor)InventoryUtils.getStackInSlot(slot).getItem();
                int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{is}, DamageSource.generic);
                if (otherProtection > slotProtection || otherProtection == slotProtection) {
                    return false;
                }
            }
            int i = 0;
            while (i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory()) {
                if (InventoryUtils.getStackInSlot(i) != null && InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
                    int otherProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i)}, DamageSource.generic);
                    ia1 = (ItemArmor)InventoryUtils.getStackInSlot(slot).getItem();
                    ItemArmor ia2 = (ItemArmor)InventoryUtils.getStackInSlot(i).getItem();
                    if (ia1.armorType == 3 && ia2.armorType == 3 && otherProtection > slotProtection) {
                        return false;
                    }
                }
                ++i;
            }
        }
        return true;
    }

    public boolean isBestSword(int slotIn) {
        if (this.getBestWeapon() == slotIn) {
            return true;
        }
        return false;
    }

    public static int getItemType(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)itemStack.getItem();
            return armor.armorType;
        }
        return -1;
    }

    public static float getItemDamage(ItemStack itemStack) {
        Iterator iterator;
        Multimap multimap = itemStack.getAttributeModifiers();
        if (!multimap.isEmpty() && (iterator = multimap.entries().iterator()).hasNext()) {
            Entry entry = (Entry) iterator.next();
            AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
            double damage = attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2 ? attributeModifier.getAmount() : attributeModifier.getAmount() * 100.0;
            return attributeModifier.getAmount() > 1.0 ? 1.0f + (float)damage : 1.0f;
        }
        return 1.0f;
    }

    public boolean hasItemMoreTimes(int slotIn) {
        boolean has = false;
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        stacks.clear();
        int i = 0;
        while (i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory()) {
            if (!stacks.contains(InventoryUtils.getStackInSlot(i))) {
                stacks.add(InventoryUtils.getStackInSlot(i));
            } else if (InventoryUtils.getStackInSlot(i) == InventoryUtils.getStackInSlot(slotIn)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public int getBestWeaponInHotbar() {
        int originalSlot = InventoryUtils.mc.thePlayer.inventory.currentItem;
        int weaponSlot = -1;
        float weaponDamage = 1.0f;
        int slot = 0;
        while (slot < 9) {
            ItemStack itemStack = InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot);
            if (itemStack != null) {
                float damage = InventoryUtils.getItemDamage(itemStack);
                if ((damage += EnchantmentHelper.func_152377_a(itemStack, EnumCreatureAttribute.UNDEFINED)) > weaponDamage) {
                    weaponDamage = damage;
                    weaponSlot = slot;
                }
            }
            slot = (byte)(slot + 1);
        }
        if (weaponSlot != -1) {
            return weaponSlot;
        }
        return originalSlot;
    }

    public int getBestWeapon() {
        int originalSlot = InventoryUtils.mc.thePlayer.inventory.currentItem;
        int weaponSlot = -1;
        float weaponDamage = 1.0f;
        int slot = 0;
        while (slot < InventoryUtils.mc.thePlayer.inventory.getSizeInventory()) {
            ItemStack itemStack;
            if (InventoryUtils.getStackInSlot(slot) != null && (itemStack = InventoryUtils.getStackInSlot(slot)) != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemSword) {
                float damage = InventoryUtils.getItemDamage(itemStack);
                if ((damage += EnchantmentHelper.func_152377_a(itemStack, EnumCreatureAttribute.UNDEFINED)) > weaponDamage) {
                    weaponDamage = damage;
                    weaponSlot = slot;
                }
            }
            slot = (byte)(slot + 1);
        }
        if (weaponSlot != -1) {
            return weaponSlot;
        }
        return originalSlot;
    }

    public int getArmorProt(int i) {
        int armorprot = -1;
        if (InventoryUtils.getStackInSlot(i) != null && InventoryUtils.getStackInSlot(i).getItem() != null && InventoryUtils.getStackInSlot(i).getItem() instanceof ItemArmor) {
            armorprot = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(InventoryUtils.getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i)}, DamageSource.generic);
        }
        return armorprot;
    }

    public static int getFirstItem(Item i1) {
        int i = 0;
        while (i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory()) {
            if (InventoryUtils.getStackInSlot(i) != null && InventoryUtils.getStackInSlot(i).getItem() != null && InventoryUtils.getStackInSlot(i).getItem() == i1) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static boolean isBestSword(ItemStack itemSword, int slot) {
        if (itemSword != null && itemSword.getItem() instanceof ItemSword) {
            int i = 0;
            while (i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory()) {
                ItemStack iStack = InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i);
                if (iStack != null && iStack.getItem() instanceof ItemSword && InventoryUtils.getItemDamage(iStack) >= InventoryUtils.getItemDamage(itemSword) && slot != i) {
                    return false;
                }
                ++i;
            }
        }
        return true;
    }
}

