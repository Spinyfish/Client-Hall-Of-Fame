package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.data.state.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.data.transaction.world.*;
import java.util.*;

public abstract class VPacketPlayOutMapChunkBulk extends VPacket
{
    protected State chunks;
    private static final int count;
    protected boolean groundUp;
    
    @Override
    public int ordinal() {
        return VPacketPlayOutMapChunkBulk.count;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public boolean isGroundUp() {
        return this.groundUp;
    }
    
    static {
        count = VPacket.count();
    }
    
    public State getChunks() {
        return this.chunks;
    }
    
    public static class MappedChunk
    {
        private final int x;
        private final SectionData[] chunkMap;
        private final boolean unload;
        private final int z;
        
        @Override
        public String toString() {
            return "VPacketPlayOutMapChunkBulk.MappedChunk(x=" + this.getX() + ", z=" + this.getZ() + ", chunkMap=" + Arrays.deepToString(this.getChunkMap()) + ", unload=" + this.isUnload() + ")";
        }
        
        public SectionData[] getChunkMap() {
            return this.chunkMap;
        }
        
        public int getZ() {
            return this.z;
        }
        
        protected boolean canEqual(final Object o) {
            return o instanceof MappedChunk;
        }
        
        public MappedChunk(final int x, final int z, final SectionData[] chunkMap, final boolean unload) {
            this.x = x;
            this.z = z;
            this.chunkMap = chunkMap;
            this.unload = unload;
        }
        
        @Override
        public int hashCode() {
            final int n = 59 + this.getX();
            final int n2 = 59 + this.getZ();
            final int n3 = 59 + (this.isUnload() ? 79 : 97);
            final int n4 = 59 + Arrays.deepHashCode(this.getChunkMap());
            return 1;
        }
        
        public int getX() {
            return this.x;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof MappedChunk)) {
                return false;
            }
            final MappedChunk mappedChunk = (MappedChunk)o;
            return mappedChunk.canEqual(this) && this.getX() == mappedChunk.getX() && this.getZ() == mappedChunk.getZ() && this.isUnload() == mappedChunk.isUnload() && Arrays.deepEquals(this.getChunkMap(), mappedChunk.getChunkMap());
        }
        
        public boolean isUnload() {
            return this.unload;
        }
    }
}
