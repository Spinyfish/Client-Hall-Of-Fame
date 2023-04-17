package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import me.levansj01.verus.util.java.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.data.transaction.tracker.*;

public class SPacketPlayOutNamedEntitySpawn extends VPacketPlayOutNamedEntitySpawn
{
    protected int iX;
    private static final Field x_field;
    private static final Field id_field;
    private static final Field yaw_field;
    private static final Field y_field;
    protected int iY;
    protected int iZ;
    private static final Field pitch_field;
    private static final Field z_field;
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutNamedEntitySpawn)o);
    }
    
    public void accept(final PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn) {
        this.id = (int)SafeReflection.fetch(SPacketPlayOutNamedEntitySpawn.id_field, packetPlayOutNamedEntitySpawn);
        this.iX = (int)SafeReflection.fetch(SPacketPlayOutNamedEntitySpawn.x_field, packetPlayOutNamedEntitySpawn);
        this.iY = (int)SafeReflection.fetch(SPacketPlayOutNamedEntitySpawn.y_field, packetPlayOutNamedEntitySpawn);
        this.iZ = (int)SafeReflection.fetch(SPacketPlayOutNamedEntitySpawn.z_field, packetPlayOutNamedEntitySpawn);
        this.x = this.iX / 32.0;
        this.y = this.iY / 32.0;
        this.z = this.iZ / 32.0;
        this.yaw = (byte)SafeReflection.fetch(SPacketPlayOutNamedEntitySpawn.yaw_field, packetPlayOutNamedEntitySpawn);
        this.pitch = (byte)SafeReflection.fetch(SPacketPlayOutNamedEntitySpawn.pitch_field, packetPlayOutNamedEntitySpawn);
    }
    
    static {
        id_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "a");
        x_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "c");
        y_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "d");
        z_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "e");
        yaw_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "f");
        pitch_field = SafeReflection.access(PacketPlayOutNamedEntitySpawn.class, "g");
    }
    
    public PacketPlayOutNamedEntitySpawn create() {
        final PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn();
        SafeReflection.set(SPacketPlayOutNamedEntitySpawn.id_field, packetPlayOutNamedEntitySpawn, this.id);
        SafeReflection.set(SPacketPlayOutNamedEntitySpawn.x_field, packetPlayOutNamedEntitySpawn, MathHelper.floor(this.x * 32.0));
        SafeReflection.set(SPacketPlayOutNamedEntitySpawn.y_field, packetPlayOutNamedEntitySpawn, MathHelper.floor(this.y * 32.0));
        SafeReflection.set(SPacketPlayOutNamedEntitySpawn.z_field, packetPlayOutNamedEntitySpawn, MathHelper.floor(this.z * 32.0));
        SafeReflection.set(SPacketPlayOutNamedEntitySpawn.yaw_field, packetPlayOutNamedEntitySpawn, this.yaw);
        SafeReflection.set(SPacketPlayOutNamedEntitySpawn.pitch_field, packetPlayOutNamedEntitySpawn, this.pitch);
        return packetPlayOutNamedEntitySpawn;
    }
    
    @Override
    public IServerLocation location() {
        return (IServerLocation)new ServerLocation(this.iX, this.iY, this.iZ);
    }
}
