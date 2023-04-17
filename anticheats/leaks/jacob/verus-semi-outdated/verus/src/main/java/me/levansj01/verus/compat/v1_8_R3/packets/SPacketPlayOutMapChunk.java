package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.data.transaction.world.*;
import me.levansj01.verus.data.bytes.*;
import me.levansj01.verus.util.java.*;
import me.levansj01.verus.data.state.*;
import java.util.function.*;

public class SPacketPlayOutMapChunk extends VPacketPlayOutMapChunk
{
    private static final Field b_field;
    private static final Field d_field;
    private static final Field a_field;
    private static final Field c_field;
    
    public static SectionData[] fromNMS(final PacketPlayOutMapChunk.ChunkMap chunkMap, final boolean b) {
        final SectionData[] array = new SectionData[16];
        if (chunkMap == null) {
            return array;
        }
        final ByteData wrap = VPacketPlayOutMapChunk.wrap(chunkMap.a);
        final int b2 = chunkMap.b;
        while (true) {
            if ((b2 >> 0 & 0x1) != 0x0) {
                array[0] = (SectionData)VPacketPlayOutMapChunk.SectionDataImpl.of(wrap, 0);
                int n = 0;
                ++n;
            }
            else if (b) {
                array[0] = (SectionData)new BlankSection();
            }
            int n2 = 0;
            ++n2;
        }
    }
    
    public void accept(final PacketPlayOutMapChunk packetPlayOutMapChunk) {
        this.chunkX = (int)SafeReflection.fetch(SPacketPlayOutMapChunk.a_field, packetPlayOutMapChunk);
        this.chunkZ = (int)SafeReflection.fetch(SPacketPlayOutMapChunk.b_field, packetPlayOutMapChunk);
        this.groundUp = (boolean)SafeReflection.fetch(SPacketPlayOutMapChunk.d_field, packetPlayOutMapChunk);
        final PacketPlayOutMapChunk.ChunkMap chunkMap = (PacketPlayOutMapChunk.ChunkMap)SafeReflection.fetch(SPacketPlayOutMapChunk.c_field, packetPlayOutMapChunk);
        this.unload = (chunkMap == null || (chunkMap.a.length == 0 && this.groundUp));
        this.chunkMap = (this.unload ? State.of(null) : State.fast(this::lambda$accept$0));
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutMapChunk)o);
    }
    
    private SectionData[] lambda$accept$0(final PacketPlayOutMapChunk.ChunkMap chunkMap) {
        return fromNMS(chunkMap, this.groundUp);
    }
    
    static {
        a_field = SafeReflection.access(PacketPlayOutMapChunk.class, "a");
        b_field = SafeReflection.access(PacketPlayOutMapChunk.class, "b");
        c_field = SafeReflection.access(PacketPlayOutMapChunk.class, "c");
        d_field = SafeReflection.access(PacketPlayOutMapChunk.class, "d");
    }
}
