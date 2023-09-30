package dev.africa.pandaware.impl.module.player.antivoid.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.player.antivoid.AntiVoidModule;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.ArrayList;
import java.util.List;

public class HypixelAntiVoid extends ModuleMode<AntiVoidModule> {
    private final List<Packet<?>> cummyList = new ArrayList<>();

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (event.getPacket() instanceof C03PacketPlayer && !PlayerUtils.isBlockUnder() && mc.thePlayer.fallDistance >=
                this.getParent().getFallDistance().getValue().floatValue()) {
            this.cummyList.add(event.getPacket());
            event.cancel();
        }

        if (event.getPacket() instanceof S08PacketPlayerPosLook) this.cummyList.clear();

        if (mc.thePlayer.onGround) {
            if (!this.cummyList.isEmpty()) {
                this.cummyList.forEach(packet -> mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(packet));

                this.cummyList.clear();
            }
        }
    };

    public HypixelAntiVoid(String name, AntiVoidModule parent) {
        super(name, parent);
    }
}
