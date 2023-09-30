package dev.echo.module.impl.misc;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.network.PacketReceiveEvent;
import dev.echo.listener.event.impl.network.PacketSendEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;

@SuppressWarnings("unused")
public final class AntiTabComplete extends Module {

    public AntiTabComplete() {
        super("AntiTabComplete", Category.MISC, "prevents you from tab completing");
    }

    @Link
    public Listener<PacketSendEvent> onPacketSend = event -> {
        if (event.getPacket() instanceof C14PacketTabComplete) {
            event.setCancelled(true);
        }
    };

    @Link
    public Listener<PacketReceiveEvent> onPacketReceive = event -> {
        if (event.getPacket() instanceof S3APacketTabComplete) {
            event.setCancelled(true);
        }
    };

}
