package net.minecraft.network.status.server;

import net.minecraft.network.status.*;
import java.io.*;
import net.minecraft.network.*;
import java.lang.reflect.*;
import net.minecraft.util.*;
import com.google.gson.*;

public class S00PacketServerInfo implements Packet<INetHandlerStatusClient>
{
    private static final Gson GSON;
    private ServerStatusResponse response;
    
    public S00PacketServerInfo() {
    }
    
    public S00PacketServerInfo(final ServerStatusResponse responseIn) {
        this.response = responseIn;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.response = (ServerStatusResponse)S00PacketServerInfo.GSON.fromJson(buf.readStringFromBuffer(32767), (Class)ServerStatusResponse.class);
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeString(S00PacketServerInfo.GSON.toJson((Object)this.response));
    }
    
    @Override
    public void processPacket(final INetHandlerStatusClient handler) {
        handler.handleServerInfo(this);
    }
    
    public ServerStatusResponse getResponse() {
        return this.response;
    }
    
    static {
        GSON = new GsonBuilder().registerTypeAdapter((Type)ServerStatusResponse.MinecraftProtocolVersionIdentifier.class, (Object)new ServerStatusResponse.MinecraftProtocolVersionIdentifier.Serializer()).registerTypeAdapter((Type)ServerStatusResponse.PlayerCountData.class, (Object)new ServerStatusResponse.PlayerCountData.Serializer()).registerTypeAdapter((Type)ServerStatusResponse.class, (Object)new ServerStatusResponse.Serializer()).registerTypeHierarchyAdapter((Class)IChatComponent.class, (Object)new IChatComponent.Serializer()).registerTypeHierarchyAdapter((Class)ChatStyle.class, (Object)new ChatStyle.Serializer()).registerTypeAdapterFactory((TypeAdapterFactory)new EnumTypeAdapterFactory()).create();
    }
}
