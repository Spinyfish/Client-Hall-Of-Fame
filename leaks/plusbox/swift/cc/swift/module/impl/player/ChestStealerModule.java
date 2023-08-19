/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 20:47
 */

package cc.swift.module.impl.player;

import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;

public final class ChestStealerModule extends Module {

    public final DoubleValue firstDelay = new DoubleValue("First Delay", 100D, 0, 1000, 50);
    public final DoubleValue delay = new DoubleValue("Delay", 50D, 0, 1000, 50);
    public final BooleanValue checkName = new BooleanValue("Check Name", true);
    public final BooleanValue autoClose = new BooleanValue("Auto Close", true);

    private long lastClicked, lastOutSideChest;

    public ChestStealerModule() {
        super("ChestStealer", Category.PLAYER);
        this.registerValues(this.firstDelay, this.delay, this.checkName, this.autoClose);
    }

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (!(mc.currentScreen instanceof GuiChest)) {
            this.lastOutSideChest = System.currentTimeMillis();
            return;
        }

        final ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;

        if (System.currentTimeMillis() - this.lastOutSideChest < this.firstDelay.getValue() || System.currentTimeMillis() - this.lastClicked < this.delay.getValue())
            return;

        String chestName = chest.getLowerChestInventory().getDisplayName().getUnformattedText();

        if (this.checkName.getValue()) {
            if (!chestName.equals(I18n.format("container.chest")) && !chestName.equals(I18n.format("container.chestDouble")) && !chestName.contains("Chest"))
                return;
        }

        for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
            final ItemStack stack = chest.getLowerChestInventory().getStackInSlot(i);
            if (stack == null || stack.getItem() == null) continue;

            mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
            this.lastClicked = System.currentTimeMillis();
            return;
        }

        if (this.autoClose.getValue() && System.currentTimeMillis() - this.lastClicked > 100)
            mc.thePlayer.closeScreen();
    };
}
