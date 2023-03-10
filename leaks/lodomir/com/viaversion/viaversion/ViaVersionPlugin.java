/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.viaversion.viaversion;

import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.bukkit.classgenerator.ClassGenerator;
import com.viaversion.viaversion.bukkit.commands.BukkitCommandHandler;
import com.viaversion.viaversion.bukkit.commands.BukkitCommandSender;
import com.viaversion.viaversion.bukkit.listeners.ProtocolLibEnableListener;
import com.viaversion.viaversion.bukkit.platform.BukkitViaAPI;
import com.viaversion.viaversion.bukkit.platform.BukkitViaConfig;
import com.viaversion.viaversion.bukkit.platform.BukkitViaInjector;
import com.viaversion.viaversion.bukkit.platform.BukkitViaLoader;
import com.viaversion.viaversion.bukkit.platform.BukkitViaTask;
import com.viaversion.viaversion.bukkit.util.NMSUtil;
import com.viaversion.viaversion.dump.PluginInfo;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.unsupported.UnsupportedSoftwareImpl;
import com.viaversion.viaversion.util.GsonUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ViaVersionPlugin
extends JavaPlugin
implements ViaPlatform<Player> {
    private static ViaVersionPlugin instance;
    private final BukkitCommandHandler commandHandler;
    private final BukkitViaConfig conf;
    private final ViaAPI<Player> api = new BukkitViaAPI(this);
    private final List<Runnable> queuedTasks = new ArrayList<Runnable>();
    private final List<Runnable> asyncQueuedTasks = new ArrayList<Runnable>();
    private final boolean protocolSupport;
    private boolean compatSpigotBuild;
    private boolean spigot = true;
    private boolean lateBind;

    public ViaVersionPlugin() {
        instance = this;
        this.commandHandler = new BukkitCommandHandler();
        BukkitViaInjector injector = new BukkitViaInjector();
        Via.init(ViaManagerImpl.builder().platform(this).commandHandler(this.commandHandler).injector(injector).loader(new BukkitViaLoader(this)).build());
        this.conf = new BukkitViaConfig();
        this.protocolSupport = Bukkit.getPluginManager().getPlugin("ProtocolSupport") != null;
    }

    public void onLoad() {
        boolean hasProtocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null;
        ((BukkitViaInjector)Via.getManager().getInjector()).setProtocolLib(hasProtocolLib);
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        }
        catch (ClassNotFoundException e) {
            this.spigot = false;
        }
        try {
            NMSUtil.nms("PacketEncoder", "net.minecraft.network.PacketEncoder").getDeclaredField("version");
            this.compatSpigotBuild = true;
        }
        catch (Exception e) {
            this.compatSpigotBuild = false;
        }
        if (this.getServer().getPluginManager().getPlugin("ViaBackwards") != null) {
            MappingDataLoader.enableMappingsCache();
        }
        ClassGenerator.generate();
        this.lateBind = !((BukkitViaInjector)Via.getManager().getInjector()).isBinded();
        this.getLogger().info("ViaVersion " + this.getDescription().getVersion() + (this.compatSpigotBuild ? "compat" : "") + " is now loaded" + (this.lateBind ? ", waiting for boot. (late-bind)" : ", injecting!"));
        if (!this.lateBind) {
            ((ViaManagerImpl)Via.getManager()).init();
        }
    }

    public void onEnable() {
        if (this.lateBind) {
            ((ViaManagerImpl)Via.getManager()).init();
        }
        this.getCommand("viaversion").setExecutor((CommandExecutor)this.commandHandler);
        this.getCommand("viaversion").setTabCompleter((TabCompleter)this.commandHandler);
        this.getServer().getPluginManager().registerEvents((Listener)new ProtocolLibEnableListener(), (Plugin)this);
        if (this.conf.isAntiXRay() && !this.spigot) {
            this.getLogger().info("You have anti-xray on in your config, since you're not using spigot it won't fix xray!");
        }
        for (Runnable r : this.queuedTasks) {
            Bukkit.getScheduler().runTask((Plugin)this, r);
        }
        this.queuedTasks.clear();
        for (Runnable r : this.asyncQueuedTasks) {
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this, r);
        }
        this.asyncQueuedTasks.clear();
    }

    public void onDisable() {
        ((ViaManagerImpl)Via.getManager()).destroy();
    }

    @Override
    public String getPlatformName() {
        return Bukkit.getServer().getName();
    }

    @Override
    public String getPlatformVersion() {
        return Bukkit.getServer().getVersion();
    }

    @Override
    public String getPluginVersion() {
        return this.getDescription().getVersion();
    }

    @Override
    public PlatformTask runAsync(Runnable runnable) {
        if (this.isPluginEnabled()) {
            return new BukkitViaTask(this.getServer().getScheduler().runTaskAsynchronously((Plugin)this, runnable));
        }
        this.asyncQueuedTasks.add(runnable);
        return new BukkitViaTask(null);
    }

    @Override
    public PlatformTask runSync(Runnable runnable) {
        if (this.isPluginEnabled()) {
            return new BukkitViaTask(this.getServer().getScheduler().runTask((Plugin)this, runnable));
        }
        this.queuedTasks.add(runnable);
        return new BukkitViaTask(null);
    }

    @Override
    public PlatformTask runSync(Runnable runnable, long ticks) {
        return new BukkitViaTask(this.getServer().getScheduler().runTaskLater((Plugin)this, runnable, ticks));
    }

    @Override
    public PlatformTask runRepeatingSync(Runnable runnable, long ticks) {
        return new BukkitViaTask(this.getServer().getScheduler().runTaskTimer((Plugin)this, runnable, 0L, ticks));
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers() {
        ViaCommandSender[] array = new ViaCommandSender[Bukkit.getOnlinePlayers().size()];
        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            array[i++] = new BukkitCommandSender((CommandSender)player);
        }
        return array;
    }

    @Override
    public void sendMessage(UUID uuid, String message) {
        Player player = Bukkit.getPlayer((UUID)uuid);
        if (player != null) {
            player.sendMessage(message);
        }
    }

    @Override
    public boolean kickPlayer(UUID uuid, String message) {
        Player player = Bukkit.getPlayer((UUID)uuid);
        if (player != null) {
            player.kickPlayer(message);
            return true;
        }
        return false;
    }

    @Override
    public boolean isPluginEnabled() {
        return Bukkit.getPluginManager().getPlugin("ViaVersion").isEnabled();
    }

    @Override
    public ConfigurationProvider getConfigurationProvider() {
        return this.conf;
    }

    @Override
    public void onReload() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            this.getLogger().severe("ViaVersion is already loaded, we're going to kick all the players... because otherwise we'll crash because of ProtocolLib.");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(ChatColor.translateAlternateColorCodes((char)'&', (String)this.conf.getReloadDisconnectMsg()));
            }
        } else {
            this.getLogger().severe("ViaVersion is already loaded, this should work fine. If you get any console errors, try rebooting.");
        }
    }

    @Override
    public JsonObject getDump() {
        JsonObject platformSpecific = new JsonObject();
        ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            plugins.add(new PluginInfo(p.isEnabled(), p.getDescription().getName(), p.getDescription().getVersion(), p.getDescription().getMain(), p.getDescription().getAuthors()));
        }
        platformSpecific.add("plugins", GsonUtil.getGson().toJsonTree(plugins));
        return platformSpecific;
    }

    @Override
    public boolean isOldClientsAllowed() {
        return !this.protocolSupport;
    }

    @Override
    public BukkitViaConfig getConf() {
        return this.conf;
    }

    @Override
    public ViaAPI<Player> getApi() {
        return this.api;
    }

    @Override
    public final Collection<UnsupportedSoftware> getUnsupportedSoftwareClasses() {
        ArrayList<UnsupportedSoftware> list = new ArrayList<UnsupportedSoftware>(ViaPlatform.super.getUnsupportedSoftwareClasses());
        list.add(new UnsupportedSoftwareImpl.Builder().name("Yatopia").reason("You are using server software that - outside of possibly breaking ViaVersion - can also cause severe damage to your server's integrity as a whole.").addClassName("org.yatopiamc.yatopia.server.YatopiaConfig").addClassName("net.yatopia.api.event.PlayerAttackEntityEvent").addClassName("yatopiamc.org.yatopia.server.YatopiaConfig").addMethod("org.bukkit.Server", "getLastTickTime").build());
        return Collections.unmodifiableList(list);
    }

    public boolean isLateBind() {
        return this.lateBind;
    }

    public boolean isCompatSpigotBuild() {
        return this.compatSpigotBuild;
    }

    public boolean isSpigot() {
        return this.spigot;
    }

    public boolean isProtocolSupport() {
        return this.protocolSupport;
    }

    public static ViaVersionPlugin getInstance() {
        return instance;
    }
}

