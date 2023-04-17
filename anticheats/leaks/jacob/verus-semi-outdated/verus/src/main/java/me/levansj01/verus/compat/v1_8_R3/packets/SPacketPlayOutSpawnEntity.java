package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutSpawnEntity extends VPacketPlayOutSpawnEntity
{
    private static final Field PacketPlayOutSpawnEntity_a;
    
    static {
        PacketPlayOutSpawnEntity_a = SafeReflection.access(PacketPlayOutSpawnEntity.class, "a");
    }
    
    public void accept(final PacketPlayOutSpawnEntity packetPlayOutSpawnEntity) {
        this.id = (int)SafeReflection.fetch(SPacketPlayOutSpawnEntity.PacketPlayOutSpawnEntity_a, packetPlayOutSpawnEntity);
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutSpawnEntity)o);
    }
}
