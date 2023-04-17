package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import me.levansj01.verus.util.java.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.data.transaction.tracker.*;

public class SPacketPlayOutEntityTeleport extends VPacketPlayOutEntityTeleport
{
    protected int iZ;
    private static final Field id_field;
    private static final Field ground_field;
    protected int iY;
    protected int iX;
    private static final Field z_field;
    private static final Field x_field;
    private static final Field pitch_field;
    private static final Field yaw_field;
    private static final Field y_field;
    
    public PacketPlayOutEntityTeleport create() {
        final PacketPlayOutEntityTeleport packetPlayOutEntityTeleport = new PacketPlayOutEntityTeleport();
        SafeReflection.set(SPacketPlayOutEntityTeleport.id_field, packetPlayOutEntityTeleport, this.id);
        SafeReflection.set(SPacketPlayOutEntityTeleport.x_field, packetPlayOutEntityTeleport, MathHelper.floor(this.x * 32.0));
        SafeReflection.set(SPacketPlayOutEntityTeleport.y_field, packetPlayOutEntityTeleport, MathHelper.floor(this.y * 32.0));
        SafeReflection.set(SPacketPlayOutEntityTeleport.z_field, packetPlayOutEntityTeleport, MathHelper.floor(this.z * 32.0));
        SafeReflection.set(SPacketPlayOutEntityTeleport.yaw_field, packetPlayOutEntityTeleport, this.yaw);
        SafeReflection.set(SPacketPlayOutEntityTeleport.pitch_field, packetPlayOutEntityTeleport, this.pitch);
        SafeReflection.set(SPacketPlayOutEntityTeleport.ground_field, packetPlayOutEntityTeleport, this.ground);
        return packetPlayOutEntityTeleport;
    }
    
    public void accept(final PacketPlayOutEntityTeleport packetPlayOutEntityTeleport) {
        this.id = (int)SafeReflection.fetch(SPacketPlayOutEntityTeleport.id_field, packetPlayOutEntityTeleport);
        this.iX = (int)SafeReflection.fetch(SPacketPlayOutEntityTeleport.x_field, packetPlayOutEntityTeleport);
        this.iY = (int)SafeReflection.fetch(SPacketPlayOutEntityTeleport.y_field, packetPlayOutEntityTeleport);
        this.iZ = (int)SafeReflection.fetch(SPacketPlayOutEntityTeleport.z_field, packetPlayOutEntityTeleport);
        this.x = this.iX / 32.0;
        this.y = this.iY / 32.0;
        this.z = this.iZ / 32.0;
        this.yaw = (byte)SafeReflection.fetch(SPacketPlayOutEntityTeleport.yaw_field, packetPlayOutEntityTeleport);
        this.pitch = (byte)SafeReflection.fetch(SPacketPlayOutEntityTeleport.pitch_field, packetPlayOutEntityTeleport);
        this.ground = (boolean)SafeReflection.fetch(SPacketPlayOutEntityTeleport.ground_field, packetPlayOutEntityTeleport);
    }
    
    static {
        id_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "a");
        x_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "b");
        y_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "c");
        z_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "d");
        yaw_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "e");
        pitch_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "f");
        ground_field = SafeReflection.access(PacketPlayOutEntityTeleport.class, "g");
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutEntityTeleport)o);
    }
    
    @Override
    public IServerLocation location() {
        return (IServerLocation)new ServerLocation(this.iX, this.iY, this.iZ);
    }
}
