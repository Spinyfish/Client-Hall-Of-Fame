package net.minecraft.network.status.server;

import net.minecraft.network.status.*;
import java.io.*;
import net.minecraft.network.*;

public class S01PacketPong implements Packet<INetHandlerStatusClient>
{
    private long clientTime;
    
    public S01PacketPong() {
    }
    
    public S01PacketPong(final long time) {
        this.clientTime = time;
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
    public void processPacket(final INetHandlerStatusClient handler) {
        handler.handlePong(this);
    }
}
