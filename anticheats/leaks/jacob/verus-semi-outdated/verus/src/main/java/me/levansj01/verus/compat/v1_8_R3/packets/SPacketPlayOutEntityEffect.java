package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutEntityEffect extends VPacketPlayOutEntityEffect
{
    private static final Field b_field;
    private static final Field c_field;
    private static final Field d_field;
    private static final Field e_field;
    private static final Field a_field;
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutEntityEffect)o);
    }
    
    static {
        a_field = SafeReflection.access(PacketPlayOutEntityEffect.class, "a");
        b_field = SafeReflection.access(PacketPlayOutEntityEffect.class, "b");
        c_field = SafeReflection.access(PacketPlayOutEntityEffect.class, "c");
        d_field = SafeReflection.access(PacketPlayOutEntityEffect.class, "d");
        e_field = SafeReflection.access(PacketPlayOutEntityEffect.class, "e");
    }
    
    public void accept(final PacketPlayOutEntityEffect packetPlayOutEntityEffect) {
        this.id = (int)SafeReflection.fetch(SPacketPlayOutEntityEffect.a_field, packetPlayOutEntityEffect);
        this.effect = (byte)SafeReflection.fetch(SPacketPlayOutEntityEffect.b_field, packetPlayOutEntityEffect);
        this.amplifier = (byte)SafeReflection.fetch(SPacketPlayOutEntityEffect.c_field, packetPlayOutEntityEffect);
        this.duration = (int)SafeReflection.fetch(SPacketPlayOutEntityEffect.d_field, packetPlayOutEntityEffect);
        this.flags = (byte)SafeReflection.fetch(SPacketPlayOutEntityEffect.e_field, packetPlayOutEntityEffect);
    }
}
