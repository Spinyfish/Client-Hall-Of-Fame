/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.TimeUtils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;

public class ChestStealer
extends Module {
    private TimeUtils timeUtils = new TimeUtils();
    private final ItemStack trash = new ItemStack();
    public NumberSetting delay = new NumberSetting("Delay", 0.0, 300.0, 75.0, 25.0);
    public BooleanSetting close = new BooleanSetting("Close Chest", true);
    public BooleanSetting gui = new BooleanSetting("Hide Gui", false);
    public BooleanSetting name = new BooleanSetting("Check name", false);
    public BooleanSetting sort = new BooleanSetting("Sort", true);

    public ChestStealer() {
        super("ChestStealer", 0, Category.PLAYER);
        this.addSettings(this.delay, this.close, this.gui, this.name, this.sort);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        Object chest;
        this.setSuffix(String.valueOf(this.delay.getValue()));
        if (this.name.isEnabled()) {
            chest = (GuiChest)ChestStealer.mc.currentScreen;
            if (!((GuiChest)chest).lowerChestInventory.getName().toLowerCase().contains("chest")) {
                return;
            }
        }
        if (ChestStealer.mc.thePlayer.openContainer != null) {
            if (ChestStealer.mc.thePlayer.openContainer instanceof ContainerChest) {
                chest = (ContainerChest)ChestStealer.mc.thePlayer.openContainer;
                for (int i = 0; i < ((ContainerChest)chest).getLowerChestInventory().getSizeInventory(); ++i) {
                    if (((ContainerChest)chest).getLowerChestInventory().getStackInSlot(i) != null && this.timeUtils.hasReached(this.delay.getValueInt())) {
                        ChestStealer.mc.playerController.windowClick(((ContainerChest)chest).windowId, i, 0, 1, ChestStealer.mc.thePlayer);
                        this.timeUtils.reset();
                    }
                    if (!this.isEmpty((ContainerChest)chest)) continue;
                    if (ChestStealer.mc.thePlayer.openContainer == null) continue;
                    if (!(ChestStealer.mc.thePlayer.openContainer instanceof ContainerChest) || !this.close.isEnabled()) continue;
                    ChestStealer.mc.thePlayer.closeScreen();
                }
                if (((Container)chest).getInventory().isEmpty()) {
                    mc.displayGuiScreen(null);
                }
            }
        }
    }

    private boolean isTrash(ContainerChest chest) {
        for (int index = 0; index < chest.getLowerChestInventory().getSizeInventory(); ++index) {
            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
            if (stack == this.trash) continue;
            return false;
        }
        return true;
    }

    private boolean isEmpty(ContainerChest chest) {
        for (int index = 0; index < chest.getLowerChestInventory().getSizeInventory(); ++index) {
            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
            if (stack == null) continue;
            return false;
        }
        return true;
    }
}

