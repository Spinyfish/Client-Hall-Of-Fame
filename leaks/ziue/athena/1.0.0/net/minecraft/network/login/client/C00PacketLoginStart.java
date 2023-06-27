package net.minecraft.network.login.client;

import net.minecraft.network.login.*;
import com.mojang.authlib.*;
import java.util.*;
import java.io.*;
import net.minecraft.network.*;

public class C00PacketLoginStart implements Packet<INetHandlerLoginServer>
{
    private GameProfile profile;
    
    public C00PacketLoginStart() {
    }
    
    public C00PacketLoginStart(final GameProfile profileIn) {
        this.profile = profileIn;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.profile = new GameProfile((UUID)null, buf.readStringFromBuffer(16));
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeString(this.profile.getName());
    }
    
    @Override
    public void processPacket(final INetHandlerLoginServer handler) {
        handler.processLoginStart(this);
    }
    
    public GameProfile getProfile() {
        return this.profile;
    }
}
