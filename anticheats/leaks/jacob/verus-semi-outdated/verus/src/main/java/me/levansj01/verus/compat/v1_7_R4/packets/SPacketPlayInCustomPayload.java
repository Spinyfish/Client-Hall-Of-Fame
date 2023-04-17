package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayInCustomPayload;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayInCustomPayload;

public class SPacketPlayInCustomPayload
extends VPacketPlayInCustomPayload {
    private static final Field tag_field = SafeReflection.access(PacketPlayInCustomPayload.class, "tag");

    public void accept(PacketPlayInCustomPayload packetPlayInCustomPayload) {
        this.channel = (String)SafeReflection.fetch(tag_field, packetPlayInCustomPayload);
        byte[] byArray = packetPlayInCustomPayload.e();
        this.size = byArray == null ? 0 : byArray.length;
    }
}

