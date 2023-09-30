package dev.echo.module.impl.player;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.network.PacketSendEvent;
import dev.echo.listener.event.impl.player.BoundingBoxEvent;
import dev.echo.listener.event.impl.player.PushOutOfBlockEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class Freecam extends Module {

    @Link
    public Listener<BoundingBoxEvent> onBoundingBoxEvent = event -> {
        if (mc.thePlayer != null) {
            event.setCancelled(true);
        }
    };

    @Link
    public Listener<PushOutOfBlockEvent> pushOutOfBlockEventListener = event -> {
        if (mc.thePlayer != null) {
            event.setCancelled(true);
        }
    };

    @Link
    public Listener<PacketSendEvent> packetSendEventListener = event -> {
        if (event.getPacket() instanceof C03PacketPlayer) {
            event.setCancelled(true);
        }
    };

    @Override
    public void onEnable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.capabilities.allowFlying = true;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.capabilities.allowFlying = false;
            mc.thePlayer.capabilities.isFlying = false;
        }
        super.onDisable();
    }

    public Freecam() {
        super("Freecam", Category.PLAYER, "allows you to look around freely");
    }

}
