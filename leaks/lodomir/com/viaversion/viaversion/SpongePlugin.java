/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  org.spongepowered.api.Game
 *  org.spongepowered.api.command.CommandCallable
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.config.DefaultConfig
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.Listener
 *  org.spongepowered.api.event.game.state.GameAboutToStartServerEvent
 *  org.spongepowered.api.event.game.state.GameInitializationEvent
 *  org.spongepowered.api.event.game.state.GameStoppingServerEvent
 *  org.spongepowered.api.plugin.Plugin
 *  org.spongepowered.api.plugin.PluginContainer
 *  org.spongepowered.api.scheduler.Task
 *  org.spongepowered.api.text.serializer.TextSerializers
 */
package com.viaversion.viaversion;

import com.google.inject.Inject;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.dump.PluginInfo;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.viaversion.viaversion.sponge.commands.SpongeCommandHandler;
import com.viaversion.viaversion.sponge.commands.SpongeCommandSender;
import com.viaversion.viaversion.sponge.platform.SpongeViaAPI;
import com.viaversion.viaversion.sponge.platform.SpongeViaConfig;
import com.viaversion.viaversion.sponge.platform.SpongeViaInjector;
import com.viaversion.viaversion.sponge.platform.SpongeViaLoader;
import com.viaversion.viaversion.sponge.platform.SpongeViaTask;
import com.viaversion.viaversion.sponge.util.LoggerWrapper;
import com.viaversion.viaversion.util.GsonUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.serializer.TextSerializers;

@Plugin(id="viaversion", name="ViaVersion", version="4.1.1", authors={"_MylesC", "creeper123123321", "Gerrygames", "kennytv", "Matsv"}, description="Allow newer Minecraft versions to connect to an older server version.")
public class SpongePlugin
implements ViaPlatform<Player> {
    @Inject
    private Game game;
    @Inject
    private PluginContainer container;
    @Inject
    @DefaultConfig(sharedRoot=false)
    private File spongeConfig;
    public static final LegacyComponentSerializer COMPONENT_SERIALIZER = LegacyComponentSerializer.builder().character('\u00a7').extractUrls().build();
    private final SpongeViaAPI api = new SpongeViaAPI();
    private SpongeViaConfig conf;
    private Logger logger;

    @Listener
    public void onGameStart(GameInitializationEvent event) {
        this.logger = new LoggerWrapper(this.container.getLogger());
        this.conf = new SpongeViaConfig(this.container, this.spongeConfig.getParentFile());
        SpongeCommandHandler commandHandler = new SpongeCommandHandler();
        this.game.getCommandManager().register((Object)this, (CommandCallable)commandHandler, new String[]{"viaversion", "viaver", "vvsponge"});
        this.logger.info("ViaVersion " + this.getPluginVersion() + " is now loaded!");
        Via.init(ViaManagerImpl.builder().platform(this).commandHandler(commandHandler).injector(new SpongeViaInjector()).loader(new SpongeViaLoader(this)).build());
    }

    @Listener
    public void onServerStart(GameAboutToStartServerEvent event) {
        if (this.game.getPluginManager().getPlugin("viabackwards").isPresent()) {
            MappingDataLoader.enableMappingsCache();
        }
        this.logger.info("ViaVersion is injecting!");
        ((ViaManagerImpl)Via.getManager()).init();
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        ((ViaManagerImpl)Via.getManager()).destroy();
    }

    @Override
    public String getPlatformName() {
        return this.game.getPlatform().getImplementation().getName();
    }

    @Override
    public String getPlatformVersion() {
        return this.game.getPlatform().getImplementation().getVersion().orElse("Unknown Version");
    }

    @Override
    public String getPluginVersion() {
        return this.container.getVersion().orElse("Unknown Version");
    }

    @Override
    public PlatformTask runAsync(Runnable runnable) {
        return new SpongeViaTask(Task.builder().execute(runnable).async().submit((Object)this));
    }

    @Override
    public PlatformTask runSync(Runnable runnable) {
        return new SpongeViaTask(Task.builder().execute(runnable).submit((Object)this));
    }

    @Override
    public PlatformTask runSync(Runnable runnable, long ticks) {
        return new SpongeViaTask(Task.builder().execute(runnable).delayTicks(ticks).submit((Object)this));
    }

    @Override
    public PlatformTask runRepeatingSync(Runnable runnable, long ticks) {
        return new SpongeViaTask(Task.builder().execute(runnable).intervalTicks(ticks).submit((Object)this));
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers() {
        ViaCommandSender[] array = new ViaCommandSender[this.game.getServer().getOnlinePlayers().size()];
        int i = 0;
        for (Player player : this.game.getServer().getOnlinePlayers()) {
            array[i++] = new SpongeCommandSender((CommandSource)player);
        }
        return array;
    }

    @Override
    public void sendMessage(UUID uuid, String message) {
        String serialized = COMPONENT_SERIALIZER.serialize(COMPONENT_SERIALIZER.deserialize(message));
        this.game.getServer().getPlayer(uuid).ifPresent(player -> player.sendMessage(TextSerializers.JSON.deserialize(serialized)));
    }

    @Override
    public boolean kickPlayer(UUID uuid, String message) {
        return this.game.getServer().getPlayer(uuid).map(player -> {
            player.kick(TextSerializers.formattingCode((char)'\u00a7').deserialize(message));
            return true;
        }).orElse(false);
    }

    @Override
    public boolean isPluginEnabled() {
        return true;
    }

    @Override
    public ConfigurationProvider getConfigurationProvider() {
        return this.conf;
    }

    @Override
    public File getDataFolder() {
        return this.spongeConfig.getParentFile();
    }

    @Override
    public void onReload() {
        this.getLogger().severe("ViaVersion is already loaded, this should work fine. If you get any console errors, try rebooting.");
    }

    @Override
    public JsonObject getDump() {
        JsonObject platformSpecific = new JsonObject();
        ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
        for (PluginContainer p : this.game.getPluginManager().getPlugins()) {
            plugins.add(new PluginInfo(true, p.getName(), p.getVersion().orElse("Unknown Version"), p.getInstance().isPresent() ? p.getInstance().get().getClass().getCanonicalName() : "Unknown", p.getAuthors()));
        }
        platformSpecific.add("plugins", GsonUtil.getGson().toJsonTree(plugins));
        return platformSpecific;
    }

    @Override
    public boolean isOldClientsAllowed() {
        return true;
    }

    public SpongeViaAPI getApi() {
        return this.api;
    }

    @Override
    public SpongeViaConfig getConf() {
        return this.conf;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }
}

