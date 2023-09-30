package dev.africa.pandaware.impl.module.misc.disabler.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import lombok.var;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FuncraftDisabler extends ModuleMode<DisablerModule> {
    public FuncraftDisabler(String name, DisablerModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (mc.thePlayer != null) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    var packet = (C03PacketPlayer) event.getPacket();

                    packet.setMoving(true);
                    event.setPacket(packet);

                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C00PacketKeepAlive(-2));
                }
            }
        }
    };

    @EventHandler
    EventCallback<MotionEvent> onUpdate = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            event.setPitch(0);
        }
    };
}
