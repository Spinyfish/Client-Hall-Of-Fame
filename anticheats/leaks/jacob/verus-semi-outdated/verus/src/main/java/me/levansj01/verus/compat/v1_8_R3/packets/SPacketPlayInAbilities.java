package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;
import me.levansj01.verus.compat.api.wrapper.*;

public class SPacketPlayInAbilities extends VPacketPlayInAbilities
{
    private static final Field f_field;
    private static final Field e_field;
    
    public void accept(final PacketPlayInAbilities packetPlayInAbilities) {
        this.abilities = new PlayerAbilities(packetPlayInAbilities.a(), packetPlayInAbilities.isFlying(), packetPlayInAbilities.c(), packetPlayInAbilities.d(), (float)SafeReflection.fetch(SPacketPlayInAbilities.e_field, packetPlayInAbilities), (float)SafeReflection.fetch(SPacketPlayInAbilities.f_field, packetPlayInAbilities));
    }
    
    static {
        e_field = SafeReflection.access(PacketPlayInAbilities.class, "e");
        f_field = SafeReflection.access(PacketPlayInAbilities.class, "f");
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInAbilities)o);
    }
}
