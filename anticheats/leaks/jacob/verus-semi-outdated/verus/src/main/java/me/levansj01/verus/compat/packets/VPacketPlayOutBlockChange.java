package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.type.enterprise.transaction.world.*;
import me.levansj01.verus.data.transaction.world.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.data.state.*;

public abstract class VPacketPlayOutBlockChange extends VPacket implements SectionUpdate
{
    private static final int count;
    protected LazyData data;
    protected int x;
    protected int y;
    protected int z;
    
    @Override
    public void after(final VerusChunk verusChunk) {
        final Section section = verusChunk.getSection(this.y >> 4);
        section.getUpdates().remove(this);
        final SectionData data = section.getData();
        if (data == null) {
            return;
        }
        data.set(this.x & 0xF, this.y & 0xF, this.z & 0xF, this.data);
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public LazyData getData() {
        return this.data;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutBlockChange.count;
    }
    
    @Override
    public void before(final VerusChunk verusChunk) {
        verusChunk.getSection(this.y >> 4).getUpdates().add(this);
    }
    
    public int getY() {
        return this.y;
    }
    
    @Override
    public State get(final int n, final int n2, final int n3) {
        return (n2 == n3) ? State.of(this.data) : null;
    }
    
    static {
        count = VPacket.count();
    }
}
