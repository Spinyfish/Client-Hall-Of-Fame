package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutRemoveEntityEffect;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutRemoveEntityEffect;

public class SPacketPlayOutRemoveEntityEffect
extends VPacketPlayOutRemoveEntityEffect {
    private static final Field a_field = SafeReflection.access(PacketPlayOutRemoveEntityEffect.class, "a");
    private static final Field b_field = SafeReflection.access(PacketPlayOutRemoveEntityEffect.class, "b");

    public void accept(PacketPlayOutRemoveEntityEffect packetPlayOutRemoveEntityEffect) {
        this.id = (Integer)SafeReflection.fetch(a_field, packetPlayOutRemoveEntityEffect);
        this.effect = (Integer)SafeReflection.fetch(b_field, packetPlayOutRemoveEntityEffect);
    }
}

