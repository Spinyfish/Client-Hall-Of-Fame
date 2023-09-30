/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventUpdate;

public class ChestStealer
extends Module {
    public Setting<Double> delay = new Setting<Double>("Delay", 50.0).minimum(0.0).maximum(500.0).incrementation(5.0).describedBy("The delay between each stealing each item");
    public Setting<Boolean> stop = new Setting<Boolean>("StopMotion", false).describedBy("Stop the player's motion when stealing");
    Timer timer = new Timer();
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        if (this.mc.thePlayer != null && this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest)this.mc.thePlayer.openContainer;
            if (this.stop.getValue().booleanValue()) {
                this.mc.thePlayer.motionX = 0.0;
                this.mc.thePlayer.motionZ = 0.0;
            }
            for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); ++i) {
                if (chest.getLowerChestInventory().getStackInSlot(i) != null && this.isGoodChest() && (this.delay.getValue() <= 0.0 || this.timer.hasTimeElapsed((long)this.delay.getValue().doubleValue(), true))) {
                    this.shiftClick(i, chest.windowId);
                }
                if (i < chest.getLowerChestInventory().getSizeInventory()) continue;
                this.mc.thePlayer.closeScreen();
            }
            if (chest.getInventory().isEmpty()) {
                this.mc.thePlayer.closeScreen();
            }
            int max = chest.inventorySlots.size() == 90 ? 54 : 27;
            for (int i = 0; i < max; ++i) {
                if (!chest.getSlot(i).getHasStack()) continue;
                return;
            }
            this.mc.thePlayer.closeScreen();
        }
    };

    public ChestStealer() {
        super("Stealer", "Steals the contents of a chest for your lazy ass", Category.PLAYER);
        this.setMetadata(() -> String.valueOf(this.delay.getValue()));
    }

    public void shiftClick(int slotId, int windowId) {
        ItemStack itemstack = this.mc.thePlayer.openContainer.slotClick(slotId, 0, 1, this.mc.thePlayer);
        short short1 = this.mc.thePlayer.openContainer.getNextTransactionID(this.mc.thePlayer.inventory);
        PacketUtil.sendPacketNoEvent(new C0EPacketClickWindow(windowId, slotId, 0, 1, itemstack, short1));
    }

    public boolean isGoodChest() {
        if (this.mc.currentScreen != null && this.mc.currentScreen instanceof GuiChest) {
            GuiChest currentChest = (GuiChest)this.mc.currentScreen;
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("game")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("select")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("compass")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("select")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("teleport")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("hypixel")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("play")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("skywars")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("bedwars")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("cakewars")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("lobby")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("mode")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("shop")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("map")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("cosmetic")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("duel")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("menu")) {
                return false;
            }
            return !currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("clique");
        }
        return false;
    }
}

