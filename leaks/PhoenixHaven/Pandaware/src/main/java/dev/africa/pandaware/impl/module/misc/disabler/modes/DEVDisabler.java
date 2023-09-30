package dev.africa.pandaware.impl.module.misc.disabler.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.combat.KillAuraModule;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import dev.africa.pandaware.impl.module.movement.ScaffoldModule;
import dev.africa.pandaware.impl.ui.notification.Notification;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C18PacketSpectate;

public class DEVDisabler extends ModuleMode<DisablerModule> {
    public DEVDisabler(String name, DisablerModule parent) {
        super(name, parent);
    }

    long lastTime;
    int tick = 0, oldSlot, newSlot;

    @EventHandler
    EventCallback<MotionEvent> onPacket = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            if (Client.getInstance().getModuleManager().getByClass(ScaffoldModule.class).getData().isEnabled()) return;
            KillAuraModule killAuraModule = Client.getInstance().getModuleManager().getByClass(KillAuraModule.class);
            if (killAuraModule.getData().isEnabled() && killAuraModule.target != null) return;
            if (mc.thePlayer.isUsingItem()) return;
            if (System.currentTimeMillis() - lastTime > 1200L) {
                if (getBlockStack() == -1) {
                    Client.getInstance().getNotificationManager().addNotification(Notification.Type.WARNING, "You need to have blocks in your hotbar!", 1);
                    lastTime = System.currentTimeMillis();
                    newSlot = getBlockStack();
                    oldSlot = mc.thePlayer.inventory.currentItem;
                    tick = 0;
                    this.toggle(false);
                    return;
                }

                switch (tick) {
                    case 0:
                        newSlot = getBlockStack();
                        oldSlot = mc.thePlayer.inventory.currentItem;
                        if (newSlot != oldSlot)
                            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(newSlot));
                        event.setPitch(90);

                        tick++;
                        break;
                    case 1:
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(newSlot)));
                        event.setPitch(90);

                        tick++;
                        break;
                    case 2:
                        if (newSlot != oldSlot)
                            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(oldSlot));
                        lastTime = System.currentTimeMillis();
                        tick = 0;
                        Client.getInstance().getNotificationManager().addNotification(Notification.Type.SUCCESS, "Did funny", 1);
                        break;
                }
            }
        }
    };

    private int getBlockStack() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                slot = i;
                break;
            }
        }
        return slot;
    }
}
