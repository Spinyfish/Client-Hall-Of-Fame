package xyz.unnamed.anticheat.model.server;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MethodProfiler;
import net.minecraft.server.v1_8_R3.PacketPlayOutTransaction;
import xyz.unnamed.anticheat.Anticheat;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.model.packet.transaction.TransactionInformation;
import xyz.unnamed.anticheat.service.PlayerDataService;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

public class AnticheatMethodProfiler extends MethodProfiler {

    public AnticheatPlayerList playerList;
    private static final String TICK_START_STRING = "levels", TICK_END_STRING = "tickables";
    private static final PacketPlayOutTransaction RECYCLED_PACKET = new PacketPlayOutTransaction(0, (short) -1, false);

    private void sendPackets(boolean addInfo) {
        for (EntityPlayer p : playerList.players) {
            if (p == null || p.playerConnection == null) {
                continue;
            }

            PlayerData playerData = Anticheat.get(PlayerDataService.class).getPlayer(p.getBukkitEntity());
            p.playerConnection.sendPacket(RECYCLED_PACKET);

            if (addInfo) {
                playerData.getCheckManager().getTransactionQueue().add(new TransactionInformation());
            }
        }
    }

    private void startTick() {
        this.sendPackets(true);
    }

    private void endTick() {
        this.sendPackets(false);
    }

    @Override
    public void c(String s) {
        super.c(s);

        switch (s) {
            case TICK_START_STRING:
                startTick();
                break;
            case TICK_END_STRING:
                endTick();
                break;
        }
    }
}
