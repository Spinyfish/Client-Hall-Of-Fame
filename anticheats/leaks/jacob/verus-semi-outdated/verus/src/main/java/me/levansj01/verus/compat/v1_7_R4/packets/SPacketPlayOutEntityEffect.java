package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutEntityEffect;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEffect;

public class SPacketPlayOutEntityEffect
extends VPacketPlayOutEntityEffect {
    private static final Field c_field;
    private static final Field a_field;
    private static final Field b_field;
    private static final Field d_field;

    static {
        a_field = SafeReflection.access(PacketPlayOutEntityEffect.class, "a");
        b_field = SafeReflection.access(PacketPlayOutEntityEffect.class, "b");
        c_field = SafeReflection.access(PacketPlayOutEntityEffect.class, "c");
        d_field = SafeReflection.access(PacketPlayOutEntityEffect.class, "d");
    }

    public void accept(PacketPlayOutEntityEffect packetPlayOutEntityEffect) {
        this.id = (Integer)SafeReflection.fetch(a_field, packetPlayOutEntityEffect);
        this.effect = (Byte)SafeReflection.fetch(b_field, packetPlayOutEntityEffect);
        this.amplifier = (Byte)SafeReflection.fetch(c_field, packetPlayOutEntityEffect);
        this.duration = ((Short)SafeReflection.fetch(d_field, packetPlayOutEntityEffect)).intValue();
        this.flags = 0;
    }
}

