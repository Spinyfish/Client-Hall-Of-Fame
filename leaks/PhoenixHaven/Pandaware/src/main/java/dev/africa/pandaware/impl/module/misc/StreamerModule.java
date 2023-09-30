package dev.africa.pandaware.impl.module.misc;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.TickEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import lombok.Getter;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.ArrayList;
import java.util.List;

@Getter
@ModuleInfo(name = "Streamer Mode", description = "sry alan <3 u bby", category = Category.MISC)
public class StreamerModule extends Module {
    public final List<String> PLAYERS = new ArrayList<>();

    @EventHandler
    EventCallback<TickEvent> onCum = event -> {
        if (mc.theWorld != null && mc.thePlayer != null) {
            for (final NetworkPlayerInfo player : mc.getNetHandler().getPlayerInfoMap()) {
                if (player.getGameProfile().getName().length() < 3) continue;

                if (!PLAYERS.contains(player.getGameProfile().getName())) {
                    PLAYERS.add(player.getGameProfile().getName());
                }
            }
        }
        if (PLAYERS.size() > 60) {
            PLAYERS.remove(0);
        }
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) event.getPacket();
            String message = packet.getChatComponent().getUnformattedText();
            if (message.toLowerCase().contains("sending you to ")) {
                event.cancel();
            }
        }
    };

    @Override
    public void onDisable() {
        PLAYERS.clear();
    }
}
