package net.minecraft.network.login.server;

import net.minecraft.network.login.*;
import net.minecraft.util.*;
import java.io.*;
import net.minecraft.network.*;

public class S00PacketDisconnect implements Packet<INetHandlerLoginClient>
{
    private IChatComponent reason;
    
    public S00PacketDisconnect() {
    }
    
    public S00PacketDisconnect(final IChatComponent reasonIn) {
        this.reason = reasonIn;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.reason = buf.readChatComponent();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeChatComponent(this.reason);
    }
    
    @Override
    public void processPacket(final INetHandlerLoginClient handler) {
        handler.handleDisconnect(this);
    }
    
    public IChatComponent func_149603_c() {
        return this.reason;
    }
}
