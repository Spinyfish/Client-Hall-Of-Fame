package net.minecraft.network.status;

import net.minecraft.network.*;
import net.minecraft.network.status.server.*;

public interface INetHandlerStatusClient extends INetHandler
{
    void handleServerInfo(final S00PacketServerInfo p0);
    
    void handlePong(final S01PacketPong p0);
}
