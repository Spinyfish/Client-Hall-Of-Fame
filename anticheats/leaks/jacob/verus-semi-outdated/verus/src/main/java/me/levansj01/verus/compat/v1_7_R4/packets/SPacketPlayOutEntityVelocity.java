package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutEntityVelocity;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityVelocity;

public class SPacketPlayOutEntityVelocity
extends VPacketPlayOutEntityVelocity {
    private static final Field motY_field;
    private static final Field motX_field;
    private static final Field motZ_field;
    private static final Field id_field;

    static {
        id_field = SafeReflection.access(PacketPlayOutEntityVelocity.class, "a");
        motX_field = SafeReflection.access(PacketPlayOutEntityVelocity.class, "b");
        motY_field = SafeReflection.access(PacketPlayOutEntityVelocity.class, "c");
        motZ_field = SafeReflection.access(PacketPlayOutEntityVelocity.class, "d");
    }

    public void accept(PacketPlayOutEntityVelocity packetPlayOutEntityVelocity) {
        this.id = (Integer)SafeReflection.fetch(id_field, packetPlayOutEntityVelocity);
        this.motX = (Integer)SafeReflection.fetch(motX_field, packetPlayOutEntityVelocity);
        this.motY = (Integer)SafeReflection.fetch(motY_field, packetPlayOutEntityVelocity);
        this.motZ = (Integer)SafeReflection.fetch(motZ_field, packetPlayOutEntityVelocity);
    }
}

