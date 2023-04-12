package xyz.unnamed.anticheat.service.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.model.packet.PacketListener;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@RequiredArgsConstructor
public class PacketService {
    private static final String LISTENER_NAME = "anticheat-listener";

    public void inject(PlayerData playerData, NetworkManager networkManager) {
        ChannelPipeline pipeline = networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", LISTENER_NAME, new PacketListener(playerData));
    }

    public void eject(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        if (connection == null || connection.isDisconnected())
            return;

        Channel channel = connection.a().channel;
        ChannelPipeline pipeline = channel.pipeline();

        if (pipeline.get(LISTENER_NAME) != null)
            pipeline.remove(LISTENER_NAME);
    }
}
