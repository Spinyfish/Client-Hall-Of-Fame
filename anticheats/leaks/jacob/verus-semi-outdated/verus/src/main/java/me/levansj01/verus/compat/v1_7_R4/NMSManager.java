package me.levansj01.verus.compat.v1_7_R4;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import me.levansj01.verus.compat.api.wrapper.PlayerAbilities;
import me.levansj01.verus.compat.netty.NettyHandler;
import me.levansj01.verus.compat.v1_7_R4.SPacketListener;
import me.levansj01.verus.compat.v1_7_R4.ShadedNettyHandler;
import me.levansj01.verus.compat.v1_7_R4.WrappedHashMap;
import me.levansj01.verus.compat.v1_7_R4.trove.LongObjectMapWrapper;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.IBlockPosition;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.IUpdatePlayerListBox;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.MovingObjectPosition;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockChange;
import net.minecraft.server.v1_7_R4.PacketPlayOutTransaction;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.server.v1_7_R4.Vec3D;
import net.minecraft.server.v1_7_R4.World;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.minecraft.util.gnu.trove.map.TLongObjectMap;
import net.minecraft.util.gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;

public class NMSManager
extends me.levansj01.verus.compat.NMSManager {
    private static final Field channel_field;
    private static final List<IUpdatePlayerListBox> list;
    private static final Field effects;

    public MaterialData toData(Pair<net.minecraft.server.v1_7_R4.Block, Integer> pair) {
        net.minecraft.server.v1_7_R4.Block block = (net.minecraft.server.v1_7_R4.Block)pair.getLeft();
        int n = (Integer)pair.getRight();
        return CraftMagicNumbers.getMaterial((net.minecraft.server.v1_7_R4.Block)block).getNewData((byte)n);
    }

    @Override
    public boolean setInWater(Player player, boolean bl) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        boolean bl2 = entityPlayer.inWater;
        entityPlayer.inWater = bl;
        return bl2;
    }

    @Override
    public ClientVersion getVersion(Player player) {
        ClientVersion clientVersion;
        switch (this.getConnection((Player)player).networkManager.getVersion()) {
            case 4: 
            case 5: {
                clientVersion = ClientVersion.V1_7;
                break;
            }
            case 47: {
                clientVersion = ClientVersion.V1_8;
                break;
            }
            default: {
                clientVersion = ClientVersion.VERSION_UNSUPPORTED;
            }
        }
        return clientVersion;
    }

    @Override
    public boolean isLoaded(org.bukkit.World world, int n, int n2) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        return worldServer.chunkProvider.isChunkLoaded(n >> 4, n2 >> 4);
    }

    @Override
    public MaterialData getTypeAndData(Player player, org.bukkit.World world, IBlockPosition iBlockPosition) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        if (worldServer.isLoaded(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ())) {
            net.minecraft.server.v1_7_R4.Block block = worldServer.getType(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ());
            int n = worldServer.getData(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ());
            return CraftMagicNumbers.getMaterial((net.minecraft.server.v1_7_R4.Block)block).getNewData((byte)n);
        }
        return new MaterialData(0);
    }

    @Override
    public float getFrictionFactor(Block block) {
        return CraftMagicNumbers.getBlock((Block)block).frictionFactor;
    }

    @Override
    public void postToMainThread(Runnable runnable) {
        MinecraftServer.getServer().processQueue.add(runnable);
    }

    public NMSManager() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        this.lunarClientAPI = pluginManager.isPluginEnabled("LunarClient-API");
        this.floodGateAPI = pluginManager.isPluginEnabled("floodgate");
    }

    @Override
    public boolean isEmpty(org.bukkit.World world, Entity entity, Cuboid cuboid, IBlockPosition iBlockPosition) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        if (worldServer.isLoaded(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ())) {
            net.minecraft.server.v1_7_R4.Block block = worldServer.getType(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ());
            AxisAlignedBB axisAlignedBB = AxisAlignedBB.a((double)cuboid.getX1(), (double)cuboid.getY1(), (double)cuboid.getZ1(), (double)cuboid.getX2(), (double)cuboid.getY2(), (double)cuboid.getZ2());
            ArrayList arrayList = new ArrayList(1);
            block.a((World)worldServer, iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ(), axisAlignedBB, arrayList, ((CraftEntity)entity).getHandle());
            return arrayList.isEmpty();
        }
        return true;
    }

    @Override
    public boolean rayTrace(org.bukkit.World world, double d, double d2, double d3, double d4, double d5, double d6, boolean bl, boolean bl2, boolean bl3) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        MovingObjectPosition movingObjectPosition = worldServer.rayTrace(Vec3D.a((double)d, (double)d2, (double)d3), Vec3D.a((double)d4, (double)d5, (double)d6), bl, bl2, bl3);
        if (movingObjectPosition == null) {
            return false;
        }
        net.minecraft.server.v1_7_R4.Block block = worldServer.getType(movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d);
        return block != Blocks.ENDER_PORTAL;
    }

    @Override
    public void sendBlockUpdate(Player player, org.bukkit.World world, int n, int n2, int n3) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        if (worldServer.isLoaded(n, n2, n3)) {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutBlockChange(n, n2, n3, (World)worldServer));
        }
    }

    @Override
    public PlayerAbilities getAbilities(Player player) {
        net.minecraft.server.v1_7_R4.PlayerAbilities playerAbilities = ((CraftPlayer)player).getHandle().abilities;
        return new PlayerAbilities(playerAbilities.isInvulnerable, playerAbilities.isFlying, playerAbilities.canFly, playerAbilities.canInstantlyBuild, playerAbilities.mayBuild, playerAbilities.a(), playerAbilities.b());
    }

    @Override
    public boolean setOnGround(Player player, boolean bl) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        boolean bl2 = entityPlayer.onGround;
        entityPlayer.onGround = bl;
        return bl2;
    }

    @Override
    public boolean isRunning() {
        return MinecraftServer.getServer().isRunning();
    }

    @Override
    public Runnable scheduleEnd(Runnable runnable) {
        IUpdatePlayerListBox iUpdatePlayerListBox = runnable::run;
        list.add(iUpdatePlayerListBox);
        return () -> list.remove(iUpdatePlayerListBox);
    }

    @Override
    public <V> Map<Long, V> long2ObjectHashMap(int n, float f) {
        return new LongObjectMapWrapper((TLongObjectMap)new TLongObjectHashMap(n, f));
    }

    @Override
    public void setAsyncPotionEffects(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        Map map = (Map)SafeReflection.fetch(effects, entityPlayer);
        if (map.getClass() == HashMap.class) {
            SafeReflection.set(effects, entityPlayer, new WrappedHashMap(new ConcurrentHashMap(map)));
        }
    }

    @Override
    public Cuboid getBoundingBox(Player player, org.bukkit.World world, IBlockPosition iBlockPosition) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        if (worldServer.isLoaded(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ())) {
            net.minecraft.server.v1_7_R4.Block block = worldServer.getType(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ());
            AxisAlignedBB axisAlignedBB = block.a((World)worldServer, iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ());
            return new Cuboid(axisAlignedBB.a, axisAlignedBB.d, axisAlignedBB.b, axisAlignedBB.e, axisAlignedBB.c, axisAlignedBB.f);
        }
        return new Cuboid(iBlockPosition).add(new Cuboid(1.0, 1.0, 1.0));
    }

    @Override
    public Material getType(Player player, org.bukkit.World world, IBlockPosition iBlockPosition) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        if (worldServer.isLoaded(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ())) {
            return CraftMagicNumbers.getMaterial((net.minecraft.server.v1_7_R4.Block)worldServer.getType(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ()));
        }
        return Material.AIR;
    }

    @Override
    public void inject(PlayerData playerData) {
        Channel channel = this.getChannel(playerData.getPlayer());
        if (channel == null) {
            return;
        }
        this.getNettyHandler().inject(channel, new SPacketListener(playerData));
    }

    @Override
    protected int versionNumber() {
        return 5;
    }

    @Override
    protected ClientVersion defaultVersion() {
        return ClientVersion.V1_7;
    }

    @Override
    public double getMovementSpeed(Player player) {
        return ((CraftPlayer)player).getHandle().getAttributeInstance(GenericAttributes.d).getValue();
    }

    @Override
    public <K, V> Map<K, V> createCache(Long l, Long l2) {
        CacheBuilder cacheBuilder = CacheBuilder.newBuilder();
        if (l != null) {
            cacheBuilder.expireAfterAccess(l.longValue(), TimeUnit.MILLISECONDS);
        }
        if (l2 != null) {
            cacheBuilder.expireAfterWrite(l2.longValue(), TimeUnit.MILLISECONDS);
        }
        return cacheBuilder.build().asMap();
    }

    static {
        effects = SafeReflection.access(EntityLiving.class, "effects");
        channel_field = SafeReflection.access(NetworkManager.class, "m", "Channel", "channel");
        list = (List)SafeReflection.getLocalField(MinecraftServer.class, (Object)MinecraftServer.getServer(), "n");
    }

    @Override
    public void uninject(PlayerData playerData) {
        PlayerConnection playerConnection = ((CraftPlayer)playerData.getPlayer()).getHandle().playerConnection;
        if (playerConnection == null || playerConnection.isDisconnected()) {
            return;
        }
        this.getNettyHandler().uninject(this.getChannel(playerData.getPlayer()));
    }

    public Channel getChannel(Player player) {
        return (Channel)SafeReflection.fetch(channel_field, ((CraftPlayer)player).getHandle().playerConnection.networkManager);
    }

    @Override
    public float getDamage(Player player, org.bukkit.World world, IBlockPosition iBlockPosition) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        if (worldServer.isLoaded(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ())) {
            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            net.minecraft.server.v1_7_R4.Block block = worldServer.getType(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ());
            return block.getDamage((EntityHuman)entityPlayer, (World)worldServer, iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ());
        }
        return 0.0f;
    }

    @Override
    public void sendBlockUpdate(Player player, Location location) {
        this.sendBlockUpdate(player, location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public float getFrictionFactor(Player player, org.bukkit.World world, IBlockPosition iBlockPosition) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        if (worldServer.isLoaded(iBlockPosition.getX(), iBlockPosition.getY(), iBlockPosition.getZ())) {
            return worldServer.getType((int)iBlockPosition.getX(), (int)iBlockPosition.getY(), (int)iBlockPosition.getZ()).frictionFactor;
        }
        return Blocks.AIR.frictionFactor;
    }

    @Override
    public void updatePing(Player player, int n) {
        ((CraftPlayer)player).getHandle().ping = n;
    }

    @Override
    public void close(Player player) {
        Channel channel = this.getChannel(player);
        if (channel == null) {
            return;
        }
        channel.close();
    }

    @Override
    public void sendTransaction(Player player, short s) {
        this.getConnection(player).sendPacket((Packet)new PacketPlayOutTransaction(0, s, false));
    }

    public PlayerConnection getConnection(Player player) {
        return ((CraftPlayer)player).getHandle().playerConnection;
    }

    @Override
    public double getKnockbackResistance(Player player) {
        return ((CraftPlayer)player).getHandle().getAttributeInstance(GenericAttributes.c).getValue();
    }

    @Override
    public int getCurrentTick() {
        return MinecraftServer.currentTick;
    }

    @Override
    protected NettyHandler<?> newNettyHandler() {
        return new ShadedNettyHandler();
    }
}

