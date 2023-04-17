package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import me.levansj01.verus.util.java.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.data.transaction.world.*;
import me.levansj01.verus.data.state.*;
import java.util.function.*;

public class SPacketPlayOutMapChunkBulk extends VPacketPlayOutMapChunkBulk
{
    private static final Field b_field;
    private static final Field c_field;
    private static final Field a_field;
    private static final Field d_field;
    
    static {
        a_field = SafeReflection.access(PacketPlayOutMapChunkBulk.class, "a");
        b_field = SafeReflection.access(PacketPlayOutMapChunkBulk.class, "b");
        c_field = SafeReflection.access(PacketPlayOutMapChunkBulk.class, "c");
        d_field = SafeReflection.access(PacketPlayOutMapChunkBulk.class, "d");
    }
    
    private static MappedChunk[] lambda$accept$0(final PacketPlayOutMapChunk.ChunkMap[] array, final int[] array2, final int[] array3) {
        final MappedChunk[] array4 = new MappedChunk[array.length];
        while (0 < array4.length) {
            final int n = array2[0];
            final int n2 = array3[0];
            final PacketPlayOutMapChunk.ChunkMap chunkMap = array[0];
            final boolean b = chunkMap == null || chunkMap.a.length == 0;
            array4[0] = new MappedChunk(n, n2, (SectionData[])(b ? null : SPacketPlayOutMapChunk.fromNMS(chunkMap, true)), b);
            int n3 = 0;
            ++n3;
        }
        return array4;
    }
    
    public void accept(final PacketPlayOutMapChunkBulk packetPlayOutMapChunkBulk) {
        this.groundUp = (boolean)SafeReflection.fetch(SPacketPlayOutMapChunkBulk.d_field, packetPlayOutMapChunkBulk);
        this.chunks = State.fast(SPacketPlayOutMapChunkBulk::lambda$accept$0);
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutMapChunkBulk)o);
    }
}
