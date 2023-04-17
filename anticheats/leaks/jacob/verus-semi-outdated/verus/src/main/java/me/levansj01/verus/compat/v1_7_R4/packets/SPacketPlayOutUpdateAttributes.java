package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import me.levansj01.verus.compat.packets.VPacketPlayOutUpdateAttributes;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.AttributeModifier;
import net.minecraft.server.v1_7_R4.AttributeSnapshot;
import net.minecraft.server.v1_7_R4.PacketPlayOutUpdateAttributes;

public class SPacketPlayOutUpdateAttributes
extends VPacketPlayOutUpdateAttributes {
    private static final Field b_field;
    private static final Field a_field;

    public void accept(PacketPlayOutUpdateAttributes packetPlayOutUpdateAttributes) {
        this.entityId = (Integer)SafeReflection.fetch(a_field, packetPlayOutUpdateAttributes);
        List list = (List)SafeReflection.fetch(b_field, packetPlayOutUpdateAttributes);
        for (AttributeSnapshot attributeSnapshot : list) {
            VPacketPlayOutUpdateAttributes.Snapshot snapshot = new VPacketPlayOutUpdateAttributes.Snapshot(attributeSnapshot.a(), attributeSnapshot.b());
            Collection collection = attributeSnapshot.c();
            for (AttributeModifier attributeModifier : collection) {
                snapshot.getModifiers().add(new VPacketPlayOutUpdateAttributes.Snapshot.Modifier(attributeModifier.b(), attributeModifier.d(), attributeModifier.c()));
            }
            this.snapshots.add(snapshot);
        }
    }

    static {
        a_field = SafeReflection.access(PacketPlayOutUpdateAttributes.class, "a");
        b_field = SafeReflection.access(PacketPlayOutUpdateAttributes.class, "b");
    }
}

