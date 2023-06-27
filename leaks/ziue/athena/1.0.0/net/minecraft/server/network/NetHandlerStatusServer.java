package net.minecraft.server.network;

import net.minecraft.network.status.*;
import net.minecraft.server.*;
import net.minecraft.network.*;
import net.minecraft.network.status.client.*;
import net.minecraft.network.status.server.*;
import net.minecraft.util.*;

public class NetHandlerStatusServer implements INetHandlerStatusServer
{
    private static final IChatComponent field_183007_a;
    private final MinecraftServer server;
    private final NetworkManager networkManager;
    private boolean field_183008_d;
    
    public NetHandlerStatusServer(final MinecraftServer serverIn, final NetworkManager netManager) {
        this.server = serverIn;
        this.networkManager = netManager;
    }
    
    @Override
    public void onDisconnect(final IChatComponent reason) {
    }
    
    @Override
    public void processServerQuery(final C00PacketServerQuery packetIn) {
        if (this.field_183008_d) {
            this.networkManager.closeChannel(NetHandlerStatusServer.field_183007_a);
        }
        else {
            this.field_183008_d = true;
            this.networkManager.sendPacket(new S00PacketServerInfo(this.server.getServerStatusResponse()));
        }
    }
    
    @Override
    public void processPing(final C01PacketPing packetIn) {
        this.networkManager.sendPacket(new S01PacketPong(packetIn.getClientTime()));
        this.networkManager.closeChannel(NetHandlerStatusServer.field_183007_a);
    }
    
    static {
        field_183007_a = new ChatComponentText("Status request has been handled.");
    }
}
