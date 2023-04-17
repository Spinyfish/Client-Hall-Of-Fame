package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutEntityDestroy extends VPacketPlayOutEntityDestroy
{
    private static final Field ids_field;
    
    static {
        ids_field = SafeReflection.access(PacketPlayOutEntityDestroy.class, "a");
    }
    
    public void accept(final PacketPlayOutEntityDestroy packetPlayOutEntityDestroy) {
        this.ids = (int[])SafeReflection.fetch(SPacketPlayOutEntityDestroy.ids_field, packetPlayOutEntityDestroy);
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutEntityDestroy)o);
    }
}
