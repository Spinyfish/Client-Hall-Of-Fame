/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.velocitypowered.api.event.PostOrder
 *  com.velocitypowered.api.event.Subscribe
 *  com.velocitypowered.api.event.proxy.ProxyInitializeEvent
 *  com.velocitypowered.api.plugin.Dependency
 *  com.velocitypowered.api.plugin.Plugin
 *  com.velocitypowered.api.plugin.annotation.DataDirectory
 *  org.slf4j.Logger
 */
package de.gerrygames.viarewind;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.viaversion.viaversion.sponge.util.LoggerWrapper;
import de.gerrygames.viarewind.api.ViaRewindConfigImpl;
import de.gerrygames.viarewind.api.ViaRewindPlatform;
import java.nio.file.Path;
import org.slf4j.Logger;

@Plugin(id="viarewind", name="ViaRewind", version="2.0.3-SNAPSHOT", authors={"Gerrygames"}, dependencies={@Dependency(id="viaversion"), @Dependency(id="viabackwards", optional=true)}, url="https://viaversion.com/rewind")
public class VelocityPlugin
implements ViaRewindPlatform {
    private java.util.logging.Logger logger;
    @Inject
    private Logger loggerSlf4j;
    @Inject
    @DataDirectory
    private Path configDir;

    @Subscribe(order=PostOrder.LATE)
    public void onProxyStart(ProxyInitializeEvent e) {
        this.logger = new LoggerWrapper(this.loggerSlf4j);
        ViaRewindConfigImpl conf = new ViaRewindConfigImpl(this.configDir.resolve("config.yml").toFile());
        conf.reloadConfig();
        this.init(conf);
    }

    @Override
    public java.util.logging.Logger getLogger() {
        return this.logger;
    }
}

