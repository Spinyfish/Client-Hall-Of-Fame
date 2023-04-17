package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import me.levansj01.verus.util.java.*;
import me.levansj01.verus.compat.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.*;

public class SPacketPlayOutMultiBlockChange extends VPacketPlayOutMultiBlockChange
{
    private static final Field b_field;
    private static final Field a_field;
    
    static {
        a_field = SafeReflection.access(PacketPlayOutMultiBlockChange.class, new String[] { "a", "chunkCoordIntPair" });
        b_field = SafeReflection.access(PacketPlayOutMultiBlockChange.class, new String[] { "b", "multiBlockChangeInfoArray" });
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutMultiBlockChange)o);
    }
    
    public void accept(final PacketPlayOutMultiBlockChange packetPlayOutMultiBlockChange) {
        final ChunkCoordIntPair chunkCoordIntPair = (ChunkCoordIntPair)SafeReflection.fetch(SPacketPlayOutMultiBlockChange.a_field, packetPlayOutMultiBlockChange);
        this.chunkX = chunkCoordIntPair.x;
        this.chunkZ = chunkCoordIntPair.z;
        final PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[] array = (PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[])SafeReflection.fetch(SPacketPlayOutMultiBlockChange.b_field, packetPlayOutMultiBlockChange);
        this.changes = new BlockChange[array.length];
        while (0 < array.length) {
            final PacketPlayOutMultiBlockChange.MultiBlockChangeInfo multiBlockChangeInfo = array[0];
            final BlockPosition a = multiBlockChangeInfo.a();
            this.changes[0] = new BlockChange(a.getX() & 0xF, a.getY(), a.getZ() & 0xF, NMSManager.getInstance().toLazy(multiBlockChangeInfo.c()));
            int n = 0;
            ++n;
        }
    }
}
