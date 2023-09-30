// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.network.status.server;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import com.google.gson.TypeAdapterFactory;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import net.minecraft.network.ServerStatusResponse;
import com.google.gson.Gson;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.Packet;

public class S00PacketServerInfo implements Packet<INetHandlerStatusClient>
{
    private static final Gson GSON;
    private ServerStatusResponse response;
    
    static {
        GSON = new GsonBuilder().registerTypeAdapter((Type)ServerStatusResponse.MinecraftProtocolVersionIdentifier.class, (Object)new ServerStatusResponse.MinecraftProtocolVersionIdentifier.Serializer()).registerTypeAdapter((Type)ServerStatusResponse.PlayerCountData.class, (Object)new ServerStatusResponse.PlayerCountData.Serializer()).registerTypeAdapter((Type)ServerStatusResponse.class, (Object)new ServerStatusResponse.Serializer()).registerTypeHierarchyAdapter((Class)IChatComponent.class, (Object)new IChatComponent.Serializer()).registerTypeHierarchyAdapter((Class)ChatStyle.class, (Object)new ChatStyle.Serializer()).registerTypeAdapterFactory((TypeAdapterFactory)new EnumTypeAdapterFactory()).create();
    }
    
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
}
