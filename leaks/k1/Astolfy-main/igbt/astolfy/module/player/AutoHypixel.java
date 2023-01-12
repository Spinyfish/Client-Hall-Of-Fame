package igbt.astolfy.module.player;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventPacket;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.ui.Notifications.Notification;
import igbt.astolfy.ui.Notifications.NotificationManager;
import igbt.astolfy.ui.Notifications.NotificationType;
import igbt.astolfy.utils.TimerUtils;
import net.minecraft.inventory.Slot;

public class AutoHypixel extends ModuleBase {
    public AutoHypixel() {
        super("AutoHypixel", 0, Category.PLAYER);
    }
    TimerUtils timer = new TimerUtils();
    public void onEvent(Event e){
        if(e instanceof EventPacket){
            if(mc.thePlayer != null)
                if(timer.hasReached(2500))
                    for(Slot slot : mc.thePlayer.inventoryContainer.inventorySlots){
                        if(slot.getStack() != null){
                            //System.out.println(slot.getStack().getDisplayName());
                            if(slot.getStack().getDisplayName().equalsIgnoreCase("§b§lPlay Again §7(Right Click)")){
                                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot.slotNumber, 1, 4, mc.thePlayer);
                                NotificationManager.showNotification(new Notification(NotificationType.SUCCESS, "Auto-Hypixel", "Joining a new game.", 5));
                                timer.reset();
                                return;
                            }
                        }
                    }
        }
    }
}
