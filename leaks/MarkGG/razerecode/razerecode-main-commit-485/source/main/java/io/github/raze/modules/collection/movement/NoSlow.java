package io.github.raze.modules.collection.movement;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.collection.network.EventPacketSend;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.utilities.collection.visual.ChatUtil;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPosition;

public class NoSlow extends BaseModule {

    public ArraySetting mode;
    public static boolean isNoSlow = false;

    public NoSlow() {
        super("NoSlow", "Removes Slowness while using items.", ModuleCategory.MOVEMENT);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(

                mode = new ArraySetting(this, "Mode", "Vanilla", "Vanilla", "NCP", "Intave", "MMC")

        );
    }

    public void onEnable() {
        isNoSlow = true;
        switch (mode.get()) {
            case "Intave":
                ChatUtil.addChatMessage("Note! This only works on swords properly.");
                break;
            default:
                break;
        }
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            switch(mode.get()) {
                case "NCP":
                    if (mc.thePlayer.isUsingItem() || mc.thePlayer.isEating() || mc.thePlayer.isBlocking())
                        mc.getNetHandler().addToSendQueueSilent(new C08PacketPlayerBlockPlacement(new BlockPosition(mc.thePlayer.posX, mc.thePlayer.posY + 10, mc.thePlayer.posZ), 0, mc.thePlayer.getHeldItem(), 0, 0, 0));
                    break;
                case "Intave":
                    if (mc.thePlayer.isBlocking())
                        if(mc.thePlayer.ticksExisted % 2 == 0)
                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange());
                    break;
                case "MMC":
                    // I think this is supposed to bypass?
                    if (mc.thePlayer.isUsingItem() || mc.thePlayer.isEating() || mc.thePlayer.isBlocking()) {
                        mc.thePlayer.setSprinting(false);
                    } else {
                        mc.thePlayer.setSprinting(mc.gameSettings.keyBindSprint.pressed);
                    }
                    break;
            }
        }
    }

    @SubscribeEvent
    private void onPacketSend(EventPacketSend event) {
        if (mode.compare("Intave")) {
            if (mc.thePlayer.isUsingItem() || mc.thePlayer.isEating() || mc.thePlayer.isBlocking()) {
                if (event.getPacket() instanceof C0CPacketInput) {
                    event.setCancelled(true);
                    mc.thePlayer.sendQueue.addToSendQueue(event.getPacket());
                }
            }
        }
    }

    public void onDisable() {
        isNoSlow = false;
    }

}