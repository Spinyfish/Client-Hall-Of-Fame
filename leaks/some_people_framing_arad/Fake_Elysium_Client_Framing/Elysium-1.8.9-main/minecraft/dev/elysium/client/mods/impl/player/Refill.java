package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventUpdate;
import dev.elysium.client.utils.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Refill extends Mod {

    public ModeSetting mode = new ModeSetting("Mode",this,"Spoof","Inventory Only");
    public NumberSetting delay = new NumberSetting("Delay",0,2500,200,1,this);
    public NumberSetting minpots = new NumberSetting("Pots",1,9,6,1,this);
    public BooleanSetting nomove = new BooleanSetting("Only When Still",true,this);
    public Timer timer = new Timer();

    public Refill() {
        super("Refill","Refills your hotbar with disreable potions", Category.PLAYER);
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        if(timer.hasTimeElapsed((long) delay.getValue(), false) && hotbarHasSlots() && (!nomove.isEnabled() || !mc.thePlayer.isMoving()) && potionsInHotbar() < minpots.getValue()) {
            if(mode.is("Spoof") || mc.currentScreen instanceof GuiInventory) {
                refill();
                timer.reset();
            }
        }
    }

    public boolean hotbarHasSlots() {
        for(int x = 0; x < 9; x++) {
            ItemStack itemstack = mc.thePlayer.inventory.getStackInSlot(x);
            if (itemstack == null) {
                return true;
            }
        }
        return false;
    }

    public int potionsInHotbar() {
        int potions = 0;
        for(int x = 0; x < 9; x++) {
            ItemStack itemstack = mc.thePlayer.inventory.getStackInSlot(x);
            if (itemstack != null && itemstack.getItem() instanceof ItemPotion) {

                ItemPotion potion = (ItemPotion) itemstack.getItem();

                for(PotionEffect effect : potion.getEffects(itemstack)) {
                    if(effect.getPotionID() == Potion.heal.id) {
                        potions++;
                    }
                }
            }
        }
        return potions;
    }

    public void refill() {
        for(int x = 9; x < 36; x++) {
            ItemStack itemstack = mc.thePlayer.inventory.getStackInSlot(x);

            if (itemstack != null && itemstack.getItem() instanceof ItemPotion) {

                ItemPotion potion = (ItemPotion) itemstack.getItem();

                for(PotionEffect effect : potion.getEffects(itemstack)) {
                    if(effect.getPotionID() == Potion.heal.id) {
                        mc.playerController.windowClick(0, x, 0, 1, mc.thePlayer);
                        return;
                    }
                }
            }
        }
    }
}
