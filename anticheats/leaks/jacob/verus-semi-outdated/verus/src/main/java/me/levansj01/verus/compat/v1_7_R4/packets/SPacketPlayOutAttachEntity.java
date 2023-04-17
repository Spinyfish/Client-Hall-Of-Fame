package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayOutAttachEntity;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayOutAttachEntity;

public class SPacketPlayOutAttachEntity
extends VPacketPlayOutAttachEntity {
    private static final Field c_field;
    private static final Field a_field;
    private static final Field b_field;

    static {
        a_field = SafeReflection.access(PacketPlayOutAttachEntity.class, "a");
        b_field = SafeReflection.access(PacketPlayOutAttachEntity.class, "b");
        c_field = SafeReflection.access(PacketPlayOutAttachEntity.class, "c");
    }

    public void accept(PacketPlayOutAttachEntity packetPlayOutAttachEntity) {
        this.leash = (byte)((Integer)SafeReflection.fetch(a_field, packetPlayOutAttachEntity)).intValue();
        this.entityId = (Integer)SafeReflection.fetch(b_field, packetPlayOutAttachEntity);
        this.attachId = (Integer)SafeReflection.fetch(c_field, packetPlayOutAttachEntity);
    }
}

