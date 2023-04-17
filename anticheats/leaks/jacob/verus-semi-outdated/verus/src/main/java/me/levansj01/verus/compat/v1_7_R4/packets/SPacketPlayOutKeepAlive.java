package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import java.math.BigInteger;
import me.levansj01.verus.compat.packets.VPacketPlayOutKeepAlive;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutKeepAlive;

public class SPacketPlayOutKeepAlive
extends VPacketPlayOutKeepAlive {
    private static final Field id_field = SafeReflection.access(PacketPlayOutKeepAlive.class, "a");

    public void accept(PacketPlayOutKeepAlive packetPlayOutKeepAlive) {
        this.id = BigInteger.valueOf(((Integer)SafeReflection.fetch(id_field, packetPlayOutKeepAlive)).longValue());
    }
}

