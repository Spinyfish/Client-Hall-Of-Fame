/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.commands.defaultsubs;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.command.ViaSubCommand;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PPSSubCmd
extends ViaSubCommand {
    @Override
    public String name() {
        return "pps";
    }

    @Override
    public String description() {
        return "Shows the packets per second of online players";
    }

    @Override
    public String usage() {
        return "pps";
    }

    @Override
    public boolean execute(ViaCommandSender sender, String[] args) {
        HashMap playerVersions = new HashMap();
        int totalPackets = 0;
        int clients = 0;
        long max = 0L;
        for (ViaCommandSender p : Via.getPlatform().getOnlinePlayers()) {
            UserConnection uc;
            int playerVersion = Via.getAPI().getPlayerVersion(p.getUUID());
            if (!playerVersions.containsKey(playerVersion)) {
                playerVersions.put(playerVersion, new HashSet());
            }
            if ((uc = Via.getManager().getConnectionManager().getConnectedClient(p.getUUID())) == null || uc.getPacketTracker().getPacketsPerSecond() <= -1L) continue;
            ((Set)playerVersions.get(playerVersion)).add(p.getName() + " (" + uc.getPacketTracker().getPacketsPerSecond() + " PPS)");
            totalPackets = (int)((long)totalPackets + uc.getPacketTracker().getPacketsPerSecond());
            if (uc.getPacketTracker().getPacketsPerSecond() > max) {
                max = uc.getPacketTracker().getPacketsPerSecond();
            }
            ++clients;
        }
        TreeMap sorted = new TreeMap(playerVersions);
        PPSSubCmd.sendMessage(sender, "&4Live Packets Per Second", new Object[0]);
        if (clients > 1) {
            PPSSubCmd.sendMessage(sender, "&cAverage: &f" + totalPackets / clients, new Object[0]);
            PPSSubCmd.sendMessage(sender, "&cHighest: &f" + max, new Object[0]);
        }
        if (clients == 0) {
            PPSSubCmd.sendMessage(sender, "&cNo clients to display.", new Object[0]);
        }
        for (Map.Entry entry : sorted.entrySet()) {
            PPSSubCmd.sendMessage(sender, "&8[&6%s&8]: &b%s", ProtocolVersion.getProtocol((Integer)entry.getKey()).getName(), entry.getValue());
        }
        sorted.clear();
        return true;
    }
}

