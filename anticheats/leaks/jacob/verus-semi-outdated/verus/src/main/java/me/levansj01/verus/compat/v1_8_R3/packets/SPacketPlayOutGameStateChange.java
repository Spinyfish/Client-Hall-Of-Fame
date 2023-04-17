package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutGameStateChange extends VPacketPlayOutGameStateChange
{
    private static final Field c_field;
    private static final Field b_field;
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutGameStateChange)o);
    }
    
    static {
        b_field = SafeReflection.access(PacketPlayOutGameStateChange.class, "b");
        c_field = SafeReflection.access(PacketPlayOutGameStateChange.class, "c");
    }
    
    public void accept(final PacketPlayOutGameStateChange packetPlayOutGameStateChange) {
        this.setting = (int)SafeReflection.fetch(SPacketPlayOutGameStateChange.b_field, packetPlayOutGameStateChange);
        this.value = (float)SafeReflection.fetch(SPacketPlayOutGameStateChange.c_field, packetPlayOutGameStateChange);
    }
}
