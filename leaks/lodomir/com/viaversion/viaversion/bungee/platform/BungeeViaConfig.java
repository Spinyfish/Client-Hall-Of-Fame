/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.bungee.platform;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.bungee.providers.BungeeVersionProvider;
import com.viaversion.viaversion.configuration.AbstractViaConfig;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class BungeeViaConfig
extends AbstractViaConfig {
    private static final List<String> UNSUPPORTED = Arrays.asList("nms-player-ticking", "item-cache", "anti-xray-patch", "quick-move-action-fix", "velocity-ping-interval", "velocity-ping-save", "velocity-servers", "blockconnection-method", "change-1_9-hitbox", "change-1_14-hitbox");
    private int bungeePingInterval;
    private boolean bungeePingSave;
    private Map<String, Integer> bungeeServerProtocols;

    public BungeeViaConfig(File configFile) {
        super(new File(configFile, "config.yml"));
        this.reloadConfig();
    }

    @Override
    protected void loadFields() {
        super.loadFields();
        this.bungeePingInterval = this.getInt("bungee-ping-interval", 60);
        this.bungeePingSave = this.getBoolean("bungee-ping-save", true);
        this.bungeeServerProtocols = this.get("bungee-servers", Map.class, new HashMap());
    }

    @Override
    public URL getDefaultConfigURL() {
        return BungeeViaConfig.class.getClassLoader().getResource("assets/viaversion/config.yml");
    }

    @Override
    protected void handleConfig(Map<String, Object> config) {
        Map servers = !(config.get("bungee-servers") instanceof Map) ? new HashMap() : (Map)config.get("bungee-servers");
        for (Map.Entry entry : new HashSet(servers.entrySet())) {
            if (entry.getValue() instanceof Integer) continue;
            if (entry.getValue() instanceof String) {
                ProtocolVersion found = ProtocolVersion.getClosest((String)entry.getValue());
                if (found != null) {
                    servers.put((String)entry.getKey(), found.getVersion());
                    continue;
                }
                servers.remove(entry.getKey());
                continue;
            }
            servers.remove(entry.getKey());
        }
        if (!servers.containsKey("default")) {
            servers.put("default", BungeeVersionProvider.getLowestSupportedVersion());
        }
        config.put("bungee-servers", servers);
    }

    @Override
    public List<String> getUnsupportedOptions() {
        return UNSUPPORTED;
    }

    @Override
    public boolean isItemCache() {
        return false;
    }

    @Override
    public boolean isNMSPlayerTicking() {
        return false;
    }

    public int getBungeePingInterval() {
        return this.bungeePingInterval;
    }

    public boolean isBungeePingSave() {
        return this.bungeePingSave;
    }

    public Map<String, Integer> getBungeeServerProtocols() {
        return this.bungeeServerProtocols;
    }
}

