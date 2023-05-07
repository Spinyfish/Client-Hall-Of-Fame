package client.module.impl.player;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.UpdateEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.network.play.client.C0EPacketClickWindow;

@ModuleInfo(name = "Stealer", description = "Steals items from chests for you", category = Category.PLAYER)
public class Stealer extends Module {
    @EventLink()
    public final Listener<UpdateEvent> onUpdate = event -> {
        if (mc.currentScreen instanceof GuiChest) {
            for (int i = 0; i < 9*((GuiChest) mc.currentScreen).inventoryRows; i++) {
                if (((GuiChest) mc.currentScreen).inventorySlots.getSlot(i).getHasStack()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0EPacketClickWindow(((GuiChest) mc.currentScreen).inventorySlots.windowId, i, 0, 1, ((GuiChest) mc.currentScreen).inventorySlots.getSlot(i).getStack(), mc.thePlayer.openContainer.getNextTransactionID(mc.thePlayer.inventory)));
                }
            }
            mc.thePlayer.closeScreen();
        }
    };
}