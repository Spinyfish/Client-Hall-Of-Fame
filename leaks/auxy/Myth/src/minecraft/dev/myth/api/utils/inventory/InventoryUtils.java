/**
 * @project Myth
 * @author CodeMan
 * @at 11.08.22, 18:06
 */
package dev.myth.api.utils.inventory;

import dev.myth.api.interfaces.IMethods;
import lombok.experimental.UtilityClass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@UtilityClass
public class InventoryUtils implements IMethods {

    private final UUID damageAttribID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

    public double getAttackDamage(ItemStack item) {
        AtomicReference<Double> damage = new AtomicReference<>((double) 0);

        item.getAttributeModifiers().values().forEach(attrib -> {
            double damageAttrib = attrib.getAmount();

            if (Objects.equals(attrib.getID(), damageAttribID)) {
                damageAttrib += EnchantmentHelper.getModifierForCreature(item, EnumCreatureAttribute.UNDEFINED);
            }

            if (attrib.getOperation() != 1 && attrib.getOperation() != 2) {
                damage.set(damageAttrib);
            } else {
                damage.set(damageAttrib * 100);
            }
        });

        return damage.get();
    }

    public int getSlot(Item item) {
        if(!MC.thePlayer.inventory.hasItem(item)) return -1;
        for(int i = 0; i < 9; i++) {
            ItemStack itemInSlot = MC.thePlayer.inventory.getStackInSlot(i);
            if(itemInSlot != null && itemInSlot.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public int canBow() {
        if(!MC.thePlayer.inventory.hasItem(Items.arrow)) return -1;
        return getSlot(Items.bow);
    }

    public float getToolEfficiency(ItemStack itemStack) {
        if (itemStack != null && itemStack.getItem() instanceof ItemTool) {
            ItemTool tool = (ItemTool) itemStack.getItem();
            float efficiency = tool.getToolMaterial().getEfficiencyOnProperMaterial();
            int lvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
            if (efficiency > 1.0f && lvl > 0) {
                efficiency += lvl * lvl + 1;
            }
            return efficiency;
        } else {
            return -1;
        }
    }

    public double getDamageReduction(ItemStack stack) {
        double reduction = 0.0;
        if(stack.getItem() instanceof ItemArmor){
            ItemArmor armor = (ItemArmor) stack.getItem();
            reduction += armor.damageReduceAmount;
            if (stack.isItemEnchanted()) {
                reduction += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.25;
            }
        }
        return reduction;
    }

    public void windowClick(int slotId, int mouseButtonClicked, ClickType mode) {
        MC.playerController.windowClick(MC.thePlayer.inventoryContainer.windowId, slotId, mouseButtonClicked, mode.ordinal(), MC.thePlayer);
    }

    public enum ClickType {
        CLICK, SHIFT_CLICK, SWAP_WITH_HOT_BAR_SLOT, MID_CLICK, DROP_ITEM;
    }
}
