package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutTransaction;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutTransaction;

public class SPacketPlayOutTransaction
extends VPacketPlayOutTransaction {
    private static final Field id_field;
    private static final Field accepted_field;
    private static final Field window_field;

    public void accept(PacketPlayOutTransaction packetPlayOutTransaction) {
        this.window = (Integer)SafeReflection.fetch(window_field, packetPlayOutTransaction);
        this.id = (Short)SafeReflection.fetch(id_field, packetPlayOutTransaction);
        this.accepted = (Boolean)SafeReflection.fetch(accepted_field, packetPlayOutTransaction);
    }

    static {
        window_field = SafeReflection.access(PacketPlayOutTransaction.class, "a");
        id_field = SafeReflection.access(PacketPlayOutTransaction.class, "b");
        accepted_field = SafeReflection.access(PacketPlayOutTransaction.class, "c");
    }
}

