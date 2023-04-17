package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutTransaction extends VPacketPlayOutTransaction
{
    private static final Field id_field;
    private static final Field window_field;
    private static final Field accepted_field;
    
    static {
        window_field = SafeReflection.access(PacketPlayOutTransaction.class, "a");
        id_field = SafeReflection.access(PacketPlayOutTransaction.class, "b");
        accepted_field = SafeReflection.access(PacketPlayOutTransaction.class, "c");
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutTransaction)o);
    }
    
    public void accept(final PacketPlayOutTransaction packetPlayOutTransaction) {
        this.window = (int)SafeReflection.fetch(SPacketPlayOutTransaction.window_field, packetPlayOutTransaction);
        this.id = (short)SafeReflection.fetch(SPacketPlayOutTransaction.id_field, packetPlayOutTransaction);
        this.accepted = (boolean)SafeReflection.fetch(SPacketPlayOutTransaction.accepted_field, packetPlayOutTransaction);
    }
}
