/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.status;

import net.minecraft.network.INetHandler;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;

public interface INetHandlerStatusClient
extends INetHandler {
    public void handleServerInfo(S00PacketServerInfo var1);

    public void handlePong(S01PacketPong var1);
}

