package dev.africa.pandaware.impl.module.misc.disabler.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import dev.africa.pandaware.impl.setting.EnumSetting;
import lombok.AllArgsConstructor;
import lombok.var;
import net.minecraft.network.play.client.C03PacketPlayer;

public class C03ReplaceDisabler extends ModuleMode<DisablerModule> {
    private final EnumSetting<C03Mode> mode = new EnumSetting<>("Mode", C03Mode.C04);

    public C03ReplaceDisabler(String name, DisablerModule parent) {
        super(name, parent);

        this.registerSettings(
                this.mode
        );
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        switch (this.mode.getValue()) {
            case C04:
                if (event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                    var packet = (C03PacketPlayer.C04PacketPlayerPosition) event.getPacket();
                    event.cancel();
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                            packet.getX(), packet.getY(), packet.getZ(), mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch,
                            packet.isOnGround()
                    ));
                }
                break;
            case C06:
                if (event.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                    var packet = (C03PacketPlayer.C06PacketPlayerPosLook) event.getPacket();
                    event.cancel();
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                            packet.getX(), packet.getY(), packet.getZ(), packet.isOnGround()
                    ));
                }
                break;
        }
    };

    @AllArgsConstructor
    private enum C03Mode {
        C04("C04"),
        C06("C06");

        private final String label;
    }
}
