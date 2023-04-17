package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutSpawnEntityLiving extends VPacketPlayOutSpawnEntityLiving
{
    private static final Field PacketPlayOutSpawnEntityLiving_a;
    
    public void accept(final PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving) {
        this.id = (int)SafeReflection.fetch(SPacketPlayOutSpawnEntityLiving.PacketPlayOutSpawnEntityLiving_a, packetPlayOutSpawnEntityLiving);
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutSpawnEntityLiving)o);
    }
    
    static {
        PacketPlayOutSpawnEntityLiving_a = SafeReflection.access(PacketPlayOutSpawnEntityLiving.class, "a");
    }
}
