package net.minecraft.network.status.client;

import net.minecraft.network.status.*;
import java.io.*;
import net.minecraft.network.*;

public class C01PacketPing implements Packet<INetHandlerStatusServer>
{
    private long clientTime;
    
    public C01PacketPing() {
    }
    
    public C01PacketPing(final long ping) {
        this.clientTime = ping;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.clientTime = buf.readLong();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeLong(this.clientTime);
    }
    
    @Override
    public void processPacket(final INetHandlerStatusServer handler) {
        handler.processPing(this);
    }
    
    public long getClientTime() {
        return this.clientTime;
    }
}
