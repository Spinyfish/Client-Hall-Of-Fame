package me.levansj01.verus.compat;

import com.google.common.cache.CacheBuilder;
import com.lunarclient.bukkitapi.LunarClientAPI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import me.levansj01.launcher.VerusLauncher;
import me.levansj01.verus.compat.api.wrapper.BlockPosition;
import me.levansj01.verus.compat.api.wrapper.PlayerAbilities;
import me.levansj01.verus.compat.netty.NettyHandler;
import me.levansj01.verus.compat.netty.UnshadedNettyHandler;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.state.State;
import me.levansj01.verus.data.transaction.world.LazyData;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.IBlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;
import com.viaversion.viaversion.api.Via;

public abstract class NMSManager<D> {
    private static NMSManager<?> nmsManager = null;
    protected ServerVersion serverVersion;
    private final NettyHandler nettyHandler = this.newNettyHandler();
    protected boolean protocolsupportplugin;
    protected boolean viaversionplugin;
    protected boolean lunarClientAPI;
    protected boolean floodGateAPI;
    protected boolean fastUtil;

    public static NMSManager<?> getInstance() {
        return nmsManager == null ? (nmsManager = newInstance()) : nmsManager;
    }

    private static NMSManager<?> newInstance() {
        try {
            String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
            NMSManager<?> nmsManager = (NMSManager)Class.forName(NMSManager.class.getName().replace(".NMSManager", "." + version + ".NMSManager")).newInstance();
            ServerVersion serverVersion = ServerVersion.valueOf(version);
            nmsManager.setServerVersion(serverVersion);
            if (serverVersion.afterEq(ServerVersion.v1_17_R1) && Bukkit.getPluginManager().isPluginEnabled("ViaBackwards")) {
                try {
                    //V1_17Remapper.remap();
                } catch (Throwable var4) {
                    VerusLauncher.getPlugin().getLogger().log(Level.SEVERE, "Failed to inject protocol changes into ViaVersion, ", var4);
                }
            }

            return nmsManager;
        } catch (Throwable var5) {
            throw new IllegalArgumentException(var5);
        }
    }

    public NMSManager() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        this.protocolsupportplugin = pluginManager.isPluginEnabled("ProtocolSupport");
        this.viaversionplugin = pluginManager.isPluginEnabled("ViaVersion");
        this.lunarClientAPI = pluginManager.isPluginEnabled("LunarClient-API");
        this.floodGateAPI = pluginManager.isPluginEnabled("floodgate");

        try {
            Class.forName("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap");
            this.fastUtil = true;
        } catch (ClassNotFoundException var3) {
        }

    }

    public abstract boolean isRunning();

    public void syncCommands(List<Command> commands) {
    }

    public NettyHandler getNettyHandler() {
        return this.nettyHandler;
    }

    protected NettyHandler newNettyHandler() {
        return new UnshadedNettyHandler();
    }

    public ExecutorService getDataExecutorService(String name) {
        return Executors.newCachedThreadPool(this.nettyHandler.newThreadFactory(name));
    }

    public boolean isRunningLunarClient(Player player) {
        return this.lunarClientAPI && LunarClientAPI.getInstance().isRunningLunarClient(player);
    }

    protected abstract ClientVersion defaultVersion();

    protected abstract int versionNumber();

    public ClientVersion getVersion(Player player) {
        if (this.viaversionplugin) {
            int version = Via.getAPI().getPlayerVersion(player.getUniqueId());
            if (version <= 5) {
                return ClientVersion.V1_7;
            }

            if (version <= 47) {
                return ClientVersion.V1_8;
            }

            if (!this.protocolsupportplugin || version != this.versionNumber()) {
                return this.getById(version);
            }
        }

        if (this.protocolsupportplugin) {
            ProtocolVersion version = ProtocolSupportAPI.getProtocolVersion(player);
            if (version == null) {
                return ClientVersion.VERSION_UNSUPPORTED;
            } else {
                return this.getByProtocolSupport(version.name(), version.getId());
            }
        } else {
            return this.defaultVersion();
        }
    }

    protected ClientVersion getByProtocolSupport(String versionName, int id) {
        switch (versionName) {
            case "MINECRAFT_1_7_5":
            case "MINECRAFT_1_7_10":
                return ClientVersion.V1_7;
            case "MINECRAFT_1_8":
                return ClientVersion.V1_8;
            case "MINECRAFT_1_9":
            case "MINECRAFT_1_9_1":
            case "MINECRAFT_1_9_2":
            case "MINECRAFT_1_9_4":
                return ClientVersion.V1_9;
            case "MINECRAFT_1_10":
                return ClientVersion.V1_10;
            case "MINECRAFT_1_11":
            case "MINECRAFT_1_11_1":
                return ClientVersion.V1_11;
            case "MINECRAFT_1_12":
            case "MINECRAFT_1_12_1":
            case "MINECRAFT_1_12_2":
                return ClientVersion.V1_12;
            case "MINECRAFT_1_13":
            case "MINECRAFT_1_13_1":
            case "MINECRAFT_1_13_2":
                return ClientVersion.V1_13;
            case "MINECRAFT_1_14":
            case "MINECRAFT_1_14_1":
            case "MINECRAFT_1_14_2":
            case "MINECRAFT_1_14_3":
            case "MINECRAFT_1_14_4":
                return ClientVersion.V1_14;
            case "MINECRAFT_1_15":
            case "MINECRAFT_1_15_1":
            case "MINECRAFT_1_15_2":
                return ClientVersion.V1_15;
            case "MINECRAFT_1_16":
            case "MINECRAFT_1_16_1":
            case "MINECRAFT_1_16_2":
            case "MINECRAFT_1_16_3":
            case "MINECRAFT_1_16_4":
                return ClientVersion.V1_16;
            case "MINECRAFT_1_17":
            case "MINECRAFT_1_17_1":
                return ClientVersion.V1_17;
            case "MINECRAFT_1_18":
            case "MINECRAFT_1_18_1":
                return ClientVersion.V1_18;
            case "MINECRAFT_FUTURE":
                return this.defaultVersion().next();
            default:
                return id > 47 ? this.getById(id) : ClientVersion.VERSION_UNSUPPORTED;
        }
    }

    protected ClientVersion getById(int id) {
        if (id >= 757) {
            return ClientVersion.V1_18;
        } else if (id >= 755) {
            return ClientVersion.V1_17;
        } else if (id >= 735) {
            return ClientVersion.V1_16;
        } else if (id >= 573) {
            return ClientVersion.V1_15;
        } else if (id >= 477) {
            return ClientVersion.V1_14;
        } else if (id >= 393) {
            return ClientVersion.V1_13;
        } else if (id >= 335) {
            return ClientVersion.V1_12;
        } else if (id >= 315) {
            return ClientVersion.V1_11;
        } else if (id >= 210) {
            return ClientVersion.V1_10;
        } else if (id >= 107) {
            return ClientVersion.V1_9;
        } else {
            return id >= 47 ? ClientVersion.V1_8 : ClientVersion.V1_7;
        }
    }

    public abstract boolean isLoaded(World var1, int var2, int var3);

    public abstract Material getType(Player var1, World var2, IBlockPosition var3);

    public abstract MaterialData toData(D var1);

    public D fromID(int id) {
        throw new UnsupportedOperationException();
    }

    public LazyData toLazy(D data, State id) {
        throw new UnsupportedOperationException();
    }

    public LazyData toLazy(D data) {
        throw new UnsupportedOperationException();
    }

    public LazyData fromIDToLazy(int id) {
        return this.toLazy(this.fromID(id), State.of(id));
    }

    public D place(ItemStack stack, Player player, BlockPosition pos, int face, float blockX, float blockY, float blockZ) {
        throw new UnsupportedOperationException();
    }

    public LazyData getPlace(ItemStack stack, Player player, BlockPosition pos, int face, float blockX, float blockY, float blockZ) {
        D data = this.place(stack, player, pos, face, blockX, blockY, blockZ);
        return data == null ? null : this.toLazy(data);
    }

    public abstract MaterialData getTypeAndData(Player var1, World var2, IBlockPosition var3);

    public abstract float getDamage(Player var1, World var2, IBlockPosition var3);

    public abstract double getMovementSpeed(Player var1);

    public abstract double getKnockbackResistance(Player var1);

    public abstract boolean setOnGround(Player var1, boolean var2);

    public abstract boolean setInWater(Player var1, boolean var2);

    public abstract float getFrictionFactor(Block var1);

    public abstract float getFrictionFactor(Player var1, World var2, IBlockPosition var3);

    public abstract void sendTransaction(Player var1, short var2);

    public abstract void sendBlockUpdate(Player var1, World var2, int var3, int var4, int var5);

    public abstract void sendBlockUpdate(Player var1, Location var2);

    public abstract Cuboid getBoundingBox(Player var1, World var2, IBlockPosition var3);

    public abstract PlayerAbilities getAbilities(Player var1);

    public boolean isGliding(Player player) {
        return false;
    }

    public boolean isRiptiding(Player player) {
        return false;
    }

    public float getJumpFactor(Player player, World world, IBlockPosition block) {
        return 1.0F;
    }

    public <K, V> Map<K, V> createCache(Long access, Long write) {
        CacheBuilder<K, V> builder = (CacheBuilder<K, V>) CacheBuilder.newBuilder();
        if (access != null) {
            builder.expireAfterAccess(access, TimeUnit.MILLISECONDS);
        }

        if (write != null) {
            builder.expireAfterWrite(write, TimeUnit.MILLISECONDS);
        }

        return builder.build().asMap();
    }

    public <V> Map<Long, V> long2ObjectHashMap(int initialCapacity, float loadFactor) {
        return this.fastUtil ? new Long2ObjectOpenHashMap(initialCapacity, loadFactor) : new HashMap();
    }

    public void setAsyncPotionEffects(Player player) {
    }

    public abstract void inject(PlayerData var1);

    public abstract void uninject(PlayerData var1);

    public abstract void postToMainThread(Runnable var1);

    public abstract Runnable scheduleEnd(Runnable var1);

    public abstract void close(Player var1);

    public void close(PlayerData playerData) {
        this.close(playerData.getPlayer());
    }

    public abstract void updatePing(Player var1, int var2);

    public void updatePing(PlayerData data) {
        this.updatePing(data.getPlayer(), data.getPing());
    }

    public abstract int getCurrentTick();

    public ItemStack getOffHand(Player player) {
        return null;
    }

    public boolean rayTrace(World world, double x, double y, double z, double dX, double dY, double dZ, boolean b1, boolean b2, boolean b3) {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty(World world, Entity entity, Cuboid boundingBox, IBlockPosition block) {
        throw new UnsupportedOperationException();
    }

    public boolean isWaterLogged(World world, IBlockPosition blockPosition) {
        return false;
    }

    public ServerVersion getServerVersion() {
        return this.serverVersion;
    }

    public boolean isProtocolsupportplugin() {
        return this.protocolsupportplugin;
    }

    public boolean isViaversionplugin() {
        return this.viaversionplugin;
    }

    public boolean isLunarClientAPI() {
        return this.lunarClientAPI;
    }

    public boolean isFloodGateAPI() {
        return this.floodGateAPI;
    }

    public boolean isFastUtil() {
        return this.fastUtil;
    }

    public void setServerVersion(ServerVersion serverVersion) {
        this.serverVersion = serverVersion;
    }
}
