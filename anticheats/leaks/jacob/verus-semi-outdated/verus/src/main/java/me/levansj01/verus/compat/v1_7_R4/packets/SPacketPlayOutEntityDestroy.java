package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutEntityDestroy;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;

public class SPacketPlayOutEntityDestroy
extends VPacketPlayOutEntityDestroy {
    private static final Field ids_field = SafeReflection.access(PacketPlayOutEntityDestroy.class, "a");

    public void accept(PacketPlayOutEntityDestroy packetPlayOutEntityDestroy) {
        this.ids = (int[])SafeReflection.fetch(ids_field, packetPlayOutEntityDestroy);
    }
}

