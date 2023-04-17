package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutEntity;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_7_R4.PacketPlayOutRelEntityMoveLook;

public class SPacketPlayOutEntity
extends VPacketPlayOutEntity {
    private static final Field z_field;
    private static final Field ground_field;
    private static final Field y_field;
    private static final Field x_field;
    private static final Field pitch_field;
    private static final Field yaw_field;
    private static final Field id_field;

    public void accept(PacketPlayOutEntity packetPlayOutEntity) {
        this.id = (Integer)SafeReflection.fetch(id_field, packetPlayOutEntity);
        this.x = ((Byte)SafeReflection.fetch(x_field, packetPlayOutEntity)).byteValue();
        this.y = ((Byte)SafeReflection.fetch(y_field, packetPlayOutEntity)).byteValue();
        this.z = ((Byte)SafeReflection.fetch(z_field, packetPlayOutEntity)).byteValue();
        this.yaw = (Byte)SafeReflection.fetch(yaw_field, packetPlayOutEntity);
        this.pitch = (Byte)SafeReflection.fetch(pitch_field, packetPlayOutEntity);
        this.ground = (Boolean)SafeReflection.fetch(ground_field, packetPlayOutEntity);
        this.look = false;
        this.pos = packetPlayOutEntity instanceof PacketPlayOutRelEntityMoveLook || packetPlayOutEntity instanceof PacketPlayOutRelEntityMove;
    }

    static {
        id_field = SafeReflection.access(PacketPlayOutEntity.class, "a");
        x_field = SafeReflection.access(PacketPlayOutEntity.class, "b");
        y_field = SafeReflection.access(PacketPlayOutEntity.class, "c");
        z_field = SafeReflection.access(PacketPlayOutEntity.class, "d");
        yaw_field = SafeReflection.access(PacketPlayOutEntity.class, "e");
        pitch_field = SafeReflection.access(PacketPlayOutEntity.class, "f");
        ground_field = SafeReflection.access(PacketPlayOutEntity.class, "g");
    }
}

