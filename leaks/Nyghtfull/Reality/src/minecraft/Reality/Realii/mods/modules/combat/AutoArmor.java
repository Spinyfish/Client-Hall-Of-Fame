package Reality.Realii.mods.modules.combat;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;

import java.util.Random;

public class AutoArmor
        extends Module {
    private Numbers<Number> delay = new Numbers<Number>("Delay", "delay", 50.0, 0.0, 1000.0, 10.0);
    private Option invOnly = new Option("InvOnly", true);
    private TimerUtil timer = new TimerUtil();
    private int[] boots = new int[]{313, 309, 317, 305, 301};
    private int[] chestplate = new int[]{311, 307, 315, 303, 299};
    private int[] helmet = new int[]{310, 306, 314, 302, 298};
    private int[] leggings = new int[]{312, 308, 316, 304, 300};
    private int slot = 5;
    private double enchantmentValue = -1.0;
    private double protectionValue;
    private int item = -1;

    public AutoArmor() {
        super("AutoArmor", "Auto armor", ModuleType.Combat);
        this.addValues(this.delay, invOnly);
    }

    @EventHandler
    private void onPre(EventPreUpdate e) {
        if (((boolean) invOnly.getValue()) && !(mc.currentScreen instanceof GuiInventory))
            return;
        if (e.getType() == 0) {
            if (this.mc.thePlayer.capabilities.isCreativeMode || this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer.windowId != 0) {
                return;
            }
            if (this.timer.delay(this.delay.getValue().intValue() + new Random().nextInt(4))) {
                this.enchantmentValue = -1.0;
                this.item = -1;
                int i = 9;
                while (i < 45) {
                    if (this.mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null && this.canEquip(this.mc.thePlayer.inventoryContainer.getSlot(i).getStack()) != -1 && this.canEquip(this.mc.thePlayer.inventoryContainer.getSlot(i).getStack()) == this.slot) {
                        this.change(this.slot, i);
                    }
                    ++i;
                }
                if (this.item != -1) {
                    if (this.mc.thePlayer.inventoryContainer.getSlot(this.item).getStack() != null) {
                        this.mc.playerController.windowClick(0, this.slot, 0, 1, this.mc.thePlayer);
                    }
                    this.mc.playerController.windowClick(0, this.item, 0, 1, this.mc.thePlayer);
                }
                this.slot = this.slot == 8 ? 5 : ++this.slot;
                this.timer.reset();
            }
        }
    }

    private int canEquip(ItemStack stack) {
        int id;
        int[] arrn = this.boots;
        int n = arrn.length;
        int n2 = 0;
        while (n2 < n) {
            id = arrn[n2];
            stack.getItem();
            if (Item.getIdFromItem(stack.getItem()) == id) {
                return 8;
            }
            ++n2;
        }
        arrn = this.leggings;
        n = arrn.length;
        n2 = 0;
        while (n2 < n) {
            id = arrn[n2];
            stack.getItem();
            if (Item.getIdFromItem(stack.getItem()) == id) {
                return 7;
            }
            ++n2;
        }
        arrn = this.chestplate;
        n = arrn.length;
        n2 = 0;
        while (n2 < n) {
            id = arrn[n2];
            stack.getItem();
            if (Item.getIdFromItem(stack.getItem()) == id) {
                return 6;
            }
            ++n2;
        }
        arrn = this.helmet;
        n = arrn.length;
        n2 = 0;
        while (n2 < n) {
            id = arrn[n2];
            stack.getItem();
            if (Item.getIdFromItem(stack.getItem()) == id) {
                return 5;
            }
            ++n2;
        }
        return -1;
    }

    private void change(int numy, int i) {
        this.protectionValue = this.enchantmentValue == -1.0 ? (this.mc.thePlayer.inventoryContainer.getSlot(numy).getStack() != null ? this.getProtValue(this.mc.thePlayer.inventoryContainer.getSlot(numy).getStack()) : this.enchantmentValue) : this.enchantmentValue;
        if (this.protectionValue <= this.getProtValue(this.mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
            if (this.protectionValue == this.getProtValue(this.mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                int newD;
                int currentD = this.mc.thePlayer.inventoryContainer.getSlot(numy).getStack() != null ? this.mc.thePlayer.inventoryContainer.getSlot(numy).getStack().getItemDamage() : 999;
                int n = newD = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null ? this.mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItemDamage() : 500;
                if (newD <= currentD && newD != currentD) {
                    this.item = i;
                    this.enchantmentValue = this.getProtValue(this.mc.thePlayer.inventoryContainer.getSlot(i).getStack());
                }
            } else {
                this.item = i;
                this.enchantmentValue = this.getProtValue(this.mc.thePlayer.inventoryContainer.getSlot(i).getStack());
            }
        }
    }

    private double getProtValue(ItemStack stack) {
        if (stack != null) {
        	if(stack.getItem() instanceof ItemSkull)
        		return 0;
            return ((ItemArmor) stack.getItem()).damageReduceAmount + ((100 - ((ItemArmor) stack.getItem()).damageReduceAmount * 4) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 4) * 0.0075;
        }
        return 0.0;
    }
}

