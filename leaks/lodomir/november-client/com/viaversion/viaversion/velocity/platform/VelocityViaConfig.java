/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.velocity.platform;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.configuration.AbstractViaConfig;
import com.viaversion.viaversion.velocity.platform.VelocityViaInjector;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class VelocityViaConfig
extends AbstractViaConfig {
    private static final List<String> UNSUPPORTED = Arrays.asList("nms-player-ticking", "item-cache", "anti-xray-patch", "quick-move-action-fix", "bungee-ping-interval", "bungee-ping-save", "bungee-servers", "blockconnection-method", "change-1_9-hitbox", "change-1_14-hitbox");
    private int velocityPingInterval;
    private boolean velocityPingSave;
    private Map<String, Integer> velocityServerProtocols;

    public VelocityViaConfig(File configFile) {
        super(new File(configFile, "config.yml"));
        this.reloadConfig();
    }

    @Override
    protected void loadFields() {
        super.loadFields();
        this.velocityPingInterval = this.getInt("velocity-ping-interval", 60);
        this.velocityPingSave = this.getBoolean("velocity-ping-save", true);
        this.velocityServerProtocols = this.get("velocity-servers", Map.class, new HashMap());
    }

    @Override
    public URL getDefaultConfigURL() {
        return this.getClass().getClassLoader().getResource("assets/viaversion/config.yml");
    }

    @Override
    protected void handleConfig(Map<String, Object> config) {
        Map servers = !(config.get("velocity-servers") instanceof Map) ? new HashMap() : (Map)config.get("velocity-servers");
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
            try {
                servers.put("default", VelocityViaInjector.getLowestSupportedProtocolVersion());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        config.put("velocity-servers", servers);
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

    public int getVelocityPingInterval() {
        return this.velocityPingInterval;
    }

    public boolean isVelocityPingSave() {
        return this.velocityPingSave;
    }

    public Map<String, Integer> getVelocityServerProtocols() {
        return this.velocityServerProtocols;
    }
}

