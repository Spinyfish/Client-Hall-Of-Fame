package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;
import org.bukkit.*;

public class SPacketPlayOutRespawn extends VPacketPlayOutRespawn
{
    private static final Field c_field;
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutRespawn)o);
    }
    
    public void accept(final PacketPlayOutRespawn packetPlayOutRespawn) {
        this.gameMode = GameMode.getByValue(((WorldSettings.EnumGamemode)SafeReflection.fetch(SPacketPlayOutRespawn.c_field, packetPlayOutRespawn)).getId());
    }
    
    static {
        c_field = SafeReflection.access(PacketPlayOutRespawn.class, "c");
    }
}
