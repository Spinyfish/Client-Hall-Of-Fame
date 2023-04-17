package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutEntityVelocity extends VPacketPlayOutEntityVelocity
{
    private static final Field motX_field;
    private static final Field motZ_field;
    private static final Field id_field;
    private static final Field motY_field;
    
    public void accept(final PacketPlayOutEntityVelocity packetPlayOutEntityVelocity) {
        this.id = (int)SafeReflection.fetch(SPacketPlayOutEntityVelocity.id_field, packetPlayOutEntityVelocity);
        this.motX = (int)SafeReflection.fetch(SPacketPlayOutEntityVelocity.motX_field, packetPlayOutEntityVelocity);
        this.motY = (int)SafeReflection.fetch(SPacketPlayOutEntityVelocity.motY_field, packetPlayOutEntityVelocity);
        this.motZ = (int)SafeReflection.fetch(SPacketPlayOutEntityVelocity.motZ_field, packetPlayOutEntityVelocity);
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutEntityVelocity)o);
    }
    
    static {
        id_field = SafeReflection.access(PacketPlayOutEntityVelocity.class, new String[] { "a", "id" });
        motX_field = SafeReflection.access(PacketPlayOutEntityVelocity.class, new String[] { "b", "x" });
        motY_field = SafeReflection.access(PacketPlayOutEntityVelocity.class, new String[] { "c", "y" });
        motZ_field = SafeReflection.access(PacketPlayOutEntityVelocity.class, new String[] { "d", "z" });
    }
}
