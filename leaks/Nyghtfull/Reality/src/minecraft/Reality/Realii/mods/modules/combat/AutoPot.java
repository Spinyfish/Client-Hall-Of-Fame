/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.combat;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPostUpdate;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;

public class AutoPot
        extends Module {
    private Numbers<Number> health = new Numbers<Number>("Health", "health", 3.0, 0.0, 10.0, 0.5);
    private Numbers<Number> delay = new Numbers<Number>("Delay", "delay", 400.0, 0.0, 1000.0, 10.0);
    private Option<Boolean> jump = new Option<Boolean>("Jump", "jump", true);
    private Mode mode = new Mode("Mode", "mode", new String[]{"Potion", "Head"}, "Potion");
    static boolean currentlyPotting = false;
    private boolean isUsing;
    private int slot;
    private TimerUtil timer = new TimerUtil();

    public AutoPot() {
        super("AutoPot", ModuleType.Combat);
        this.addValues(this.health, this.delay, this.jump, this.mode);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        if (this.timer.hasReached(this.delay.getValue().intValue()) && (double) this.mc.thePlayer.getHealth() <= this.health.getValue().intValue() * 2.0) {
            this.slot = this.mode.getValue() == "Potion" ? this.getPotionSlot() : this.getSoupSlot();
            boolean bl = this.isUsing = this.slot != -1 && (this.jump.getValue() == false || this.mc.thePlayer.onGround);
            if (this.isUsing) {
                this.timer.reset();
                if (this.mode.getValue() == "Potion") {
                    currentlyPotting = true;
                    e.setPitch(this.jump.getValue() != false ? -90 : 90);
                    if (this.timer.hasReached(1.0)) {
                        currentlyPotting = false;
                        this.timer.reset();
                    }
                }
            }
        }
    }

    @EventHandler
    private void onUpdatePost(EventPostUpdate e) {
        if (this.isUsing) {
            int current = this.mc.thePlayer.inventory.currentItem;
            int next = this.mc.thePlayer.nextSlot();
            this.mc.thePlayer.moveToHotbar(this.slot, next);
            this.mc.thePlayer.inventory.currentItem = next;
            this.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
            this.mc.playerController.sendUseItem(this.mc.thePlayer, this.mc.theWorld, this.mc.thePlayer.getHeldItem());
            this.mc.thePlayer.inventory.currentItem = current;
            this.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
            this.isUsing = false;
            if (this.mc.thePlayer.onGround && this.jump.getValue().booleanValue() && this.mode.getValue() == "Potion") {
                this.mc.thePlayer.setSpeed(0.0);
                this.mc.thePlayer.motionY = 0.42;
            }
        }
    }

    private int getPotionSlot() {
        int slot = -1;
        for (Slot s : this.mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack is;
            if (!s.getHasStack() || !((is = s.getStack()).getItem() instanceof ItemPotion)) continue;
            ItemPotion ip = (ItemPotion) is.getItem();
            if (!ItemPotion.isSplash(is.getMetadata())) continue;
            boolean hasHealing = false;
            for (PotionEffect pe : ip.getEffects(is)) {
                if (pe.getPotionID() != Potion.heal.id) continue;
                hasHealing = true;
                break;
            }
            if (!hasHealing) continue;
            slot = s.slotNumber;
            break;
        }
        return slot;
    }

    private int getSoupSlot() {
        int slot = -1;
        for (Slot s : this.mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack is;
            if (!s.getHasStack() || !((is = s.getStack()).getItem() instanceof ItemSkull)) continue;
            slot = s.slotNumber;
            break;
        }
        return slot;
    }

    private int getPotionCount() {
        int count = 0;
        for (Slot s : this.mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack is;
            if (!s.getHasStack() || !((is = s.getStack()).getItem() instanceof ItemPotion)) continue;
            ItemPotion ip = (ItemPotion) is.getItem();
            if (!ItemPotion.isSplash(is.getMetadata())) continue;
            boolean hasHealing = false;
            for (PotionEffect pe : ip.getEffects(is)) {
                if (pe.getPotionID() != Potion.heal.id) continue;
                hasHealing = true;
                break;
            }
            if (!hasHealing) continue;
            ++count;
        }
        return count;
    }

    private int getSoupCount() {
        int count = 0;
        for (Slot s : this.mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack is;
            if (!s.getHasStack() || !((is = s.getStack()).getItem() instanceof ItemSoup)) continue;
            ++count;
        }
        return count;
    }


}

