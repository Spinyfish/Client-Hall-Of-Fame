package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutExplosion extends VPacketPlayOutExplosion
{
    private static final Field motY_field;
    private static final Field motX_field;
    private static final Field motZ_field;
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutExplosion)o);
    }
    
    public void accept(final PacketPlayOutExplosion packetPlayOutExplosion) {
        this.motX = (float)SafeReflection.fetch(SPacketPlayOutExplosion.motX_field, packetPlayOutExplosion);
        this.motY = (float)SafeReflection.fetch(SPacketPlayOutExplosion.motY_field, packetPlayOutExplosion);
        this.motZ = (float)SafeReflection.fetch(SPacketPlayOutExplosion.motZ_field, packetPlayOutExplosion);
    }
    
    static {
        motX_field = SafeReflection.access(PacketPlayOutExplosion.class, "f");
        motY_field = SafeReflection.access(PacketPlayOutExplosion.class, "g");
        motZ_field = SafeReflection.access(PacketPlayOutExplosion.class, "h");
    }
}
