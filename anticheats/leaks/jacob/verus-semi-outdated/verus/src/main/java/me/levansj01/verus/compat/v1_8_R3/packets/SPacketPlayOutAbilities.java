package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutAbilities extends VPacketPlayOutAbilities
{
    private static final Field f_field;
    private static final Field e_field;
    
    static {
        e_field = SafeReflection.access(PacketPlayOutAbilities.class, "e");
        f_field = SafeReflection.access(PacketPlayOutAbilities.class, "f");
    }
    
    public void accept(final PacketPlayOutAbilities packetPlayOutAbilities) {
        this.isInvulnerable = packetPlayOutAbilities.a();
        this.isFlying = packetPlayOutAbilities.b();
        this.canFly = packetPlayOutAbilities.c();
        this.canInstantlyBuild = packetPlayOutAbilities.d();
        this.walkSpeed = (float)SafeReflection.fetch(SPacketPlayOutAbilities.e_field, packetPlayOutAbilities);
        this.flySpeed = (float)SafeReflection.fetch(SPacketPlayOutAbilities.f_field, packetPlayOutAbilities);
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutAbilities)o);
    }
}
