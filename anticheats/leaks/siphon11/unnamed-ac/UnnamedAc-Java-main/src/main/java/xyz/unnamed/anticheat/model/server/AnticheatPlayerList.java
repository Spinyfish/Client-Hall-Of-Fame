package xyz.unnamed.anticheat.model.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;
import xyz.unnamed.anticheat.Anticheat;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.service.PlayerDataService;
import xyz.unnamed.anticheat.service.packet.PacketService;
import xyz.unnamed.anticheat.utilities.chat.CC;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

public class AnticheatPlayerList extends DedicatedPlayerList {

    public AnticheatPlayerList(DedicatedServer dedicatedServer) {
        super(dedicatedServer);
    }

    @Override
    public EntityPlayer attemptLogin(LoginListener loginlistener, GameProfile gameprofile, String hostname) {
        EntityPlayer entityPlayer = super.attemptLogin(loginlistener, gameprofile, hostname);

        if (entityPlayer == null)
            return null;

        NetworkManager networkManager = loginlistener.networkManager;
        Player player = entityPlayer.getBukkitEntity();
        PlayerData playerData = Anticheat.get(PlayerDataService.class).registerPlayer(player);
        Anticheat.get(PacketService.class).inject(playerData, networkManager);

        return entityPlayer;
    }

    @Override
    public String disconnect(EntityPlayer entityplayer) {
        String res = super.disconnect(entityplayer);

        if (entityplayer == null)
            return res;

        Player player = entityplayer.getBukkitEntity();
        Anticheat.get(PacketService.class).eject(player);
        Anticheat.get(PlayerDataService.class).unregisterPlayer(player);

        return res;
    }

    @Override
    public void onPlayerJoin(EntityPlayer entityplayer, String joinMessage) {
        super.onPlayerJoin(entityplayer, joinMessage);

        if (entityplayer == null || !entityplayer.getBukkitEntity().hasPermission("anticheat.admin") || !entityplayer.getBukkitEntity().isOp())
            return;

        entityplayer.getBukkitEntity().sendMessage(CC.GOLD + "∫ " + CC.GRAY + "» " +
                CC.YELLOW + "Welcome back, " + CC.GOLD +
                entityplayer.getBukkitEntity().getDisplayName() + CC.YELLOW  + ".");
    }
}
