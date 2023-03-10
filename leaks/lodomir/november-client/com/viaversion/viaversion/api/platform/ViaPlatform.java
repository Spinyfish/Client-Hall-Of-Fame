/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.platform;

import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Logger;

public interface ViaPlatform<T> {
    public Logger getLogger();

    public String getPlatformName();

    public String getPlatformVersion();

    default public boolean isProxy() {
        return false;
    }

    public String getPluginVersion();

    public PlatformTask runAsync(Runnable var1);

    public PlatformTask runSync(Runnable var1);

    public PlatformTask runSync(Runnable var1, long var2);

    public PlatformTask runRepeatingSync(Runnable var1, long var2);

    public ViaCommandSender[] getOnlinePlayers();

    public void sendMessage(UUID var1, String var2);

    public boolean kickPlayer(UUID var1, String var2);

    default public boolean disconnect(UserConnection connection, String message) {
        if (connection.isClientSide()) {
            return false;
        }
        UUID uuid = connection.getProtocolInfo().getUuid();
        if (uuid == null) {
            return false;
        }
        return this.kickPlayer(uuid, message);
    }

    public boolean isPluginEnabled();

    public ViaAPI<T> getApi();

    public ViaVersionConfig getConf();

    public ConfigurationProvider getConfigurationProvider();

    public File getDataFolder();

    public void onReload();

    public JsonObject getDump();

    public boolean isOldClientsAllowed();

    default public Collection<UnsupportedSoftware> getUnsupportedSoftwareClasses() {
        return Collections.emptyList();
    }
}

