package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import me.levansj01.verus.util.java.*;
import net.minecraft.server.v1_8_R3.*;
import java.util.*;

public class SPacketPlayOutUpdateAttributes extends VPacketPlayOutUpdateAttributes
{
    private static final Field a_field;
    private static final Field b_field;
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutUpdateAttributes)o);
    }
    
    public void accept(final PacketPlayOutUpdateAttributes packetPlayOutUpdateAttributes) {
        this.entityId = (int)SafeReflection.fetch(SPacketPlayOutUpdateAttributes.a_field, packetPlayOutUpdateAttributes);
        for (final PacketPlayOutUpdateAttributes.AttributeSnapshot attributeSnapshot : (List)SafeReflection.fetch(SPacketPlayOutUpdateAttributes.b_field, packetPlayOutUpdateAttributes)) {
            final Snapshot snapshot = new Snapshot(attributeSnapshot.a(), attributeSnapshot.b());
            for (final AttributeModifier attributeModifier : attributeSnapshot.c()) {
                snapshot.getModifiers().add(new Snapshot.Modifier(attributeModifier.b(), attributeModifier.d(), attributeModifier.c()));
            }
            this.snapshots.add(snapshot);
        }
    }
    
    static {
        a_field = SafeReflection.access(PacketPlayOutUpdateAttributes.class, "a");
        b_field = SafeReflection.access(PacketPlayOutUpdateAttributes.class, "b");
    }
}
