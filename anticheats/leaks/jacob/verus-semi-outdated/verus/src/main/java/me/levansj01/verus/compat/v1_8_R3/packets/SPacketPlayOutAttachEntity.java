package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayOutAttachEntity extends VPacketPlayOutAttachEntity
{
    private static final Field a_field;
    private static final Field b_field;
    private static final Field c_field;
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutAttachEntity)o);
    }
    
    public void accept(final PacketPlayOutAttachEntity packetPlayOutAttachEntity) {
        this.leash = (byte)(int)SafeReflection.fetch(SPacketPlayOutAttachEntity.a_field, packetPlayOutAttachEntity);
        this.entityId = (int)SafeReflection.fetch(SPacketPlayOutAttachEntity.b_field, packetPlayOutAttachEntity);
        this.attachId = (int)SafeReflection.fetch(SPacketPlayOutAttachEntity.c_field, packetPlayOutAttachEntity);
    }
    
    static {
        a_field = SafeReflection.access(PacketPlayOutAttachEntity.class, "a");
        b_field = SafeReflection.access(PacketPlayOutAttachEntity.class, "b");
        c_field = SafeReflection.access(PacketPlayOutAttachEntity.class, "c");
    }
}
