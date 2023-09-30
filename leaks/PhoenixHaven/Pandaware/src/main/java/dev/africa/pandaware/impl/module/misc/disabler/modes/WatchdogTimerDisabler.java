//DEPRECATED DONT USE
/*package dev.africa.pandaware.impl.module.misc.disabler.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import lombok.var;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class WatchdogTimerDisabler extends ModuleMode<DisablerModule> {
    private double packets;
    @EventHandler
    EventCallback<MotionEvent> onUpdate = event -> {
        if (packets != 20) {
            event.setPitch(0);
        }
    };
    private boolean game;
    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S01PacketJoinGame) {
            game = true;
            packets = 0;
        }
        if (event.getPacket() instanceof C03PacketPlayer && packets != 20 && game && mc.thePlayer != null) {
            event.cancel();
            packets++;

            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new
                    C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,
                    mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(null));
        }
        if (event.getPacket() instanceof S08PacketPlayerPosLook && packets != 20 && game && mc.thePlayer != null) {
            var packet = (S08PacketPlayerPosLook) event.getPacket();
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new
                    C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,
                    packet.getYaw(), packet.getPitch(), mc.thePlayer.onGround));
        }
        if (packets == 20) {
            game = false;
        }
    };

    public WatchdogTimerDisabler(String name, DisablerModule parent) {
        super(name, parent);
    }

    public void onDisable() {
        super.onDisable();
        packets = 0;
        game = false;
    }
}*/
//DEPRECATED DONT USE
