package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutGameStateChange;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutGameStateChange;

public class SPacketPlayOutGameStateChange
extends VPacketPlayOutGameStateChange {
    private static final Field b_field = SafeReflection.access(PacketPlayOutGameStateChange.class, "b");
    private static final Field c_field = SafeReflection.access(PacketPlayOutGameStateChange.class, "c");

    public void accept(PacketPlayOutGameStateChange packetPlayOutGameStateChange) {
        this.setting = (Integer)SafeReflection.fetch(b_field, packetPlayOutGameStateChange);
        this.value = ((Float)SafeReflection.fetch(c_field, packetPlayOutGameStateChange)).floatValue();
    }
}

