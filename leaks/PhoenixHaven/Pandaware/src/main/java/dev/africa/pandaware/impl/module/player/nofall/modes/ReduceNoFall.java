package dev.africa.pandaware.impl.module.player.nofall.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.player.nofall.NoFallModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import net.minecraft.network.play.client.C03PacketPlayer;

public class ReduceNoFall extends ModuleMode<NoFallModule> {
    private final BooleanSetting packet = new BooleanSetting("Packet", false);

    public ReduceNoFall(String name, NoFallModule parent) {
        super(name, parent);

        this.registerSettings(this.packet);
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (this.getParent().canFall() && mc.thePlayer.fallDistance > 4) {
            if (this.packet.getValue()) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer(true));
            } else {
                event.setOnGround(true);
            }

            mc.thePlayer.fallDistance = 0;
        }
    };
}
