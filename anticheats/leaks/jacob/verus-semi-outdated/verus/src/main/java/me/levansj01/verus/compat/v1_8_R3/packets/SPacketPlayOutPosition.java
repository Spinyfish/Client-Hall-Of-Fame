package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class SPacketPlayOutPosition extends VPacketPlayOutPosition
{
    private static final Field flags_field;
    private static final Field pitch_field;
    private static final Field yaw_field;
    private static final Field z_field;
    private static final Field y_field;
    private static final Field x_field;
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutPosition)o);
    }
    
    public void accept(final PacketPlayOutPosition packetPlayOutPosition) {
        this.x = (double)SafeReflection.fetch(SPacketPlayOutPosition.x_field, packetPlayOutPosition);
        this.y = (double)SafeReflection.fetch(SPacketPlayOutPosition.y_field, packetPlayOutPosition);
        this.z = (double)SafeReflection.fetch(SPacketPlayOutPosition.z_field, packetPlayOutPosition);
        this.yaw = (float)SafeReflection.fetch(SPacketPlayOutPosition.yaw_field, packetPlayOutPosition);
        this.pitch = (float)SafeReflection.fetch(SPacketPlayOutPosition.pitch_field, packetPlayOutPosition);
        this.flags = (Set<Object>)((Set)SafeReflection.fetch(SPacketPlayOutPosition.flags_field, packetPlayOutPosition)).stream().map(SPacketPlayOutPosition::lambda$accept$0).collect(Collectors.toSet());
    }
    
    static {
        x_field = SafeReflection.access(PacketPlayOutPosition.class, new String[] { "a", "x" });
        y_field = SafeReflection.access(PacketPlayOutPosition.class, new String[] { "b", "y" });
        z_field = SafeReflection.access(PacketPlayOutPosition.class, new String[] { "c", "z" });
        yaw_field = SafeReflection.access(PacketPlayOutPosition.class, new String[] { "d", "yaw" });
        pitch_field = SafeReflection.access(PacketPlayOutPosition.class, new String[] { "e", "pitch" });
        flags_field = SafeReflection.access(PacketPlayOutPosition.class, new String[] { "f", "flags" });
    }
    
    private static TeleportFlag lambda$accept$0(final PacketPlayOutPosition.EnumPlayerTeleportFlags enumPlayerTeleportFlags) {
        return TeleportFlag.values()[enumPlayerTeleportFlags.ordinal()];
    }
}
