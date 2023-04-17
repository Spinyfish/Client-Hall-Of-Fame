package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;
import java.math.*;

public class SPacketPlayOutKeepAlive extends VPacketPlayOutKeepAlive
{
    private static final Field id_field;
    
    static {
        id_field = SafeReflection.access(PacketPlayOutKeepAlive.class, "a");
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutKeepAlive)o);
    }
    
    public void accept(final PacketPlayOutKeepAlive packetPlayOutKeepAlive) {
        this.id = BigInteger.valueOf((long)SafeReflection.fetch(SPacketPlayOutKeepAlive.id_field, packetPlayOutKeepAlive));
    }
}
