package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutEntityTeleport;
import me.levansj01.verus.data.transaction.tracker.IServerLocation;
import me.levansj01.verus.data.transaction.tracker.ServerLocation;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;

public class SPacketPlayOutEntityTeleport
extends VPacketPlayOutEntityTeleport {
    private static final Field id_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "a");
    private static final Field pitch_field;
    private static final Field y_field;
    protected int iZ;
    private static final Field z_field;
    protected int iX;
    private static final Field x_field;
    private static final Field ground_field;
    protected int iY;
    private static final Field yaw_field;

    public void accept(PacketPlayOutEntityTeleport packetPlayOutEntityTeleport) {
        this.id = (Integer)SafeReflection.fetch(id_field, packetPlayOutEntityTeleport);
        this.iX = (Integer)SafeReflection.fetch(x_field, packetPlayOutEntityTeleport);
        this.iY = (Integer)SafeReflection.fetch(y_field, packetPlayOutEntityTeleport);
        this.iZ = (Integer)SafeReflection.fetch(z_field, packetPlayOutEntityTeleport);
        this.x = (double)this.iX / 32.0;
        this.y = (double)this.iY / 32.0;
        this.z = (double)this.iZ / 32.0;
        this.yaw = (Byte)SafeReflection.fetch(yaw_field, packetPlayOutEntityTeleport);
        this.pitch = (Byte)SafeReflection.fetch(pitch_field, packetPlayOutEntityTeleport);
        this.ground = (Boolean)SafeReflection.fetch(ground_field, packetPlayOutEntityTeleport);
    }

    @Override
    public IServerLocation location() {
        return new ServerLocation(this.iX, this.iY, this.iZ);
    }

    static {
        x_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "b");
        y_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "c");
        z_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "d");
        yaw_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "e");
        pitch_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "f");
        ground_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "onGround");
    }
}

