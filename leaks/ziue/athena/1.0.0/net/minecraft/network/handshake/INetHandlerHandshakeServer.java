package net.minecraft.network.handshake;

import net.minecraft.network.*;
import net.minecraft.network.handshake.client.*;

public interface INetHandlerHandshakeServer extends INetHandler
{
    void processHandshake(final C00Handshake p0);
}
