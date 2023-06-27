package net.minecraft.network.login;

import net.minecraft.network.*;
import net.minecraft.network.login.server.*;

public interface INetHandlerLoginClient extends INetHandler
{
    void handleEncryptionRequest(final S01PacketEncryptionRequest p0);
    
    void handleLoginSuccess(final S02PacketLoginSuccess p0);
    
    void handleDisconnect(final S00PacketDisconnect p0);
    
    void handleEnableCompression(final S03PacketEnableCompression p0);
}
