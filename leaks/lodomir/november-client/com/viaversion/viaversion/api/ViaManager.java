/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api;

import com.viaversion.viaversion.api.command.ViaVersionCommand;
import com.viaversion.viaversion.api.connection.ConnectionManager;
import com.viaversion.viaversion.api.debug.DebugHandler;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.ProtocolManager;
import java.util.Set;

public interface ViaManager {
    public ProtocolManager getProtocolManager();

    public ViaPlatform<?> getPlatform();

    public ConnectionManager getConnectionManager();

    public ViaProviders getProviders();

    public ViaInjector getInjector();

    public ViaVersionCommand getCommandHandler();

    public ViaPlatformLoader getLoader();

    @Deprecated
    default public boolean isDebug() {
        return this.debugHandler().enabled();
    }

    @Deprecated
    default public void setDebug(boolean debug) {
        this.debugHandler().setEnabled(debug);
    }

    public DebugHandler debugHandler();

    public Set<String> getSubPlatforms();

    public void addEnableListener(Runnable var1);
}

