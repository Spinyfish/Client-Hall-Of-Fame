/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  org.slf4j.Logger
 *  org.spongepowered.api.config.ConfigDir
 *  org.spongepowered.api.event.Listener
 *  org.spongepowered.api.event.Order
 *  org.spongepowered.api.event.game.state.GameInitializationEvent
 *  org.spongepowered.api.plugin.Dependency
 *  org.spongepowered.api.plugin.Plugin
 */
package de.gerrygames.viarewind;

import com.google.inject.Inject;
import com.viaversion.viaversion.sponge.util.LoggerWrapper;
import de.gerrygames.viarewind.api.ViaRewindConfigImpl;
import de.gerrygames.viarewind.api.ViaRewindPlatform;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id="viarewind", name="ViaRewind", version="2.0.3-SNAPSHOT", authors={"Gerrygames"}, dependencies={@Dependency(id="viaversion"), @Dependency(id="viabackwards", optional=true)}, url="https://viaversion.com/rewind")
public class SpongePlugin
implements ViaRewindPlatform {
    private java.util.logging.Logger logger;
    @Inject
    private Logger loggerSlf4j;
    @Inject
    @ConfigDir(sharedRoot=false)
    private Path configDir;

    @Listener(order=Order.LATE)
    public void onGameStart(GameInitializationEvent e) {
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

