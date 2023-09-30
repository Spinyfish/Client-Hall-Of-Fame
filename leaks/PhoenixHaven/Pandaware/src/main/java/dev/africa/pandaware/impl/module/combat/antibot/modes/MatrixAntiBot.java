package dev.africa.pandaware.impl.module.combat.antibot.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.module.combat.antibot.AntiBotModule;
import dev.africa.pandaware.impl.ui.notification.Notification;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;

import java.util.ArrayList;
import java.util.List;

public class MatrixAntiBot extends ModuleMode<AntiBotModule> {
    private final List<Integer> botIDs = new ArrayList<>();

    @EventHandler
    EventCallback<PacketEvent> onPacket = e -> {
        if (e.getPacket() instanceof S0CPacketSpawnPlayer) {
            S0CPacketSpawnPlayer packet = e.getPacket();

            if (packet.func_148944_c().size() < 10) {
                botIDs.add(packet.getEntityID());
            }
        } else if (e.getPacket() instanceof S01PacketJoinGame) {
            botIDs.clear();
        }
    };

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = e -> {
        mc.theWorld.playerEntities.forEach(entityPlayer -> {
            if (entityPlayer != mc.thePlayer && botIDs.contains(entityPlayer.getEntityId())) {
                Client.getInstance().getNotificationManager().addNotification(Notification.Type.INFO, "Detected bot: §l" + entityPlayer.getName(), 3);
                mc.theWorld.removeEntity(entityPlayer);
            }
        });
    };

    public MatrixAntiBot(String name, AntiBotModule parent) {
        super(name, parent);
    }
}
