package dev.echo.module.impl.misc;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.network.PacketReceiveEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.utils.player.ChatUtil;
import net.minecraft.network.play.server.S29PacketSoundEffect;

public final class LightningTracker extends Module {

    public LightningTracker() {
        super("LightningTracker", Category.MISC, "detects lightning");
    }

    @Link
    public Listener<PacketReceiveEvent> onPacketReceive = event -> {
        if (event.getPacket() instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect soundPacket = ((S29PacketSoundEffect) event.getPacket());
            if (soundPacket.getSoundName().equals("ambient.weather.thunder")) {
                ChatUtil.print(String.format("Lightning detected at (%s, %s, %s)", (int) soundPacket.getX(), (int) soundPacket.getY(), (int) soundPacket.getZ()));
            }
        }
    };

}
