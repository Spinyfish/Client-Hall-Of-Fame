/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;

public interface INetHandlerLoginClient
extends INetHandler {
    public void handleEncryptionRequest(S01PacketEncryptionRequest var1);

    public void handleLoginSuccess(S02PacketLoginSuccess var1);

    public void handleDisconnect(S00PacketDisconnect var1);

    public void handleEnableCompression(S03PacketEnableCompression var1);
}

