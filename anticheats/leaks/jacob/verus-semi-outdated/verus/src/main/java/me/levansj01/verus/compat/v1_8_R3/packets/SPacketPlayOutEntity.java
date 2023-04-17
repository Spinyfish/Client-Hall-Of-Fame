package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutEntity extends VPacketPlayOutEntity
{
    private static final Field yaw_field;
    private static final Field ground_field;
    private static final Field x_field;
    private static final Field y_field;
    private static final Field pitch_field;
    private static final Field look_field;
    private static final Field z_field;
    private static final Field id_field;
    
    public PacketPlayOutEntity create() {
        final PacketPlayOutEntity packetPlayOutEntity = new PacketPlayOutEntity();
        SafeReflection.set(SPacketPlayOutEntity.id_field, packetPlayOutEntity, this.id);
        SafeReflection.set(SPacketPlayOutEntity.x_field, packetPlayOutEntity, this.x);
        SafeReflection.set(SPacketPlayOutEntity.y_field, packetPlayOutEntity, this.y);
        SafeReflection.set(SPacketPlayOutEntity.z_field, packetPlayOutEntity, this.z);
        SafeReflection.set(SPacketPlayOutEntity.yaw_field, packetPlayOutEntity, this.yaw);
        SafeReflection.set(SPacketPlayOutEntity.pitch_field, packetPlayOutEntity, this.pitch);
        SafeReflection.set(SPacketPlayOutEntity.ground_field, packetPlayOutEntity, this.ground);
        SafeReflection.set(SPacketPlayOutEntity.look_field, packetPlayOutEntity, this.look);
        return packetPlayOutEntity;
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutEntity)o);
    }
    
    static {
        id_field = SafeReflection.access(PacketPlayOutEntity.class, "a");
        x_field = SafeReflection.access(PacketPlayOutEntity.class, "b");
        y_field = SafeReflection.access(PacketPlayOutEntity.class, "c");
        z_field = SafeReflection.access(PacketPlayOutEntity.class, "d");
        yaw_field = SafeReflection.access(PacketPlayOutEntity.class, "e");
        pitch_field = SafeReflection.access(PacketPlayOutEntity.class, "f");
        ground_field = SafeReflection.access(PacketPlayOutEntity.class, "g");
        look_field = SafeReflection.access(PacketPlayOutEntity.class, "h");
    }
    
    public void accept(final PacketPlayOutEntity packetPlayOutEntity) {
        this.id = (int)SafeReflection.fetch(SPacketPlayOutEntity.id_field, packetPlayOutEntity);
        this.x = (byte)SafeReflection.fetch(SPacketPlayOutEntity.x_field, packetPlayOutEntity);
        this.y = (byte)SafeReflection.fetch(SPacketPlayOutEntity.y_field, packetPlayOutEntity);
        this.z = (byte)SafeReflection.fetch(SPacketPlayOutEntity.z_field, packetPlayOutEntity);
        this.yaw = (byte)SafeReflection.fetch(SPacketPlayOutEntity.yaw_field, packetPlayOutEntity);
        this.pitch = (byte)SafeReflection.fetch(SPacketPlayOutEntity.pitch_field, packetPlayOutEntity);
        this.ground = (boolean)SafeReflection.fetch(SPacketPlayOutEntity.ground_field, packetPlayOutEntity);
        this.look = (boolean)SafeReflection.fetch(SPacketPlayOutEntity.look_field, packetPlayOutEntity);
        this.pos = (packetPlayOutEntity instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook || packetPlayOutEntity instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMove);
    }
}
