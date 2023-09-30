package de.fanta.module.impl.player;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.fanta.events.Event;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.TimeUtil;

public class InvCleaner extends Module {

    private TimeUtil timer = new TimeUtil();
    private List<Integer> trash = new ArrayList<>();

    public InvCleaner() {
        super("InvCleaner", 0, Type.Player, Color.cyan);
        this.settings.add(new Setting("OpenInv", new CheckBox(true)));
        this.settings.add(new Setting("Delay", new Slider(1, 1000, 1, 100)));
    }

    public void onEnable() {
        timer.reset();
    }

    public void onDisable() {
        timer.reset();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventTick && e.isPre()) {
            if (((mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)
                  
                    && ((CheckBox) this.getSetting("OpenInv").getSetting()).state)) {
                collect();
                Collections.shuffle(trash);
                if(AutoArmor.needArmor == false) {
                    for (Integer integer : trash) {
                        if (timer.hasReached((long) 100)) {
                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, integer < 9 ? integer + 36 : integer,
                                    1, 4, mc.thePlayer);
                            mc.playerController.updateController();
                            timer.reset();
                        }
                    }
                }
            }
        }
    }

    private void collect() {
        trash.clear();
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);
                if (stack != null && stack.getItem() != null && !(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemAppleGold) && !(stack.getItem() instanceof ItemEnderPearl) && !(stack.getItem() instanceof ItemBlock) &&
                        !(stack.getItem() instanceof ItemFishingRod) && !(stack.getItem() instanceof ItemBow) && !(stack.getItem() instanceof ItemFood) && !(stack.getItem() instanceof ItemPotion)
                        && !(stack.getItem() instanceof ItemTool) && !(stack.getItem() instanceof ItemArmor)) {
                    trash.add(slot);
                    }
            if(stack != null && stack.getItem() instanceof ItemSword && isBestSword(stack) == false){
                trash.add(slot);
            }
        if (stack != null && stack.getItem() instanceof ItemArmor && isBestArmor(stack) == false) {
            trash.add(slot);
        }
        }
    }


    private boolean isBestSword(ItemStack input) {
        boolean best = true;

        for(int i = 9; i < 45; i++) {
            if(mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if(stack.getItem() instanceof ItemSword) {
                    if(this.getWeaponStrength(stack) > this.getWeaponStrength(input)) {
                        best = false;
                    }
                }
            }
        }

        return best;
    }

    private boolean isBestArmor(ItemStack input) {
        boolean best = true;

        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if (stack.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) stack.getItem();

                    if(armor.armorType == ((ItemArmor) input.getItem()).armorType) {
                        if (getProtection(stack) >= getProtection(input)) {
                            best = false;
                        }
                    }
                }
            }
        }

        return best;
    }

    private double getProtection(ItemStack stack) {

        double protection = 0;

        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();

            protection += armor.damageReduceAmount;
            protection += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.25;
        }

        return protection;
    }

    private double getWeaponStrength(ItemStack stack) {

        double damage = 0;

        if (stack != null) {
            if (stack.getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword) stack.getItem();
                damage += sword.getDamageVsEntity();
            }

            if (stack.getItem() instanceof ItemTool) {
                ItemTool tool = (ItemTool) stack.getItem();
                damage += tool.getToolMaterial().getDamageVsEntity();
            }

            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25;
        }

        return damage;
    }

}
