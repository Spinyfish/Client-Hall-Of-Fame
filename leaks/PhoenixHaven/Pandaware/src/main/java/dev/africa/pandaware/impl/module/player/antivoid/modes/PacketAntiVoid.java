package dev.africa.pandaware.impl.module.player.antivoid.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.player.antivoid.AntiVoidModule;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class PacketAntiVoid extends ModuleMode<AntiVoidModule> {
    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (mc.thePlayer.fallDistance >= this.getParent().getFallDistance().getValue().floatValue() && !PlayerUtils.isBlockUnder()) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ, true));
            mc.thePlayer.fallDistance = 0;
        }
    };

    public PacketAntiVoid(String name, AntiVoidModule parent) {
        super(name, parent);
    }
}
