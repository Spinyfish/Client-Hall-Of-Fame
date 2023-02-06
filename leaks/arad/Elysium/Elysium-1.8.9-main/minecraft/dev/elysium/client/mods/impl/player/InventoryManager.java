package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventUpdate;
import dev.elysium.client.mods.impl.combat.Killaura;
import dev.elysium.client.utils.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

public class InventoryManager extends Mod {

    public BooleanSetting autoarmor = new BooleanSetting("Auto Armor",true,this);

    public ModeSetting mode = new ModeSetting("Mode",this,"Inventory Only","Spoof");

    public NumberSetting delay = new NumberSetting("Delay",0,2000,200,1,this);

    public BooleanSetting move = new BooleanSetting("Only When Still",false, this);
    public BooleanSetting attacking = new BooleanSetting("While Attacking",true, this);

    public Timer timer = new Timer();

    public boolean inventoryOpen;
    public InventoryManager() {
        super("InventoryManager","Manages inventory actions", Category.PLAYER);
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        if(!(mc.currentScreen instanceof GuiInventory) && mode.getMode() == "Inventory Only" || mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory) || mc.thePlayer.isMoving() && move.isEnabled()) return;

        if(!inventoryOpen && mc.currentScreen instanceof GuiInventory) inventoryOpen = true;

        if(timer.hasTimeElapsed((long) delay.getValue(), false) && (attacking.isEnabled() || ((Killaura)Elysium.getInstance().getModManager().getModByName("Killaura")).target == null)) {
            if(doAutoArmor() == -1 && inventoryOpen) {
                closeInventory();
            }

            if(doInventoryManager() == -1 && inventoryOpen) {
                closeInventory();
            }
        }
    }

    public int doAutoArmor() {
        for(int i = 0; i < 4; i++) {
            int slot = getBestArmourForType(i);
            if(slot != -1) {
                openInventory();

                if(mc.thePlayer.inventory.armorItemInSlot(3-i) != null) {
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i+5, 1, 4, mc.thePlayer);
                    timer.reset();
                    return 1;
                }

                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot <=  8 ? slot + 36 : slot, 0, 1, mc.thePlayer);
                timer.reset();
                return 1;
            }
        }
        return -1;
    }

    public int doInventoryManager() {
        return -1;
    }

    public void closeInventory() {
        if(inventoryOpen && !(mc.currentScreen instanceof GuiInventory)) {
            inventoryOpen = false;
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
        }
    }

    public void openInventory() {
        if (!inventoryOpen) {
            inventoryOpen = true;
            if (!(mc.currentScreen instanceof GuiInventory)) {
                mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            }
        }
    }

    public double getArmourValue(int slot) {
        ItemArmor armour = null;
        if(mc.thePlayer.inventory.getStackInSlot(slot) != null && mc.thePlayer.inventory.getStackInSlot(slot).getItem() != null && mc.thePlayer.inventory.getStackInSlot(slot).getItem() instanceof ItemArmor) {
            armour = (ItemArmor) mc.thePlayer.inventory.getStackInSlot(slot).getItem();
        } else  return -1;
        double armourvalue = armour.damageReduceAmount;
        armourvalue += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, mc.thePlayer.inventory.getStackInSlot(slot));
        armourvalue += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, mc.thePlayer.inventory.getStackInSlot(slot))*0.1;
        armourvalue += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, mc.thePlayer.inventory.getStackInSlot(slot))*0.1;
        armourvalue += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, mc.thePlayer.inventory.getStackInSlot(slot))*0.1;
        return armourvalue;
    }

    public int getBestArmourForType(int type) {
        int bestslot = -1;
        double damagereduce = 0;
        for(int x = 0; x < 36; x++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(x);

            if(stack != null && stack.getItem() != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor armour = (ItemArmor)stack.getItem();
                double strength = getArmourValue(x);

                if(armour.armorType != type) continue;

                if(strength > damagereduce) {
                    damagereduce = strength;
                    bestslot = x;
                }
            }
        }
        if(getArmourValue(39-type) >= damagereduce) return -1;
        return bestslot;
    }
}
