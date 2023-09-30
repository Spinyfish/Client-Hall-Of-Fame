package dev.africa.pandaware.impl.module.misc.disabler.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class NoSprintDisabler extends ModuleMode<DisablerModule> {
    public NoSprintDisabler(String name, DisablerModule parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer.isSprinting()) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer,
                    C0BPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof C0BPacketEntityAction) {
            event.cancel();
        }
    };
}
