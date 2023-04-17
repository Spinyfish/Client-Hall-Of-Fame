package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.util.*;
import me.levansj01.verus.data.state.*;
import me.levansj01.verus.type.enterprise.transaction.world.*;
import me.levansj01.verus.data.transaction.world.*;

public abstract class VPacketPlayOutMultiBlockChange extends VPacket
{
    protected int chunkX;
    protected BlockChange[] changes;
    protected int chunkZ;
    private static final int count;
    
    static {
        count = VPacket.count();
    }
    
    public BlockChange[] getChanges() {
        return this.changes;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutMultiBlockChange.count;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public int getChunkZ() {
        return this.chunkZ;
    }
    
    public int getChunkX() {
        return this.chunkX;
    }
    
    public static class BlockChange implements IBlockPosition, SectionUpdate
    {
        private final int y;
        private final int x;
        private final int z;
        private final LazyData data;
        
        @Override
        public int getX() {
            return this.x;
        }
        
        @Override
        public int getY() {
            return this.y;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof BlockChange)) {
                return false;
            }
            final BlockChange blockChange = (BlockChange)o;
            if (!blockChange.canEqual(this)) {
                return false;
            }
            if (this.getX() != blockChange.getX()) {
                return false;
            }
            if (this.getY() != blockChange.getY()) {
                return false;
            }
            if (this.getZ() != blockChange.getZ()) {
                return false;
            }
            final LazyData data = this.getData();
            final LazyData data2 = blockChange.getData();
            if (data == null) {
                if (data2 == null) {
                    return true;
                }
            }
            else if (data.equals(data2)) {
                return true;
            }
            return false;
        }
        
        public BlockChange(final int x, final int y, final int z, final LazyData data) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.data = data;
        }
        
        @Override
        public String toString() {
            return "VPacketPlayOutMultiBlockChange.BlockChange(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", data=" + this.getData() + ")";
        }
        
        protected boolean canEqual(final Object o) {
            return o instanceof BlockChange;
        }
        
        public LazyData getData() {
            return this.data;
        }
        
        @Override
        public int hashCode() {
            final int n = 59 + this.getX();
            final int n2 = 59 + this.getY();
            final int n3 = 59 + this.getZ();
            final LazyData data = this.getData();
            final int n4 = 59 + ((data == null) ? 43 : data.hashCode());
            return 1;
        }
        
        @Override
        public State get(final int n, final int n2, final int n3) {
            return (n2 == n3) ? State.of(this.data) : null;
        }
        
        @Override
        public void before(final VerusChunk verusChunk) {
            verusChunk.getSection(this.y >> 4).getUpdates().add(this);
        }
        
        @Override
        public void after(final VerusChunk verusChunk) {
            final Section section = verusChunk.getSection(this.y >> 4);
            section.getUpdates().remove(this);
            final SectionData data = section.getData();
            if (data == null) {
                return;
            }
            data.set(this.x, this.y & 0xF, this.z, this.data);
        }
        
        @Override
        public int getZ() {
            return this.z;
        }
    }
}
