package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import org.bukkit.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutRespawn extends VPacket
{
    protected GameMode gameMode;
    private static final int count;
    
    @Override
    public int ordinal() {
        return VPacketPlayOutRespawn.count;
    }
    
    static {
        count = VPacket.count();
    }
    
    public GameMode getGameMode() {
        return this.gameMode;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}
