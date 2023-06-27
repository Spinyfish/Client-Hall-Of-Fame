package net.minecraft.network.status.client;

import net.minecraft.network.status.*;
import java.io.*;
import net.minecraft.network.*;

public class C00PacketServerQuery implements Packet<INetHandlerStatusServer>
{
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
    }
    
    @Override
    public void processPacket(final INetHandlerStatusServer handler) {
        handler.processServerQuery(this);
    }
}
