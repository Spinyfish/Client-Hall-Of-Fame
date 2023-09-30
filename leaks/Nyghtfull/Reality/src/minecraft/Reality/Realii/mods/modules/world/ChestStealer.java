/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.world;

import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventTick;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;

public class ChestStealer
extends Module {
    private Numbers<Number> delay = new Numbers<Number>("Delay", "delay", 0.0, 0.0, 1000.0, 10.0);
    private TimerUtil timer = new TimerUtil();

    public ChestStealer() {
        super("Cheststealer", ModuleType.World);

        this.addValues(this.delay);
    }

    @EventHandler
    private void onUpdate(EventTick event) {
        if (this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest)this.mc.thePlayer.openContainer;
            int i = 0;
            while (i < container.getLowerChestInventory().getSizeInventory()) {
                if (container.getLowerChestInventory().getStackInSlot(i) != null && this.timer.hasReached(this.delay.getValue().intValue())) {
                    this.mc.playerController.windowClick(container.windowId, i, 0, 1, this.mc.thePlayer);
                    this.timer.reset();
                }
                ++i;
            }
            if (this.isEmpty()) {
                this.mc.thePlayer.closeScreen();
            }
        }
    }

    private boolean isEmpty() {
        if (this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest)this.mc.thePlayer.openContainer;
            int i = 0;
            while (i < container.getLowerChestInventory().getSizeInventory()) {
                ItemStack itemStack = container.getLowerChestInventory().getStackInSlot(i);
                if (itemStack != null && itemStack.getItem() != null) {
                    return false;
                }
                ++i;
            }
        }
        return true;
    }
}

