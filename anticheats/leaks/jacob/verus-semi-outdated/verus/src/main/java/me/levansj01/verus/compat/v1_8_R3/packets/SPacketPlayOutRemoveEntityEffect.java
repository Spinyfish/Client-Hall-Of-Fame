package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutRemoveEntityEffect extends VPacketPlayOutRemoveEntityEffect
{
    private static final Field a_field;
    private static final Field b_field;
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutRemoveEntityEffect)o);
    }
    
    static {
        a_field = SafeReflection.access(PacketPlayOutRemoveEntityEffect.class, "a");
        b_field = SafeReflection.access(PacketPlayOutRemoveEntityEffect.class, "b");
    }
    
    public void accept(final PacketPlayOutRemoveEntityEffect packetPlayOutRemoveEntityEffect) {
        this.id = (int)SafeReflection.fetch(SPacketPlayOutRemoveEntityEffect.a_field, packetPlayOutRemoveEntityEffect);
        this.effect = (int)SafeReflection.fetch(SPacketPlayOutRemoveEntityEffect.b_field, packetPlayOutRemoveEntityEffect);
    }
}
