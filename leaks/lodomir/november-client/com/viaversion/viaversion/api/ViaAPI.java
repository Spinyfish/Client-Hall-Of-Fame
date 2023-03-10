/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.legacy.LegacyViaAPI;
import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;
import io.netty.buffer.ByteBuf;
import java.util.SortedSet;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ViaAPI<T> {
    default public int majorVersion() {
        return 4;
    }

    default public int apiVersion() {
        return 10;
    }

    public ServerProtocolVersion getServerVersion();

    public int getPlayerVersion(T var1);

    public int getPlayerVersion(UUID var1);

    public boolean isInjected(UUID var1);

    public @Nullable UserConnection getConnection(UUID var1);

    public String getVersion();

    public void sendRawPacket(T var1, ByteBuf var2);

    public void sendRawPacket(UUID var1, ByteBuf var2);

    public SortedSet<Integer> getSupportedVersions();

    public SortedSet<Integer> getFullSupportedVersions();

    public LegacyViaAPI<T> legacyAPI();
}

