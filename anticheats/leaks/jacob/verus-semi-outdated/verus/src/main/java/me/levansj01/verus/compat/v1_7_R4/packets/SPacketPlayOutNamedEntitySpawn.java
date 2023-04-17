package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutNamedEntitySpawn;
import me.levansj01.verus.data.transaction.tracker.ServerLocation;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;

public class SPacketPlayOutNamedEntitySpawn
extends VPacketPlayOutNamedEntitySpawn {
    private static final Field z_field;
    protected int iX;
    private static final Field yaw_field;
    private static final Field pitch_field;
    protected int iY;
    protected int iZ;
    private static final Field y_field;
    private static final Field id_field;
    private static final Field x_field;

    public ServerLocation location() {
        return new ServerLocation(this.iX, this.iY, this.iZ);
    }

    static {
        id_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "a");
        x_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "c");
        y_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "d");
        z_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "e");
        yaw_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "f");
        pitch_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "g");
    }

    public void accept(PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn) {
        this.id = (Integer)SafeReflection.fetch(id_field, packetPlayOutNamedEntitySpawn);
        this.iX = (Integer)SafeReflection.fetch(x_field, packetPlayOutNamedEntitySpawn);
        this.iY = (Integer)SafeReflection.fetch(y_field, packetPlayOutNamedEntitySpawn);
        this.iZ = (Integer)SafeReflection.fetch(z_field, packetPlayOutNamedEntitySpawn);
        this.x = (double)this.iX / 32.0;
        this.y = (double)this.iY / 32.0;
        this.z = (double)this.iZ / 32.0;
        this.yaw = (Byte)SafeReflection.fetch(yaw_field, packetPlayOutNamedEntitySpawn);
        this.pitch = (Byte)SafeReflection.fetch(pitch_field, packetPlayOutNamedEntitySpawn);
    }
}

