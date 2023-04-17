package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutExplosion;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutExplosion;

public class SPacketPlayOutExplosion
extends VPacketPlayOutExplosion {
    private static final Field motY_field;
    private static final Field motZ_field;
    private static final Field motX_field;

    static {
        motX_field = SafeReflection.access(PacketPlayOutExplosion.class, "f");
        motY_field = SafeReflection.access(PacketPlayOutExplosion.class, "g");
        motZ_field = SafeReflection.access(PacketPlayOutExplosion.class, "h");
    }

    public void accept(PacketPlayOutExplosion packetPlayOutExplosion) {
        this.motX = ((Float)SafeReflection.fetch(motX_field, packetPlayOutExplosion)).floatValue();
        this.motY = ((Float)SafeReflection.fetch(motY_field, packetPlayOutExplosion)).floatValue();
        this.motZ = ((Float)SafeReflection.fetch(motZ_field, packetPlayOutExplosion)).floatValue();
    }
}

