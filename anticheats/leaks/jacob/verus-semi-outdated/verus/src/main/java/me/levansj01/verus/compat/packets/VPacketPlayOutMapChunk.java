package me.levansj01.verus.compat.packets;

import me.levansj01.verus.data.state.*;
import me.levansj01.verus.storage.*;
import me.levansj01.verus.compat.*;
import me.levansj01.verus.data.bytes.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutMapChunk extends VPacket
{
    protected int chunkX;
    protected int chunkZ;
    protected State chunkMap;
    protected boolean groundUp;
    protected boolean unload;
    private static final int count;
    
    public static ByteData wrap(final byte[] array) {
        return (ByteData)(StorageEngine.getInstance().getVerusConfig().isDirectMemory() ? NMSManager.getInstance().getNettyHandler().wrap(array, 0, array.length) : new BasicByteData(array));
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutMapChunk.count;
    }
    
    public int getChunkX() {
        return this.chunkX;
    }
    
    public int getChunkZ() {
        return this.chunkZ;
    }
    
    public State getChunkMap() {
        return this.chunkMap;
    }
    
    public boolean isGroundUp() {
        return this.groundUp;
    }
    
    public boolean isUnload() {
        return this.unload;
    }
    
    static {
        count = VPacket.count();
    }
}
