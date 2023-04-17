package me.levansj01.verus.compat.v1_8_R3;

import com.viaversion.viaversion.api.Via;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import me.levansj01.verus.compat.api.wrapper.PlayerAbilities;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.state.State;
import me.levansj01.verus.data.transaction.world.LazyData;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.IBlockPosition;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.IUpdatePlayerListBox;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemBlock;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.MobEffect;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutTransaction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.Vec3D;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class NMSManager extends me.levansj01.verus.compat.NMSManager<IBlockData> {
    private boolean nspigot;
    private static final Field ItemBlock_a = SafeReflection.access(ItemBlock.class, "a");
    private final BlockPosition.MutableBlockPosition blockPosition = new BlockPosition.MutableBlockPosition();
    private static final Field effects = SafeReflection.access(EntityLiving.class, "effects");
    private static final State listState = State.fast(() -> SafeReflection.getLocalField(MinecraftServer.class, MinecraftServer.getServer(), "p"));

    public static NMSManager getInstance() {
        return (NMSManager)me.levansj01.verus.compat.NMSManager.getInstance();
    }

    public NMSManager() {
        try {
            Class.forName("com.ngxdev.config.nSpigotConfig");
            this.nspigot = true;
        } catch (Throwable var4) {
        }
    }

    public boolean isRunning() {
        return MinecraftServer.getServer().isRunning();
    }

    public <V> Map<Long, V> long2ObjectHashMap(int initialCapacity, float loadFactor) {
        return new Long2ObjectOpenHashMap(initialCapacity, loadFactor);
    }

    public float getFrictionFactor(Block block) {
        return CraftMagicNumbers.getBlock(block).frictionFactor;
    }

    public boolean isLoaded(World world, int x, int z) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        return worldServer.chunkProviderServer.isChunkLoaded(x >> 4, z >> 4);
    }

    protected net.minecraft.server.v1_8_R3.Block getBlock(ItemBlock block) {
        return (net.minecraft.server.v1_8_R3.Block)SafeReflection.fetch(ItemBlock_a, block);
    }

    public IBlockData place(ItemStack stack, Player player, me.levansj01.verus.compat.api.wrapper.BlockPosition pos, int face, float blockX, float blockY, float blockZ) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        net.minecraft.server.v1_8_R3.ItemStack copy = CraftItemStack.asNMSCopy(stack);
        if (stack == null) {
            return null;
        } else {
            Item item = copy.getItem();
            if (item instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock)item;
                int metadata = itemBlock.filterData(copy.getData());
                net.minecraft.server.v1_8_R3.Block block = this.getBlock(itemBlock);
                return block.getPlacedState(entityPlayer.world, new BlockPosition(pos.getX(), pos.getY(), pos.getZ()), EnumDirection.fromType1(face), blockX, blockY, blockZ, metadata, entityPlayer);
            } else {
                return null;
            }
        }
    }

    public Material getType(Player player, World world, IBlockPosition block) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        this.blockPosition.c(block.getX(), block.getY(), block.getZ());
        if (worldServer.isLoaded(this.blockPosition)) {
            return CraftMagicNumbers.getMaterial(worldServer.getType(this.blockPosition).getBlock());
        } else {
            return Material.AIR;
        }
    }

    public MaterialData toData(IBlockData data) {
        net.minecraft.server.v1_8_R3.Block nmsBlock = data.getBlock();
        return CraftMagicNumbers.getMaterial(nmsBlock).getNewData((byte)nmsBlock.toLegacyData(data));
    }

    public IBlockData fromID(int id) {
        return (IBlockData)net.minecraft.server.v1_8_R3.Block.d.a(id);
    }

    public LazyData toLazy(IBlockData data) {
        return this.toLazy(data, State.fast(() -> net.minecraft.server.v1_8_R3.Block.d.b(data)));
    }

    public LazyData toLazy(IBlockData data, State id) {
        if (data == null) {
            return LazyData.AIR;
        } else {
            net.minecraft.server.v1_8_R3.Block block = data.getBlock();
            State<Material> material = State.fast(() -> {
                return CraftMagicNumbers.getMaterial(block);
            });
            return new LazyData(id, State.fast(() -> {
                return ((Material)material.get()).getNewData((byte)block.toLegacyData(data));
            }), material, State.of(block.frictionFactor));
        }
    }

    public MaterialData getTypeAndData(Player player, World world, IBlockPosition block) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        this.blockPosition.c(block.getX(), block.getY(), block.getZ());
        if (worldServer.isLoaded(this.blockPosition)) {

            IBlockData blockData = worldServer.getType(this.blockPosition);
            return this.toData(blockData);
        } else {
            return new MaterialData(0);
        }
    }

    public float getDamage(Player player, World world, IBlockPosition block) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        this.blockPosition.c(block.getX(), block.getY(), block.getZ());
        if (!worldServer.isLoaded(this.blockPosition)) {
            return 0.0F;
        } else {
            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            net.minecraft.server.v1_8_R3.Block nmsBlock = (this.vspigot && this.multiblock ? worldServer.getType(((CraftPlayer)player).getHandle(), this.blockPosition) : worldServer.getType(this.blockPosition)).getBlock();
            return nmsBlock.getDamage(entityPlayer, worldServer, this.blockPosition);
        }
    }

    public float getFrictionFactor(Player player, World world, IBlockPosition block) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        this.blockPosition.c(block.getX(), block.getY(), block.getZ());
        if (worldServer.isLoaded(this.blockPosition)) {
            return this.vspigot && this.multiblock ? worldServer.getType(((CraftPlayer)player).getHandle(), this.blockPosition).getBlock().frictionFactor : worldServer.getType(this.blockPosition).getBlock().frictionFactor;
        } else {
            return Blocks.AIR.frictionFactor;
        }
    }

    public Cuboid getBoundingBox(Player player, World world, IBlockPosition block) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        this.blockPosition.c(block.getX(), block.getY(), block.getZ());
        if (!worldServer.isLoaded(this.blockPosition)) {
            return null;
        } else {
            IBlockData blockData;
            AxisAlignedBB axisAlignedBB = blockData.getBlock().a(worldServer, this.blockPosition, blockData);
            return axisAlignedBB == null ? null : new Cuboid(axisAlignedBB.a, axisAlignedBB.d, axisAlignedBB.b, axisAlignedBB.e, axisAlignedBB.c, axisAlignedBB.f);
        }
    }

    public double getMovementSpeed(Player player) {
        return ((CraftPlayer)player).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    public double getKnockbackResistance(Player player) {
        return ((CraftPlayer)player).getHandle().getAttributeInstance(GenericAttributes.c).getValue();
    }

    public boolean setOnGround(Player player, boolean ground) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        boolean value = entityPlayer.onGround;
        entityPlayer.onGround = ground;
        return value;
    }

    public boolean setInWater(Player player, boolean water) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        boolean value = entityPlayer.inWater;
        entityPlayer.inWater = water;
        return value;
    }

    protected ClientVersion defaultVersion() {
        return ClientVersion.V1_8;
    }

    protected int versionNumber() {
        return 47;
    }

    public ClientVersion getVersion(Player player) {
        if (!this.viaversionplugin && !this.nspigot) {
            if (this.protocolsupportplugin) {
                protocolsupport.api.ProtocolVersion version = protocolsupport.api.ProtocolSupportAPI.getProtocolVersion(player);
                return version == null ? ClientVersion.VERSION_UNSUPPORTED : this.getByProtocolSupport(version.name(), version.getId());
            } else {
                return ClientVersion.V1_8;
            }
        } else {
            int version;
            if (this.nspigot) {
                version = Via.getAPI().getPlayerVersion(player.getUniqueId());
            } else {
                version = com.viaversion.viaversion.api.Via.getAPI().getPlayerVersion(player.getUniqueId());
            }

            return this.getById(version);
        }
    }

    public void updatePing(Player player, int ping) {
        ((CraftPlayer)player).getHandle().ping = ping;
    }

    public void sendTransaction(Player player, short id) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutTransaction(0, id, false));
    }

    public void sendBlockUpdate(Player player, World world, int x, int y, int z) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        this.blockPosition.c(x, y, z);
        if (worldServer.isLoaded(this.blockPosition)) {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockChange(worldServer, this.blockPosition));
        }

    }

    public void sendBlockUpdate(Player player, Location location) {
        this.sendBlockUpdate(player, location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public PlayerAbilities getAbilities(Player player) {
        net.minecraft.server.v1_8_R3.PlayerAbilities abilities = ((CraftPlayer)player).getHandle().abilities;
        return new PlayerAbilities(abilities.isInvulnerable, abilities.isFlying, abilities.canFly, abilities.canInstantlyBuild, abilities.mayBuild, abilities.a(), abilities.b());
    }

    public void setAsyncPotionEffects(Player player) {
        if (!this.vspigot) {
            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            Map<Integer, MobEffect> map = entityPlayer.effects;
            if (map instanceof HashMap) {
                SafeReflection.set(effects, entityPlayer, new ConcurrentHashMap<>(map));
            }

        }
    }

    public int getCurrentTick() {
        return MinecraftServer.currentTick;
    }

    public boolean rayTrace(World world, double x, double y, double z, double dX, double dY, double dZ, boolean b1, boolean b2, boolean b3) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        MovingObjectPosition result = worldServer.rayTrace(new Vec3D(x, y, z), new Vec3D(dX, dY, dZ), b1, b2, b3);
        if (result == null) {
            return false;
        } else {
            IBlockData blockData = worldServer.getType(result.a());
            return blockData.getBlock() != Blocks.END_PORTAL;
        }
    }

    public boolean isEmpty(World world, Entity entity, Cuboid boundingBox, IBlockPosition block) {
        WorldServer worldServer = ((CraftWorld)world).getHandle();
        this.blockPosition.c(block.getX(), block.getY(), block.getZ());
        if (worldServer.isLoaded(this.blockPosition)) {
            IBlockData blockData = worldServer.getType(this.blockPosition);
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(boundingBox.getX1(), boundingBox.getY1(), boundingBox.getZ1(), boundingBox.getX2(), boundingBox.getY2(), boundingBox.getZ2());
            List<AxisAlignedBB> list = new ArrayList(1);
            blockData.getBlock().a(worldServer, this.blockPosition, blockData, axisAlignedBB, list, ((CraftEntity)entity).getHandle());
            return list.isEmpty();
        } else {
            return true;
        }
    }

    public void inject(PlayerData playerData) {
        Channel channel = ((CraftPlayer)playerData.getPlayer()).getHandle().playerConnection.networkManager.channel;
        if (channel != null) {
            this.getNettyHandler().inject(channel, new SPacketListener(playerData));
        }
    }

    public void uninject(PlayerData playerData) {
        PlayerConnection playerConnection = ((CraftPlayer)playerData.getPlayer()).getHandle().playerConnection;
        if (playerConnection != null && !playerConnection.isDisconnected()) {
            this.getNettyHandler().uninject(playerConnection.networkManager.channel);
        }
    }

    public void postToMainThread(Runnable runnable) {
        MinecraftServer.getServer().postToMainThread(runnable);
    }

    public Runnable scheduleEnd(Runnable runnable) {
        Objects.requireNonNull(runnable);
        IUpdatePlayerListBox update = runnable::run;
        List<IUpdatePlayerListBox> list = (List)listState.get();
        list.add(update);
        return () -> {
            list.remove(update);
        };
    }

    public void close(Player player) {
        Channel channel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;
        if (channel != null) {
            channel.close();
        }
    }
}