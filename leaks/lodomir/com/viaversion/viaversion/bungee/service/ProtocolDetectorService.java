/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.Callback
 *  net.md_5.bungee.api.ServerPing
 *  net.md_5.bungee.api.config.ServerInfo
 */
package com.viaversion.viaversion.bungee.service;

import com.viaversion.viaversion.BungeePlugin;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.bungee.platform.BungeeViaConfig;
import com.viaversion.viaversion.bungee.providers.BungeeVersionProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;

public class ProtocolDetectorService
implements Runnable {
    private static final Map<String, Integer> detectedProtocolIds = new ConcurrentHashMap<String, Integer>();
    private static ProtocolDetectorService instance;
    private final BungeePlugin plugin;

    public ProtocolDetectorService(BungeePlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static Integer getProtocolId(String serverName) {
        Map<String, Integer> servers = ((BungeeViaConfig)Via.getConfig()).getBungeeServerProtocols();
        Integer protocol = servers.get(serverName);
        if (protocol != null) {
            return protocol;
        }
        Integer detectedProtocol = detectedProtocolIds.get(serverName);
        if (detectedProtocol != null) {
            return detectedProtocol;
        }
        Integer defaultProtocol = servers.get("default");
        if (defaultProtocol != null) {
            return defaultProtocol;
        }
        return BungeeVersionProvider.getLowestSupportedVersion();
    }

    @Override
    public void run() {
        for (Map.Entry lists : this.plugin.getProxy().getServers().entrySet()) {
            ProtocolDetectorService.probeServer((ServerInfo)lists.getValue());
        }
    }

    public static void probeServer(ServerInfo serverInfo) {
        final String key = serverInfo.getName();
        serverInfo.ping((Callback)new Callback<ServerPing>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void done(ServerPing serverPing, Throwable throwable) {
                if (throwable == null && serverPing != null && serverPing.getVersion() != null && serverPing.getVersion().getProtocol() > 0) {
                    detectedProtocolIds.put(key, serverPing.getVersion().getProtocol());
                    if (((BungeeViaConfig)Via.getConfig()).isBungeePingSave()) {
                        Map<String, Integer> servers = ((BungeeViaConfig)Via.getConfig()).getBungeeServerProtocols();
                        Integer protocol = servers.get(key);
                        if (protocol != null && protocol.intValue() == serverPing.getVersion().getProtocol()) {
                            return;
                        }
                        ConfigurationProvider configurationProvider = Via.getPlatform().getConfigurationProvider();
                        synchronized (configurationProvider) {
                            servers.put(key, serverPing.getVersion().getProtocol());
                        }
                        Via.getPlatform().getConfigurationProvider().saveConfig();
                    }
                }
            }
        });
    }

    public static Map<String, Integer> getDetectedIds() {
        return new HashMap<String, Integer>(detectedProtocolIds);
    }

    public static ProtocolDetectorService getInstance() {
        return instance;
    }

    public BungeePlugin getPlugin() {
        return this.plugin;
    }
}

