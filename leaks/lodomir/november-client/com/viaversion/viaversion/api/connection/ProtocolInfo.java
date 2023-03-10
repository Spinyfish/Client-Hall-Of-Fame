/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.connection;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.ProtocolPipeline;
import com.viaversion.viaversion.api.protocol.packet.State;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ProtocolInfo {
    public State getState();

    public void setState(State var1);

    public int getProtocolVersion();

    public void setProtocolVersion(int var1);

    public int getServerProtocolVersion();

    public void setServerProtocolVersion(int var1);

    public @Nullable String getUsername();

    public void setUsername(String var1);

    public @Nullable UUID getUuid();

    public void setUuid(UUID var1);

    public ProtocolPipeline getPipeline();

    public void setPipeline(ProtocolPipeline var1);

    public UserConnection getUser();
}

