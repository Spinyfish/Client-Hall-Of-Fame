package me.levansj01.verus.compat.v1_7_R4.packets;

import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.util.Arrays;
import me.levansj01.verus.compat.packets.VPacketPlayOutPosition;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutPosition;

public class SPacketPlayOutPosition
extends VPacketPlayOutPosition {
    private static final Field flags_field;
    private static final Field pitch_field;
    private static final Field z_field;
    private static final Field y_field;
    private static final Field yaw_field;
    private static final Field x_field;

    public void accept(PacketPlayOutPosition packetPlayOutPosition) {
        this.x = (Double)SafeReflection.fetch(x_field, packetPlayOutPosition);
        this.y = (Double)SafeReflection.fetch(y_field, packetPlayOutPosition);
        this.z = (Double)SafeReflection.fetch(z_field, packetPlayOutPosition);
        this.yaw = ((Float)SafeReflection.fetch(yaw_field, packetPlayOutPosition)).floatValue();
        this.pitch = ((Float)SafeReflection.fetch(pitch_field, packetPlayOutPosition)).floatValue();
        this.flags = Sets.newHashSet();
        this.flags.addAll(Arrays.asList(VPacketPlayOutPosition.TeleportFlag.X, VPacketPlayOutPosition.TeleportFlag.Y, VPacketPlayOutPosition.TeleportFlag.Z));
        if (flags_field != null && ((Boolean)SafeReflection.fetch(flags_field, packetPlayOutPosition)).booleanValue()) {
            this.flags.addAll(Arrays.asList(VPacketPlayOutPosition.TeleportFlag.X_ROT, VPacketPlayOutPosition.TeleportFlag.Y_ROT));
        }
    }

    static {
        x_field = SafeReflection.access(PacketPlayOutPosition.class, "a", "x");
        y_field = SafeReflection.access(PacketPlayOutPosition.class, "b", "y");
        z_field = SafeReflection.access(PacketPlayOutPosition.class, "c", "z");
        yaw_field = SafeReflection.access(PacketPlayOutPosition.class, "d", "yaw");
        pitch_field = SafeReflection.access(PacketPlayOutPosition.class, "e", "pitch");
        Field field = null;
        try {
            field = SafeReflection.access(PacketPlayOutPosition.class, "f", "flags");
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        flags_field = field;
    }
}

