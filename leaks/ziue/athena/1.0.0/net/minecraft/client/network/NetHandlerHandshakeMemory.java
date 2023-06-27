package net.minecraft.client.network;

import net.minecraft.network.handshake.*;
import net.minecraft.server.*;
import net.minecraft.network.handshake.client.*;
import net.minecraft.server.network.*;
import net.minecraft.network.*;
import net.minecraft.util.*;

public class NetHandlerHandshakeMemory implements INetHandlerHandshakeServer
{
    private final MinecraftServer mcServer;
    private final NetworkManager networkManager;
    
    public NetHandlerHandshakeMemory(final MinecraftServer p_i45287_1_, final NetworkManager p_i45287_2_) {
        this.mcServer = p_i45287_1_;
        this.networkManager = p_i45287_2_;
    }
    
    @Override
    public void processHandshake(final C00Handshake packetIn) {
        this.networkManager.setConnectionState(packetIn.getRequestedState());
        this.networkManager.setNetHandler(new NetHandlerLoginServer(this.mcServer, this.networkManager));
    }
    
    @Override
    public void onDisconnect(final IChatComponent reason) {
    }
}
