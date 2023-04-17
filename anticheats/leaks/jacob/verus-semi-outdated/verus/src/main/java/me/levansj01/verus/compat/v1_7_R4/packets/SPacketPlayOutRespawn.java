package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutRespawn;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.EnumGamemode;
import net.minecraft.server.v1_7_R4.PacketPlayOutRespawn;
import org.bukkit.GameMode;

public class SPacketPlayOutRespawn
extends VPacketPlayOutRespawn {
    private static final Field c_field = SafeReflection.access(PacketPlayOutRespawn.class, "c");

    public void accept(PacketPlayOutRespawn packetPlayOutRespawn) {
        EnumGamemode enumGamemode = (EnumGamemode)SafeReflection.fetch(c_field, packetPlayOutRespawn);
        this.gameMode = GameMode.getByValue((int)enumGamemode.getId());
    }
}

