package me.levansj01.verus.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import me.levansj01.launcher.VerusLauncher;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.alert.Alert;
import me.levansj01.verus.alert.manager.AlertManager;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.MovementCheck2;
import me.levansj01.verus.check.PacketCheck;
import me.levansj01.verus.check.ReachCheck;
import me.levansj01.verus.check.VehicleCheck;
import me.levansj01.verus.check.handler.TeleportCheck;
import me.levansj01.verus.check.manager.CheckManager;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.compat.VPacket;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.api.Transactionable;
import me.levansj01.verus.compat.api.wrapper.BlockPosition;
import me.levansj01.verus.compat.api.wrapper.Direction;
import me.levansj01.verus.compat.api.wrapper.EnumHand;
import me.levansj01.verus.compat.packets.VPacketPlayInArmAnimation;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockDig;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.compat.packets.VPacketPlayInClientCommand;
import me.levansj01.verus.compat.packets.VPacketPlayInCloseWindow;
import me.levansj01.verus.compat.packets.VPacketPlayInCustomPayload;
import me.levansj01.verus.compat.packets.VPacketPlayInEntityAction;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.compat.packets.VPacketPlayInHeldItemSlot;
import me.levansj01.verus.compat.packets.VPacketPlayInKeepAlive;
import me.levansj01.verus.compat.packets.VPacketPlayInSteerVehicle;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity;
import me.levansj01.verus.compat.packets.VPacketPlayInUseItem;
import me.levansj01.verus.compat.packets.VPacketPlayInVehicleMove;
import me.levansj01.verus.compat.packets.VPacketPlayInWindowClick;
import me.levansj01.verus.compat.packets.VPacketPlayOutAbilities;
import me.levansj01.verus.compat.packets.VPacketPlayOutAttachEntity;
import me.levansj01.verus.compat.packets.VPacketPlayOutBlockChange;
import me.levansj01.verus.compat.packets.VPacketPlayOutEntity;
import me.levansj01.verus.compat.packets.VPacketPlayOutEntityDestroy;
import me.levansj01.verus.compat.packets.VPacketPlayOutEntityEffect;
import me.levansj01.verus.compat.packets.VPacketPlayOutEntityMetadata;
import me.levansj01.verus.compat.packets.VPacketPlayOutEntityTeleport;
import me.levansj01.verus.compat.packets.VPacketPlayOutEntityVelocity;
import me.levansj01.verus.compat.packets.VPacketPlayOutExplosion;
import me.levansj01.verus.compat.packets.VPacketPlayOutGameStateChange;
import me.levansj01.verus.compat.packets.VPacketPlayOutKeepAlive;
import me.levansj01.verus.compat.packets.VPacketPlayOutMapChunk;
import me.levansj01.verus.compat.packets.VPacketPlayOutMapChunkBulk;
import me.levansj01.verus.compat.packets.VPacketPlayOutMultiBlockChange;
import me.levansj01.verus.compat.packets.VPacketPlayOutNamedEntitySpawn;
import me.levansj01.verus.compat.packets.VPacketPlayOutOpenWindow;
import me.levansj01.verus.compat.packets.VPacketPlayOutPosition;
import me.levansj01.verus.compat.packets.VPacketPlayOutRemoveEntityEffect;
import me.levansj01.verus.compat.packets.VPacketPlayOutRespawn;
import me.levansj01.verus.compat.packets.VPacketPlayOutSetSlot;
import me.levansj01.verus.compat.packets.VPacketPlayOutSpawnPosition;
import me.levansj01.verus.compat.packets.VPacketPlayOutUnloadChunk;
import me.levansj01.verus.compat.packets.VPacketPlayOutUpdateAttributes;
import me.levansj01.verus.data.CheckData;
import me.levansj01.verus.data.CheckLocalQueue;
import me.levansj01.verus.data.PingHandler;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.Transaction;
import me.levansj01.verus.data.client.ClientData;
import me.levansj01.verus.data.client.ClientType;
import me.levansj01.verus.data.reach.ReachBase;
import me.levansj01.verus.data.state.Releasable;
import me.levansj01.verus.data.state.ResetState;
import me.levansj01.verus.data.state.State;
import me.levansj01.verus.data.transaction.ability.IAbilityHandler;
import me.levansj01.verus.data.transaction.attribute.IAttributeHandler;
import me.levansj01.verus.data.transaction.effects.IEffectHandler;
import me.levansj01.verus.data.transaction.metadata.IMetadataHandler;
import me.levansj01.verus.data.transaction.teleport.ITeleportHandler;
import me.levansj01.verus.data.transaction.teleport.Teleport;
import me.levansj01.verus.data.transaction.tracker.ITracker;
import me.levansj01.verus.data.transaction.tracker.PacketLocation;
import me.levansj01.verus.data.transaction.velocity.ClientVelocity;
import me.levansj01.verus.data.transaction.velocity.IVelocityHandler;
import me.levansj01.verus.data.transaction.velocity.Velocity;
import me.levansj01.verus.data.transaction.world.IVerusWorld;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.messaging.MessagingHandler;
import me.levansj01.verus.messaging.listener.BrandListener;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.config.VerusConfiguration;
import me.levansj01.verus.storage.database.Database;
import me.levansj01.verus.type.VerusTypeLoader;
import me.levansj01.verus.type.dev.transaction.MetadataHandler;
import me.levansj01.verus.type.enterprise.transaction.ability.AbilityHandler;
import me.levansj01.verus.type.enterprise.transaction.attribute.AttributeHandler;
import me.levansj01.verus.type.enterprise.transaction.effects.EffectHandler;
import me.levansj01.verus.type.enterprise.transaction.teleport.TeleportHandler;
import me.levansj01.verus.type.enterprise.transaction.tracker.Tracker;
import me.levansj01.verus.type.enterprise.transaction.velocity.VelocityHandler;
import me.levansj01.verus.type.enterprise.transaction.world.VerusWorld;
import me.levansj01.verus.util.BukkitUtil;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.java.AtomicCappedQueue;
import me.levansj01.verus.util.java.BasicDeque;
import me.levansj01.verus.util.java.CappedQueue;
import me.levansj01.verus.util.java.JavaV;
import me.levansj01.verus.util.java.MathUtil;
import me.levansj01.verus.util.java.StringUtil;
import me.levansj01.verus.util.location.ILocation;
import me.levansj01.verus.util.location.ILocationGround;
import me.levansj01.verus.util.location.IVector3d;
import me.levansj01.verus.util.location.PlayerLocation;
import me.levansj01.verus.verus2.data.player.TickerMap;
import me.levansj01.verus.verus2.data.player.TickerType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Warning;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerData
        implements PacketHandler,
        Releasable {
    private static final int TRIM_SIZE = 80;
    private final Player player;
    private final UUID uuid;
    private final String name;
    private final CheckData checkData;
    private final TickerMap tickerMap = new TickerMap();
    private final ClientData clientData;
    private ClientVersion version;
    @Deprecated
    private final Queue<PlayerLocation> teleportList = new ConcurrentLinkedQueue();
    @Deprecated
    private final BasicDeque<Teleport> recentTeleports = new CappedQueue(200);
    @Deprecated
    private final Queue<Velocity> velocityQueue = new ConcurrentLinkedQueue();
    @Deprecated
    private final Queue<BiConsumer<Integer, Double>> pingQueue = new LinkedList();
    @Deprecated
    private final BasicDeque<Integer> connectionFrequency = new AtomicCappedQueue(5);
    @Deprecated
    private Teleport lastTeleport2;
    @Deprecated
    private final Queue<Teleport> teleports = new ConcurrentLinkedQueue();
    private final Queue<Alert> spoofedAlerts = new ConcurrentLinkedQueue();
    private final Map<Integer, AtomicCappedQueue<PacketLocation>> recentMoveMap = NMSManager.getInstance().createCache(Long.valueOf((long)TimeUnit.MINUTES.toMillis(30L)), null);
    private final CheckLocalQueue<ClientVelocity> velocityData = CheckLocalQueue.async();
    private final Map<Long, Long> keepAliveMap = NMSManager.getInstance().createCache(null, Long.valueOf((long)TimeUnit.MINUTES.toMillis(10L)));
    private final Map<Short, Transaction> transactionMap = StorageEngine.getInstance().getVerusConfig().isMoreTransactions() ? new ConcurrentHashMap() : NMSManager.getInstance().createCache(null, Long.valueOf((long)TimeUnit.MINUTES.toMillis(3L)));
    private final Queue<ReachBase> reachData = new LinkedList();
    private final PlayerLocation location;
    private PlayerLocation lastLocation;
    private PlayerLocation lastLastLocation;
    private me.levansj01.verus.util.location.PacketLocation currentLocation2;
    private me.levansj01.verus.util.location.PacketLocation lastLocation2;
    private me.levansj01.verus.util.location.PacketLocation lastLastLocation2;
    private BlockPosition spawnLocation;
    private PlayerData lastAttacked;
    private long lastFlying;
    private long lastLastFlying;
    private long lastDelayed;
    private long lastFast;
    private long lastTeleport;
    private long lastClientTransaction;
    private long lastRespawn;
    private long lastKeepAliveTimestamp;
    private int lastTeleportTicks;
    private int flyingTicks = 1200;
    private int allowFlightTicks = 1200;
    private int velocityTicks;
    private int verticalVelocityTicks = -20;
    private int horizontalVelocityTicks;
    private int horizontalSpeedTicks;
    private int ticks;
    private int totalTicks;
    private int lastSentTransaction;
    private int lastKeepAlive;
    private int survivalTicks = 1200;
    private int lastAttackTicks = 600;
    private int nonMoveTicks;
    private int lastNonMoveTicks;
    private int lastInventoryTick;
    private int lastInventoryOutTick;
    private int lastSetSlot;
    private int lastTransactionPing;
    private int averageTransactionPing;
    private int ping;
    private int lastPing;
    private int averagePing;
    private int lastAttackedId;
    private int blockTicks;
    private int lastFace;
    private int receivedTransactions;
    private boolean fallFlying;
    private boolean receivedTransaction;
    private boolean vehicle;
    private boolean wasVehicle;
    private boolean sentTransaction;
    private boolean moved;
    private boolean aimed;
    private boolean blocking;
    private boolean placing;
    private boolean swingDigging;
    private boolean abortedDigging;
    private boolean stoppedDigging;
    private boolean ready;
    private BlockPosition diggingBlock = null;
    private Direction diggingBlockFace = null;
    private Boolean sprinting = null;
    private Boolean sneaking = null;
    private final ResetState<PotionEffect[]> effects;
    private final ResetState<Boolean> shouldHaveReceivedPing;
    private final ResetState<Integer> speedLevel;
    private final ResetState<Integer> slowLevel;
    private final ResetState<Integer> jumpLevel;
    private final ResetState<Integer> pingTicks;
    private final ResetState<Integer> maxPingTicks;
    private final ResetState<Integer> maxPingTick2;
    private BlockPosition lastBlockPosition;
    private boolean banned;
    private boolean enabled = true;
    private boolean checkSpoofing;
    private boolean spoofBan;
    private boolean resetVelocity;
    private boolean digging;
    private boolean alerts;
    private boolean debug;
    private boolean inventoryOpen;
    private short lastTransactionID = (short)ThreadLocalRandom.current().nextInt();
    private int teleportTicks;
    private int lastFakeEntityDamageTicks;
    private int suffocationTicks = 100;
    private int transactionPing;
    private int spawned = Integer.MAX_VALUE;
    private int vehicleId;
    private int fallDamage;
    private long timestamp;
    private long nanos;
    private double lastVelY;
    private double lastVelX;
    private double lastVelZ;
    private double velY;
    private String brand;
    private Check spoofBanCheck = null;
    private CheckType focus;
    private String focusSubType;
    private Transaction transaction;
    private Transaction lastTransaction;
    private Transaction nextTransaction = new Transaction(null);
    private final Queue<Runnable> start = new ConcurrentLinkedQueue();
    private final Queue<Runnable> end = new ConcurrentLinkedQueue();
    private final Queue<Runnable> nextTrans = new ConcurrentLinkedQueue();
    private IVerusWorld world;
    private ITeleportHandler teleportHandler;
    private IVelocityHandler velocityHandler;
    private IAbilityHandler abilityHandler;
    private IEffectHandler effectHandler;
    private ITracker tracker;
    private IAttributeHandler attributeHandler;
    private IMetadataHandler metadataHandler;
    private final PlayerLocation vehicleLocation;
    private PlayerLocation lastVehicleLocation;

    public PlayerData(Player player, Check[] checks) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.updateVersion();
        this.checkData = new CheckData(this, checks);
        this.location = this.vehicleLocation = BukkitUtil.fromPlayer((Player)player);
        this.lastLocation2 = this.lastLastLocation2 = BukkitUtil.fromPlayer2((Player)player);
        this.currentLocation2 = this.lastLastLocation2;
        this.lastLocation = this.lastLastLocation = this.location.clone();
        this.alerts = BukkitUtil.hasPermissionMeta((Player)player, (String)"verus.alerts");
        this.debug = BukkitUtil.hasPermissionMeta((Player)player, (String)"verus.admin");
        this.clientData = new ClientData("Unknown", ClientType.UNKNOWN);
        GameMode gameMode = player.getGameMode();
        if (gameMode != GameMode.SURVIVAL && gameMode != GameMode.ADVENTURE) {
            this.survivalTicks = 0;
        }
        if (player.getAllowFlight()) {
            this.allowFlightTicks = 0;
        }
        if (player.isFlying()) {
            this.flyingTicks = 0;
        }
        this.effects = State.reset(() -> (PotionEffect[])player.getActivePotionEffects().toArray((Object[])new PotionEffect[0]));
        this.speedLevel = State.reset(() -> BukkitUtil.getPotionLevel(this.effects, (PotionEffectType)PotionEffectType.SPEED));
        this.slowLevel = State.reset(() -> BukkitUtil.getPotionLevel(this.effects, (PotionEffectType)PotionEffectType.SLOW));
        this.jumpLevel = State.reset(() -> BukkitUtil.getPotionLevel(this.effects, (PotionEffectType)PotionEffectType.JUMP));
        this.shouldHaveReceivedPing = State.reset(() -> this.receivedTransaction || this.totalTicks < this.lastKeepAlive + 4 || this.lastTeleport2 != null && this.tickerMap.get(TickerType.LAST_SENT_TRANSACTION) > this.lastTeleport2.getTicks() + 5 || this.totalTicks > 1200);
        this.pingTicks = State.reset(() -> Math.min((int)100, (int)((Boolean)this.shouldHaveReceivedPing.get() != false ? (int)Math.ceil((double)((double)this.getTransactionPing() / 50.0)) : (int)MathUtil.highest((Number[])new Number[]{40, this.totalTicks}))) + 1);
        this.maxPingTicks = State.reset(() -> Math.min((int)100, (int)((Boolean)this.shouldHaveReceivedPing.get() != false ? (int)Math.ceil((double)(MathUtil.highest((double)this.transactionPing, (double)this.lastTransactionPing, (double)this.averageTransactionPing) / 50.0)) : Math.max((int)40, (int)this.totalTicks))) + 1);
        this.maxPingTick2 = State.reset(() -> Math.min((int)600, (int)((Boolean)this.shouldHaveReceivedPing.get() != false ? (int)Math.ceil((double)(MathUtil.highest((double)this.transactionPing, (double)this.lastTransactionPing, (double)this.averageTransactionPing) / 50.0)) : Math.max((int)40, (int)this.totalTicks))) + 1);
        NMSManager nmsManager = NMSManager.getInstance();
        PotionEffect[] effects = (PotionEffect[])this.effects.get();
        if (BukkitUtil.hasEffect((PotionEffect[])effects, (int)PotionEffectType.JUMP.getId())) {
            this.tickerMap.reset(TickerType.JUMP_BOOST);
        }
        if (BukkitUtil.hasEffect((PotionEffect[])effects, (int)PotionEffectType.SPEED.getId())) {
            this.tickerMap.reset(TickerType.SPEED_BOOST);
        }
        if (BukkitUtil.hasEffect((PotionEffect[])effects, (int)25)) {
            this.tickerMap.reset(TickerType.LEVITATION);
        }
        if (nmsManager.isGliding(player)) {
            this.tickerMap.reset(TickerType.GLIDING);
        }
        nmsManager.postToMainThread(() -> this.after(() -> this.end(() -> {
            this.ready = true;
        })));
        nmsManager.postToMainThread(this::handleTransaction);
        if (VerusTypeLoader.isEnterprise()) {
            this.teleportHandler = new TeleportHandler(this);
            this.velocityHandler = new VelocityHandler(this);
            this.abilityHandler = new AbilityHandler(this);
            this.effectHandler = new EffectHandler(this);
            this.world = new VerusWorld(this);
            if (nmsManager.getServerVersion().beforeEq(ServerVersion.v1_8_R3)) {
                this.tracker = new Tracker(this);
            }
            this.attributeHandler = new AttributeHandler(this);
        }
        if (VerusTypeLoader.isDev()) {
            this.metadataHandler = new MetadataHandler(this);
        }
    }

    public void updateVersion() {
        ClientVersion version;
        try {
            version = NMSManager.getInstance().getVersion(this.player);
        }
        catch (Throwable throwable) {
            VerusLauncher.getPlugin().getLogger().log(Level.SEVERE, "Failed to obtain version for " + this.player.getName() + ": ", throwable);
            version = ClientVersion.VERSION_UNSUPPORTED;
        }
        if (!Objects.equals((Object)this.version, (Object)version)) {
            this.version = version;
            if (this.checkData != null) {
                this.checkData.updateChecks();
            }
        }
        if (this.tracker != null) {
            this.tracker.updateVersion();
        }
    }

    public void before(Runnable runnable) {
        if (this.lastTransaction == null) {
            runnable.run();
        } else {
            this.lastTransaction.queue(runnable);
        }
    }

    public void after(Runnable runnable) {
        this.nextTransaction.queue(runnable);
    }

    public void start(Runnable runnable) {
        this.start.add((Object)runnable);
    }

    public void end(Runnable runnable) {
        this.end.add((Object)runnable);
    }

    public void nextTrans(Runnable runnable) {
        this.nextTrans.add((Object)runnable);
    }

    public void handleIn(Transactionable transactionable) {
        short id = transactionable.id();
        this.tickerMap.set(TickerType.LAST_RECEIVED_TRANSACTION, TickerType.TOTAL);
        Transaction transaction = (Transaction)this.transactionMap.remove((Object)id);
        if (transaction != null && transaction.isSent()) {
            BiConsumer pingConsumer;
            this.transaction = transaction;
            this.receivedTransaction = true;
            transaction.receive(this.timestamp, this);
            ++this.receivedTransactions;
            JavaV.executeSafely(this.nextTrans, () -> " in next transaction " + transaction.getId());
            State deviation = State.fast(() -> MathUtil.variance((Number)Integer.valueOf((int)0), this.connectionFrequency));
            this.lastTransactionPing = this.transactionPing;
            this.transactionPing = transaction.ping().intValue();
            VerusConfiguration configuration = StorageEngine.getInstance().getVerusConfig();
            if (configuration.isPingKick() && (long)this.transactionPing > TimeUnit.SECONDS.toMillis((long)configuration.getPingTimeout())) {
                VerusLauncher.getPlugin().getLogger().info(this.getName() + " was timed out after " + configuration.getPingTimeout() + " seconds.");
                this.fuckOff();
                return;
            }
            this.averageTransactionPing = (this.averageTransactionPing * 4 + this.transactionPing) / 5;
            this.resetPingTicks();
            while ((pingConsumer = (BiConsumer)this.pingQueue.poll()) != null) {
                pingConsumer.accept((Object)this.transactionPing, (Object)((Double)deviation.get()));
            }
            ArrayList reachDataList = new ArrayList(this.reachData);
            for (ReachCheck reachCheck : this.checkData.getReachChecks()) {
                reachCheck.handle((List)reachDataList, this.timestamp);
            }
            this.reachData.clear();
            for (ReachCheck reachCheck : this.checkData.getPingHandlers()) {
                reachCheck.handleTransaction(transaction.getSent().longValue(), this.timestamp);
            }
        } else if (this.checkData.getTransactionCheck() != null && this.totalTicks > 400 && !this.inventoryOpen) {
            NMSManager.getInstance().postToMainThread(() -> {
                if (this.transactionMap.remove((Object)id) == null) {
                    // empty if block
                }
            });
        }
        this.lastClientTransaction = this.timestamp;
    }

    public void handle(VPacketPlayInArmAnimation<?> packet) {
        this.blocking = false;
        if (!this.swingDigging && !this.placing) {
            this.tickerMap.increment(TickerType.POSSIBLE_SWINGS);
        }
    }

    public void handle(VPacketPlayInBlockDig<?> packet) {
        GameMode gameMode = this.player.getGameMode();
        if (gameMode == GameMode.CREATIVE) {
            this.digging = false;
            this.swingDigging = false;
        } else if (packet.getType() == VPacketPlayInBlockDig.PlayerDigType.START_DESTROY_BLOCK) {
            if (gameMode == GameMode.SURVIVAL) {
                this.digging = true;
                this.diggingBlock = packet.getBlockPosition();
                this.diggingBlockFace = packet.getFace();
                this.swingDigging = true;
            }
            this.abortedDigging = false;
            this.stoppedDigging = false;
        } else if (packet.getType() == VPacketPlayInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK) {
            this.abortedDigging = true;
        } else if (packet.getType() == VPacketPlayInBlockDig.PlayerDigType.STOP_DESTROY_BLOCK) {
            this.stoppedDigging = true;
        } else if (packet.getType() == VPacketPlayInBlockDig.PlayerDigType.RELEASE_USE_ITEM) {
            this.blocking = false;
            if (NMSManager.getInstance().getServerVersion().afterEq(ServerVersion.v1_13_R2)) {
                for (ItemStack stack : new ItemStack[]{this.player.getItemInHand(), NMSManager.getInstance().getOffHand(this.player)}) {
                    int riptide;
                    if (stack == null || stack.getType() != MaterialList.TRIDENT || (riptide = BukkitUtil.getEnchantment((ItemStack)stack, (String)"RIPTIDE")) <= 0) continue;
                    this.tickerMap.set(TickerType.RIPTIDE, -10 * (riptide + 1));
                }
            }
        }
        if (this.world != null) {
            this.world.handle(packet);
        }
    }

    public void handle(VPacketPlayInBlockPlace<?> packet) {
        ItemStack itemStack = packet.getItemStack();
        if (!packet.isEmpty()) {
            this.lastBlockPosition = packet.getPosition();
            this.lastFace = packet.getFace();
            if (itemStack != null) {
                this.handleItem(itemStack);
                this.blocking = MaterialList.canPlace((ItemStack)itemStack);
            }
            this.placing = true;
            if (this.world != null) {
                this.world.handle(packet);
            }
            return;
        }
        EnumHand hand = EnumHand.values()[packet.getHand()];
        switch (1.$SwitchMap$me$levansj01$verus$compat$api$wrapper$EnumHand[hand.ordinal()]) {
            case 1: {
                itemStack = this.player.getItemInHand();
                break;
            }
            case 2: {
                itemStack = NMSManager.getInstance().getOffHand(this.player);
            }
        }
        if (itemStack != null) {
            this.handleInteraction(itemStack.getType());
            this.blocking = MaterialList.canPlace((ItemStack)itemStack);
        }
    }

    public void handle(VPacketPlayInUseItem<?> packet) {
        this.lastBlockPosition = packet.getPosition();
        this.lastFace = packet.getFace();
        EnumHand hand = EnumHand.values()[packet.getHand()];
        switch (1.$SwitchMap$me$levansj01$verus$compat$api$wrapper$EnumHand[hand.ordinal()]) {
            case 1: {
                this.handleItem(this.player.getItemInHand());
                break;
            }
            case 2: {
                this.handleItem(NMSManager.getInstance().getOffHand(this.player));
            }
        }
        this.placing = true;
    }

    public void handle(VPacketPlayInClientCommand<?> packet) {
        this.blocking = false;
        if (packet.getCommand() == VPacketPlayInClientCommand.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            this.inventoryOpen = true;
            this.lastInventoryTick = this.totalTicks;
        }
    }

    public void handle(VPacketPlayInCloseWindow<?> packet) {
        this.inventoryOpen = false;
        this.blocking = false;
    }

    public void handle(VPacketPlayInCustomPayload<?> packet) {
        String channel = packet.getChannel();
        if (channel == null || channel.equals((Object)"MC|Brand") || channel.equals((Object)"minecraft:brand")) {
            // empty if block
        }
    }

    /*
     * Unable to fully structure code
     */
    public void handle(VPacketPlayInEntityAction<?> packet) {
        playerAction = packet.getAction();
        switch (1.$SwitchMap$me$levansj01$verus$compat$packets$VPacketPlayInEntityAction$PlayerAction$Type[playerAction.getType().ordinal()]) {
            case 1: {
                this.sprinting = playerAction.isValue();
                this.tickerMap.reset(TickerType.SPRINT);
                break;
            }
            case 2: {
                this.sneaking = playerAction.isValue();
                break;
            }
            case 3: {
                switch (1.$SwitchMap$me$levansj01$verus$compat$packets$VPacketPlayInEntityAction$PlayerAction[packet.getAction().ordinal()]) {
                    case 1: {
                        if (!this.fallFlying) {
                            this.tickerMap.set(TickerType.FALL_FLYING, -20);
                            this.fallFlying = true;
                        } else {
                            ** GOTO lbl16
                        }
                    }
                }
            }
        }
        lbl16:
        // 7 sources

    }

    public void handle(VPacketPlayInFlying<?> packet) {
        ItemStack itemInhand;
        PlayerLocation teleport2;
        long timestamp = this.timestamp;
        this.nanos = System.nanoTime();
        this.tickerMap.incrementAll();
        this.tickerMap.set(TickerType.ATTACKS_IN_LAST, this.tickerMap.get(TickerType.ATTACKS));
        this.tickerMap.reset(TickerType.ATTACKS);
        if (packet.isPos() && !packet.sameXZ((ILocation)this.currentLocation2)) {
            this.tickerMap.increment(TickerType.MOVES_SINCE_TELEPORT);
        }
        if (this.teleportHandler != null) {
            packet.setTeleport(this.teleportHandler.isTeleport(packet));
        }
        if (!packet.isTeleport()) {
            ++this.ticks;
        }
        for (MovementCheck2[] teleport : this.teleports) {
            if (teleport.matches(packet)) {
                this.teleports.remove((Object)teleport);
                this.tickerMap.reset(TickerType.TELEPORT);
                this.tickerMap.reset(TickerType.MOVES_SINCE_TELEPORT);
                this.lastTeleport2 = teleport;
                if (this.checkData.getPingSpoofCheck() == null) break;
                this.checkData.getPingSpoofCheck().accept(timestamp, (Teleport)teleport);
                break;
            }
            if (timestamp - teleport.getTime() <= (long)((this.getAverageTransactionPing() + 1000) * 2) || this.tickerMap.get(TickerType.TOTAL) - teleport.getTicks() <= 2 * (this.getMaxPingTicks() + 20)) continue;
            try {
                this.teleports.remove((Object)teleport);
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
        if (!packet.isTeleport()) {
            JavaV.executeSafely(this.start, () -> " start of next tick");
        }
        NMSManager nmsManager = NMSManager.getInstance();
        if (this.totalTicks == 5) {
            nmsManager.postToMainThread(() -> {
                this.updateVersion();
                try {
                    if (nmsManager.isRunningLunarClient(this.player)) {
                        this.clientData.setType(ClientType.LUNAR);
                    }
                }
                catch (Throwable e) {
                    VerusLauncher.getPlugin().getLogger().log(Level.SEVERE, "Failed to update lunar status: ", e);
                }
                try {
                    String brand;
                    BrandListener listener = MessagingHandler.getInstance().getBrandListener();
                    if (listener != null && (brand = listener.getBrand(this.uuid)) != null) {
                        this.checkData.getBrandCheck().handle(brand);
                    }
                }
                catch (Throwable e) {
                    VerusLauncher.getPlugin().getLogger().log(Level.SEVERE, "Failed to update brand status: ", e);
                }
            });
        }
        this.effects.reset();
        this.speedLevel.reset();
        this.slowLevel.reset();
        this.jumpLevel.reset();
        this.resetPingTicks();
        this.velocityQueue.removeIf(velocity -> this.tickerMap.get(TickerType.TOTAL) - velocity.getTicks() > this.getMaxPingTicks() + 2 && timestamp - velocity.getTimestamp() > (long)(this.getTransactionPing() + 100) && velocity.attenuate(packet.isGround()));
        this.lastLastLocation2 = this.lastLocation2;
        this.lastLocation2 = this.currentLocation2;
        this.currentLocation2 = packet.from(this.lastLocation2);
        if (packet.isPos() && this.currentLocation2.distanceSquared((ILocation)this.lastLocation2) != 0.0) {
            this.tickerMap.reset(TickerType.NOT_MOVING);
        }
        for (MovementCheck2 check : this.checkData.getMovementChecks2()) {
            check.handle(this.lastLocation2, this.currentLocation2, timestamp);
        }
        this.location.setTimestamp(timestamp);
        this.location.setTeleport(packet.isTeleport());
        this.location.setGround(Boolean.valueOf((boolean)packet.isGround()));
        if (packet.isPos()) {
            this.location.setX(packet.getX());
            this.location.setY(packet.getY());
            this.location.setZ(packet.getZ());
        }
        if (packet.isLook()) {
            this.location.setYaw(packet.getYaw());
            this.location.setPitch(packet.getPitch());
        }
        this.lastNonMoveTicks = this.nonMoveTicks++;
        if (packet.isPos()) {
            this.nonMoveTicks = 0;
        }
        long diff = timestamp - this.lastFlying;
        if (diff > 110L) {
            this.lastDelayed = timestamp;
        }
        if (diff < 15L) {
            this.lastFast = timestamp;
        }
        this.lastLastFlying = this.lastFlying;
        this.lastFlying = timestamp;
        this.teleportTicks = this.teleportList.isEmpty() ? ++this.teleportTicks : 0;
        if (this.version == ClientVersion.V1_9) {
            diff = Math.min((long)diff, (long)110L);
        }
        this.connectionFrequency.addFirst((Object)(50 - (int)diff));
        int possibleSwings = this.tickerMap.get(TickerType.POSSIBLE_SWINGS);
        if (possibleSwings > 0 && !this.swingDigging) {
            this.tickerMap.reset(TickerType.LEGIT_SWING);
            if (possibleSwings > 1) {
                this.tickerMap.reset(TickerType.DOUBLE_SWING);
            }
        }
        this.tickerMap.reset(TickerType.POSSIBLE_SWINGS);
        if (this.placing) {
            this.placing = false;
        }
        if (this.abortedDigging) {
            this.abortedDigging = false;
            this.swingDigging = false;
            this.digging = false;
        }
        if (this.stoppedDigging) {
            this.stoppedDigging = false;
            this.digging = false;
        }
        if (this.resetVelocity) {
            this.lastVelY = 0.0;
            this.resetVelocity = false;
        }
        if ((teleport2 = (PlayerLocation)this.teleportList.peek()) != null) {
            int tickDifference = this.totalTicks - teleport2.getTickTime();
            if (packet.isPos() && tickDifference >= this.getMoveTicks() && teleport2.sameXYZ((ILocation)this.location)) {
                this.teleportList.poll();
                if (this.lastLocation.distanceXZSquared((IVector3d)this.location) > 4.0) {
                    this.spawned = 0;
                }
                this.lastVelY = this.lastVelZ = this.velY;
                this.lastVelX = this.lastVelZ;
                this.lastLocation = this.location.clone();
                this.lastLastLocation = this.location.clone();
                this.lastTeleportTicks = this.totalTicks;
            } else if (tickDifference > (packet.isPos() ? this.getMaxPingTicks() * 2 : this.getMaxPingTicks() * 4)) {
                this.teleportList.poll();
            }
        }
        if (this.lastVelY == 0.0 && this.velY != 0.0) {
            if (packet.isGround()) {
                this.velY = 0.0;
            } else {
                this.velY -= 0.08;
                this.velY *= 0.98;
            }
        }
        if (packet.isGround()) {
            if (this.fallFlying) {
                this.fallFlying = false;
                this.tickerMap.set(TickerType.ELYTRA_EXIT, -100);
            }
            if (!this.isTeleportingV2()) {
                this.spawned = Integer.MAX_VALUE;
            }
        }
        if (this.spawned <= 600) {
            ++this.spawned;
            if (packet.isPos()) {
                if (this.lastLocation.getY() < this.location.getY()) {
                    this.spawned += 40;
                } else if (this.lastLocation.getY() == this.location.getY() && !this.lastLocation.sameXYZ((ILocation)this.location)) {
                    this.spawned += 10;
                }
            }
        }
        this.flyingTicks = this.player.isFlying() ? 0 : ++this.flyingTicks;
        this.allowFlightTicks = this.player.getAllowFlight() ? 0 : ++this.allowFlightTicks;
        if (this.wasVehicle) {
            this.tickerMap.reset(TickerType.VEHICLE);
        } else {
            this.tickerMap.increment(TickerType.VEHICLE);
        }
        if (this.vehicle || this.player.getVehicle() != null) {
            this.wasVehicle = true;
            this.tickerMap.reset(TickerType.VEHICLE);
        } else if (!this.lastLocation.sameXYZ((ILocation)this.location)) {
            this.wasVehicle = false;
        }
        GameMode gameMode = this.player.getGameMode();
        this.survivalTicks = gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE ? ++this.survivalTicks : 0;
        if (gameMode.getValue() == 3) {
            this.sprinting = false;
        }
        if ((itemInhand = this.player.getItemInHand()) != null && (!MaterialList.canPlace((ItemStack)itemInhand) || itemInhand.getType().equals((Object)MaterialList.BOW) && !this.player.getInventory().contains(MaterialList.ARROW))) {
            this.blocking = false;
        }
        this.blockTicks = this.blocking ? ++this.blockTicks : 0;
        PotionEffect[] effects = (PotionEffect[])this.effects.get();
        if (BukkitUtil.hasEffect((PotionEffect[])effects, (int)PotionEffectType.JUMP.getId())) {
            this.tickerMap.reset(TickerType.JUMP_BOOST);
        }
        if (BukkitUtil.hasEffect((PotionEffect[])effects, (int)PotionEffectType.SPEED.getId())) {
            this.tickerMap.reset(TickerType.SPEED_BOOST);
        }
        if (BukkitUtil.hasEffect((PotionEffect[])effects, (int)25)) {
            this.tickerMap.reset(TickerType.LEVITATION);
        }
        if (nmsManager.isGliding(this.player)) {
            this.tickerMap.reset(TickerType.GLIDING);
        }
        ++this.totalTicks;
        ++this.lastAttackTicks;
        ++this.velocityTicks;
        ++this.horizontalSpeedTicks;
        ++this.verticalVelocityTicks;
        ++this.horizontalVelocityTicks;
        this.moved = !this.lastLocation.sameXYZ((ILocation)this.location);
        if (this.moved) {
            for (MovementCheck movementCheck : this.checkData.getMovementChecks()) {
                movementCheck.handle(this.lastLocation, this.location, timestamp);
            }
        }
        if (this.aimed = !this.lastLocation.sameYawPitch((ILocation)this.location)) {
            for (MovementCheck aimCheck : this.checkData.getAimChecks()) {
                aimCheck.handle(this.lastLocation, this.location, timestamp);
            }
        }
        this.lastLastLocation = this.lastLocation.clone();
        this.lastLocation = this.location.clone();
        if (this.lastAttackTicks <= 1 && this.lastAttacked != null && !this.isTeleporting(5) && !this.isVehicle() && this.isSurvival() && !this.isTeleportingV2() && this.lastAttacked.getTeleportTicks() > this.lastAttacked.getMaxPingTicks() + this.getMaxPingTicks() && this.lastAttacked.getTickerMap().get(TickerType.VEHICLE) > this.lastAttacked.getMaxPingTicks() + this.getMaxPingTicks()) {
            boolean possibleLag = StorageEngine.getInstance().getVerusConfig().isIgnoreLag() && this.hasFast() || this.hasLag();
            boolean sneaking = this.sneaking != null && this.sneaking != false;
            PlayerLocation location = this.location.clone();
            PlayerLocation lastLocation = this.lastLastLocation.clone();
            AtomicCappedQueue recentMovesQueue = (AtomicCappedQueue)this.recentMoveMap.get((Object)this.lastAttackedId);
            if (recentMovesQueue != null && recentMovesQueue.size() >= Math.min((int)40, (int)(10 + this.getMaxPingTicks()))) {
                List recentMoves = (List)recentMovesQueue.stream().map(PacketLocation::clone).collect(Collectors.toList());
                int lastPing = this.transactionPing;
                int nonMoveTicks = this.nonMoveTicks;
                this.pingQueue.add((ping, connection) -> {
                    ReachBase reachBase = new ReachBase(this, location, lastLocation, timestamp, ping.intValue(), lastPing, connection.intValue(), nonMoveTicks, recentMoves, sneaking, this.transaction);
                    reachBase.execute();
                    if (!possibleLag) {
                        for (ReachCheck reachCheck : this.checkData.getReachChecks()) {
                            reachCheck.handle(reachBase, timestamp);
                        }
                        this.reachData.add((Object)reachBase);
                    }
                });
            }
        }
    }

    public void handle(VPacketPlayInHeldItemSlot<?> packet) {
        this.blocking = false;
    }

    public void handle(VPacketPlayInKeepAlive<?> packet) {
        long id = packet.getId();
        this.lastKeepAlive = this.tickerMap.get(TickerType.TOTAL);
        this.lastKeepAliveTimestamp = this.timestamp;
        Long sent = (Long)this.keepAliveMap.remove((Object)id);
        if (sent != null) {
            this.lastPing = this.ping;
            this.ping = (int)(this.timestamp - sent);
            this.averagePing = (this.averagePing * 4 + this.ping) / 5;
            NMSManager.getInstance().updatePing(this);
            for (PingHandler pingHandler : this.checkData.getPingHandlers()) {
                pingHandler.handleKeepAlive(sent.longValue(), this.timestamp);
            }
        } else if (this.checkData.getKeepAliveCheck() != null) {
            NMSManager.getInstance().postToMainThread(() -> {
                if (this.keepAliveMap.remove((Object)id) == null && this.totalTicks > 600) {
                    this.checkData.getKeepAliveCheck().handleViolation(() -> String.format((String)"ID: %s", (Object[])new Object[]{id}), 1.0, true);
                }
            });
        }
        this.resetPingTicks();
    }

    public void handle(VPacketPlayInVehicleMove<?> packet) {
        if (this.isVehicle()) {
            this.vehicleLocation.setX(packet.getX());
            this.vehicleLocation.setY(packet.getY());
            this.vehicleLocation.setZ(packet.getZ());
            if (this.lastVehicleLocation != null && !this.vehicleLocation.sameXYZ((ILocation)this.lastVehicleLocation)) {
                for (VehicleCheck vehicleCheck : this.checkData.getVehicleChecks()) {
                    vehicleCheck.handle(this.lastVehicleLocation, this.vehicleLocation);
                }
            }
            this.lastVehicleLocation = this.vehicleLocation.clone();
        } else {
            this.lastVehicleLocation = null;
        }
    }

    public void handle(VPacketPlayInSteerVehicle<?> packet) {
        if ((this.vehicle || this.player.getVehicle() != null) && Math.abs((double)packet.getForward()) <= (double)0.98f && Math.abs((double)packet.getStrafe()) <= (double)0.98f) {
            this.wasVehicle = true;
            this.tickerMap.reset(TickerType.VEHICLE);
        }
    }

    public void handle(VPacketPlayInUseEntity<?> packet) {
        if (packet.getAction() == VPacketPlayInUseEntity.EntityUseAction.ATTACK) {
            this.tickerMap.increment(TickerType.ATTACKS);
            this.lastAttacked = null;
            this.lastAttackedId = packet.getId();
            if (packet.isPlayer()) {
                this.lastAttacked = packet.getPlayerData();
                ThreadLocalRandom random = ThreadLocalRandom.current();
                AlertManager alertManager = AlertManager.getInstance();
                if (this.spoofBan && random.nextDouble() < 0.1) {
                    alertManager.handleBan(this, this.spoofBanCheck, false);
                } else if (this.checkSpoofing && random.nextDouble() < 0.25) {
                    while (this.checkSpoofing) {
                        Alert alert = (Alert)this.spoofedAlerts.poll();
                        if (alert == null) {
                            this.checkSpoofing = false;
                            continue;
                        }
                        if (this.timestamp - alert.getTimestamp() >= TimeUnit.SECONDS.toMillis(15L)) continue;
                        alertManager.getExecutorService().submit(() -> alertManager.handleAlert(this, alert.getCheck(), alert.getData(), alert.getVl()));
                        break;
                    }
                }
            }
            this.lastAttackTicks = 0;
        }
    }

    public void handle(VPacketPlayInWindowClick<?> packet) {
        if (NMSManager.getInstance().getServerVersion().afterEq(ServerVersion.v1_11_R1) && packet.getWindow() == 0 && packet.getSlot() == 6 && packet.getItemStack() != null && packet.getItemStack().getType() == MaterialList.ELYTRA) {
            this.tickerMap.set(TickerType.ELYTRA_EXIT, -100);
        }
        this.tickerMap.reset(TickerType.WINDOW_CLICK);
    }

    public void handleOut(Transactionable transactionable) {
        if (transactionable.valid()) {
            long timestamp = System.currentTimeMillis();
            short id = transactionable.id();
            this.transactionMap.put((Object)id, (Object)this.nextTransaction);
            this.nextTransaction.send(id, timestamp);
            this.tickerMap.set(TickerType.LAST_SENT_TRANSACTION, TickerType.TOTAL);
            this.sentTransaction = true;
            this.lastTransaction = this.nextTransaction;
            this.nextTransaction = new Transaction(this.lastTransaction);
        }
    }

    public void handle(VPacketPlayOutAbilities<?> packet) {
        if (this.abilityHandler != null) {
            this.abilityHandler.handle(packet);
        }
    }

    public void handle(VPacketPlayOutAttachEntity<?> packet) {
        if (packet.getLeash() == 0 && packet.getEntityId() == this.player.getEntityId()) {
            this.vehicle = packet.getAttachId() > 0;
            this.vehicleId = packet.getAttachId();
            this.tickerMap.reset(TickerType.VEHICLE);
            this.wasVehicle = true;
        }
    }

    public void handle(VPacketPlayOutBlockChange<?> packet) {
        if (this.world != null) {
            this.world.handle(packet);
        }
    }

    public void handle(VPacketPlayOutEntity<?> packet) {
        PacketLocation last;
        int entityId;
        AtomicCappedQueue recentMoveQueue;
        if (this.tracker != null) {
            this.tracker.handle(packet);
        }
        if (packet.isPos() && (recentMoveQueue = (AtomicCappedQueue)this.recentMoveMap.get((Object)(entityId = packet.getId()))) != null && (last = (PacketLocation)recentMoveQueue.peekLast()) != null) {
            long now = System.currentTimeMillis();
            last.setNextOffset(now);
            PacketLocation location = packet.move(last);
            location.setNextOffset(Long.MAX_VALUE);
            location.setTimestamp(now);
            recentMoveQueue.addLast((Object)location);
        }
    }

    public void handle(VPacketPlayOutEntityDestroy<?> packet) {
        int[] ids;
        if (this.tracker != null) {
            this.tracker.handle(packet);
        }
        if ((ids = packet.getIds()).length == 1 && this.vehicle && ids[0] == this.vehicleId) {
            this.vehicle = false;
        }
        for (int id : ids) {
            this.recentMoveMap.remove((Object)id);
        }
    }

    public void handle(VPacketPlayOutEntityEffect<?> packet) {
        if (this.effectHandler != null) {
            this.effectHandler.handle(packet);
        }
    }

    public void handle(VPacketPlayOutEntityTeleport<?> packet) {
        AtomicCappedQueue recentMoveQueue;
        if (this.tracker != null) {
            this.tracker.handle(packet);
        }
        if ((recentMoveQueue = (AtomicCappedQueue)this.recentMoveMap.get((Object)packet.getId())) != null) {
            PacketLocation last = (PacketLocation)recentMoveQueue.peekLast();
            long now = System.currentTimeMillis();
            if (last != null) {
                last.setNextOffset(now);
            }
            PacketLocation location = packet.toLocation(now);
            recentMoveQueue.addLast((Object)location);
        }
    }

    public void handle(VPacketPlayOutEntityVelocity<?> packet) {
        if (this.velocityHandler != null) {
            this.velocityHandler.handle(packet);
        }
        if (packet.getId() == this.player.getEntityId()) {
            double dx = (double)packet.getMotX() / 8000.0;
            double dy = (double)packet.getMotY() / 8000.0;
            double dz = (double)packet.getMotZ() / 8000.0;
            this.handleVelocity(dx, dy, dz, false);
        }
    }

    public void handle(VPacketPlayOutExplosion<?> packet) {
        this.handleVelocity(packet.getMotX(), packet.getMotY(), packet.getMotZ(), true);
        if (this.velocityHandler != null) {
            this.velocityHandler.handle(packet);
        }
    }

    public void handle(VPacketPlayOutGameStateChange<?> packet) {
        if (this.abilityHandler != null) {
            this.abilityHandler.handle(packet);
        }
    }

    public void handle(VPacketPlayOutKeepAlive<?> packet) {
        long timestamp = System.currentTimeMillis();
        this.keepAliveMap.put((Object)packet.getId(), (Object)timestamp);
    }

    public void handle(VPacketPlayOutMapChunk<?> packet) {
        if (this.world != null) {
            this.world.handle(packet);
        }
    }

    public void handle(VPacketPlayOutMapChunkBulk<?> packet) {
        if (this.world != null) {
            this.world.handle(packet);
        }
    }

    public void handle(VPacketPlayOutMultiBlockChange<?> packet) {
        if (this.world != null) {
            this.world.handle(packet);
        }
    }

    public void handle(VPacketPlayOutNamedEntitySpawn<?> packet) {
        if (this.tracker != null) {
            this.tracker.handle(packet);
        }
        AtomicCappedQueue recentMoveQueue = (AtomicCappedQueue)this.recentMoveMap.computeIfAbsent((Object)packet.getId(), id -> new AtomicCappedQueue(80));
        PacketLocation location = packet.toLocation(System.currentTimeMillis());
        recentMoveQueue.addLast((Object)location);
    }

    public void handle(VPacketPlayOutOpenWindow<?> packet) {
        this.digging = false;
        this.lastInventoryOutTick = this.totalTicks;
    }

    public void handle(VPacketPlayOutPosition<?> packet) {
        long now = System.currentTimeMillis();
        JavaV.trim(this.teleportList, (int)1000);
        JavaV.trim(this.teleports, (int)1000);
        if (this.teleportHandler != null) {
            this.teleportHandler.handle(packet);
        }
        Teleport teleport = packet.toTeleport(this.tickerMap.get(TickerType.TOTAL), now);
        this.teleports.add((Object)teleport);
        this.recentTeleports.addFirst((Object)teleport);
        PlayerLocation playerLocation = packet.toLocation(this);
        this.teleportList.add((Object)playerLocation);
        this.teleportTicks = 0;
        this.lastTeleport = now;
        this.velY = 0.0;
        this.lastVelZ = 0.0;
        this.lastVelY = 0.0;
        this.lastVelX = 0.0;
        for (TeleportCheck teleportCheck : this.checkData.getTeleportChecks()) {
            teleportCheck.handle(packet, now);
        }
    }

    public void handle(VPacketPlayOutRemoveEntityEffect<?> packet) {
        if (this.effectHandler != null) {
            this.effectHandler.handle(packet);
        }
    }

    public void handle(VPacketPlayOutRespawn<?> packet) {
        if (this.world != null) {
            this.world.handle(packet);
        }
        if (this.abilityHandler != null) {
            this.abilityHandler.handle(packet);
        }
        this.inventoryOpen = false;
        this.blocking = false;
        this.lastRespawn = System.currentTimeMillis();
    }

    public void handle(VPacketPlayOutSetSlot<?> packet) {
        this.lastSetSlot = this.totalTicks;
    }

    public void handle(VPacketPlayOutSpawnPosition<?> packet) {
        long timestamp = System.currentTimeMillis();
        BlockPosition blockPosition = packet.getPosition();
        PlayerLocation playerLocation = new PlayerLocation(timestamp, this.totalTicks, (double)blockPosition.getX(), (double)blockPosition.getY(), (double)blockPosition.getZ(), 0.0f, 0.0f, null, false);
        this.teleportList.add((Object)playerLocation);
        this.teleportTicks = 0;
        this.lastTeleport = System.currentTimeMillis();
        this.velY = 0.0;
        this.lastVelZ = 0.0;
        this.lastVelY = 0.0;
        this.lastVelX = 0.0;
        this.spawnLocation = packet.getPosition();
    }

    public void handle(VPacketPlayOutUnloadChunk<?> packet) {
        if (this.world != null) {
            this.world.handle(packet);
        }
    }

    public void handle(VPacketPlayOutUpdateAttributes<?> packet) {
        if (this.attributeHandler != null) {
            this.attributeHandler.handle(packet);
        }
    }

    public void handle(VPacketPlayOutEntityMetadata<?> packet) {
        if (this.metadataHandler != null) {
            this.metadataHandler.handle(packet);
        }
    }

    public void preProcess(VPacket<?> packet) {
        this.timestamp = System.currentTimeMillis();
    }

    public void postProcess(VPacket<?> packet) {
        VPacketPlayInFlying flying;
        for (PacketCheck check : this.checkData.getPacketChecks()) {
            try {
                check.handle(packet, this.timestamp);
            }
            catch (Throwable e) {
                Bukkit.getLogger().log(Level.WARNING, check.getType().getName() + " " + check.getSubType() + " (" + check.getFriendlyName() + ") failed to handle " + packet.getClass().getName() + " ", e);
            }
        }
        if (packet instanceof VPacketPlayInFlying && !(flying = (VPacketPlayInFlying)packet).isTeleport()) {
            JavaV.executeSafely(this.end, () -> " end of next tick");
        }
    }

    public void handlePacketListeners(VPacket<?> packet) {
        PacketHandler[] handlers = this.checkData.getHandle(packet);
        if (handlers == null) {
            return;
        }
        for (PacketHandler listener : handlers) {
            try {
                packet.handle(listener);
            }
            catch (Throwable e) {
                Check check = (Check)listener;
                Bukkit.getLogger().log(Level.WARNING, check.getType().getName() + " " + check.getSubType() + " (" + check.getFriendlyName() + ") failed to handle " + packet.getClass().getName() + " ", e);
            }
        }
    }

    private void handleVelocity(double dx, double dy, double dz, boolean explosion) {
        if (!this.isVehicle()) {
            this.velY = dy;
            this.lastVelX = dx;
            this.lastVelZ = dz;
            this.horizontalVelocityTicks = 0;
            int currentTicks = this.totalTicks;
            Cuboid cuboid = Cuboid.withLimit((ILocation)this.lastLocation, (ILocation)this.location, (int)16).move(0.0, 1.5, 0.0).expand(1.25, 1.0, 1.25);
            World world = this.player.getWorld();
            Runnable runnable = () -> {
                boolean blockAbove;
                int ticksOffset = currentTicks - this.totalTicks;
                boolean bl = blockAbove = this.velY > 0.0 && !cuboid.checkBlocks(this.player, world, material -> material == MaterialList.AIR);
                if (!blockAbove && this.lastVelY == 0.0) {
                    this.lastVelY = this.velY;
                    this.verticalVelocityTicks = ticksOffset;
                } else {
                    this.lastVelY = 0.0;
                }
            };
            if (Bukkit.isPrimaryThread()) {
                runnable.run();
            } else {
                NMSManager.getInstance().postToMainThread(runnable);
            }
        }
        this.tickerMap.reset(TickerType.VELOCITY);
        Velocity velocity = new Velocity(this.tickerMap.get(TickerType.TOTAL), System.currentTimeMillis(), dx, dy, dz, explosion);
        this.velocityQueue.add((Object)velocity);
        this.velocityData.add((Object)velocity.getClient());
        double[] dArray = new double[]{dx, dy, dz};
        if (MathUtil.hypotSquared((double[])dArray) > 9.0E-4) {
            this.velocityTicks = Math.min((int)this.velocityTicks, (int)0) - (int)Math.ceil((double)(Math.pow((double)(MathUtil.hypotSquared((double[])new double[]{dx, dy, dz}) * 2.0), (double)1.75) * 4.0));
            double[] dArray2 = new double[]{dx, dz};
            if (MathUtil.hypotSquared((double[])dArray2) > 9.0E-4) {
                this.horizontalSpeedTicks = Math.min((int)this.horizontalSpeedTicks, (int)0) - (int)Math.ceil((double)(Math.pow((double)(MathUtil.hypotSquared((double[])new double[]{dx, dz}) * 2.0), (double)2.0) * 8.0));
            }
        }
    }

    public void handleItem(ItemStack itemStack) {
        Material type = itemStack.getType();
        if (this.lastFace >= 0 && this.lastFace <= Direction.values().length) {
            Direction direction = Direction.values()[this.lastFace];
            if (type != null && type != MaterialList.AIR && (type.isBlock() || type == MaterialList.WEB)) {
                BlockPosition placedBlock = this.lastBlockPosition.copy().move(direction, 1);
                this.reset(TickerType.PLACED_BLOCK);
                Cuboid cuboid = new Cuboid((IVector3d)this.location);
                if (type == MaterialList.WEB) {
                    cuboid.add(-1.0, 1.0, -1.0, 2.0, -1.0, 1.0);
                } else {
                    cuboid.add(-0.5, 0.5, -1.0, 1.0, -0.5, 0.5);
                }
                World world = this.player.getWorld();
                if (cuboid.containsBlock(world, placedBlock)) {
                    this.reset(TickerType.PLACED_BLOCK_UNDER);
                }
            }
        }
        this.handleInteraction(type);
    }

    public void handleInteraction(Material type) {
        if (type == MaterialList.WATER_BUCKET) {
            this.reset(TickerType.WATER_BUCKET);
        } else if (type == MaterialList._FIREWORK) {
            this.tickerMap.set(TickerType.ELYTRA_BOOST, -10);
        }
    }

    public void setBrand(String brand) {
        this.brand = brand;
        this.clientData.setBrand(brand);
    }

    public void checkBrand(String brand) {
        this.checkData.getBrandCheck().handle(brand);
    }

    public boolean isFocused(Check check) {
        return this.focus == null || this.focus == check.getType() && check.getSubType().equals((Object)this.focusSubType);
    }

    public boolean isTeleportingV2() {
        return this.tickerMap.get(TickerType.TELEPORT) == 0;
    }

    public boolean isTeleportingMST() {
        return this.tickerMap.get(TickerType.MOVES_SINCE_TELEPORT) == 0;
    }

    public void reset() {
        this.sprinting = false;
        this.tickerMap.reset(TickerType.SPRINT);
        this.sneaking = false;
    }

    public void reset(TickerType type) {
        this.tickerMap.reset(type);
    }

    public void loadData() {
        StorageEngine storageEngine = StorageEngine.getInstance();
        if (storageEngine.isConnected()) {
            Database database = storageEngine.getDatabase();
            if (BukkitUtil.hasPermission((Player)this.player, (String)"verus.alerts")) {
                database.loadAlerts(this);
            }
            if (storageEngine.getVerusConfig().isPersistence()) {
                database.loadData(this);
            }
        }
    }

    public void saveData() {
        StorageEngine storageEngine = StorageEngine.getInstance();
        if (storageEngine.isConnected() && storageEngine.getVerusConfig().isPersistence()) {
            Database database = storageEngine.getDatabase();
            if (this.banned) {
                database.removeData(this);
            } else {
                database.updateData(this);
            }
        }
    }

    public void release() {
        if (this.world != null) {
            this.world.release();
        }
    }

    public void handleTransaction() {
        int tries = 0;
        do {
            this.lastTransactionID = (short)(this.lastTransactionID + 1);
        } while (this.transactionMap.containsKey((Object)this.lastTransactionID) && tries++ < 500);
        NMSManager.getInstance().sendTransaction(this.player, this.lastTransactionID);
    }

    public void fuckOff() {
        NMSManager nmsManager = NMSManager.getInstance();
        nmsManager.close(this);
        this.enabled = false;
    }

    public void closeInventory() {
        this.player.closeInventory();
        this.inventoryOpen = false;
    }

    public int getMoveTicks() {
        return (int)Math.floor((double)((double)Math.min((int)this.transactionPing, (int)this.averageTransactionPing) / 125.0));
    }

    @Deprecated
    public Integer getLag() {
        return (int)Math.floor((double)MathUtil.variance((Number)Integer.valueOf((int)0), this.connectionFrequency));
    }

    public void resetPingTicks() {
        this.shouldHaveReceivedPing.reset();
        this.pingTicks.reset();
        this.maxPingTicks.reset();
        this.maxPingTick2.reset();
    }

    public int getPingTicks() {
        return (Integer)this.pingTicks.get();
    }

    public int getMaxPingTicks() {
        return (Integer)this.maxPingTicks.get();
    }

    public int getMaxPingTicks2() {
        return (Integer)this.maxPingTick2.get();
    }

    public boolean shouldHaveReceivedPing() {
        return (Boolean)this.shouldHaveReceivedPing.get();
    }

    @Deprecated
    public boolean hasLag(long timestamp) {
        return this.lastFlying != 0L && this.lastDelayed != 0L && timestamp - this.lastDelayed < 110L;
    }

    @Deprecated
    public boolean hasLag() {
        return this.hasLag(this.lastFlying);
    }

    @Deprecated
    public boolean hasFast(long timestamp) {
        return this.lastFlying != 0L && this.lastFast != 0L && timestamp - this.lastFast < 110L;
    }

    @Deprecated
    public boolean hasFast() {
        return this.hasFast(this.lastFlying);
    }

    @Deprecated
    @Warning
    public boolean isTeleporting() {
        return this.isTeleporting(1);
    }

    @Deprecated
    public boolean hasRecentTeleport(int multiplier, ILocationGround test) {
        Teleport teleport;
        if (test.isGround()) {
            return false;
        }
        int until = this.getTotalTicks() - this.getMaxPingTicks2() * multiplier;
        Iterator iterator = this.recentTeleports.iterator();
        while (iterator.hasNext() && (teleport = (Teleport)iterator.next()).getTicks() >= until) {
            if (!test.matches((ILocation)teleport)) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public boolean hasRecentTeleport(int multiplier, ILocationGround from, ILocationGround to) {
        Teleport teleport;
        if (from.isGround()) {
            return this.hasRecentTeleport(multiplier, to);
        }
        int until = this.getTotalTicks() - this.getMaxPingTicks2() * multiplier;
        Iterator iterator = this.recentTeleports.iterator();
        while (iterator.hasNext() && (teleport = (Teleport)iterator.next()).getTicks() >= until) {
            if (!from.matches((ILocation)teleport) && !to.matches((ILocation)teleport)) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    @Warning
    public boolean isTeleporting(int multiplier) {
        return this.teleportTicks <= this.getMaxPingTicks() * multiplier;
    }

    public boolean isBlocking() {
        return this.blockTicks >= (this.getMaxPingTicks() + 2) * 2;
    }

    public boolean isLevitating() {
        return this.tickerMap.get(TickerType.LEVITATION) <= (this.getMaxPingTicks() + 2) * 2;
    }

    public boolean hadLevitation() {
        int value = this.tickerMap.get(TickerType.LEVITATION);
        return value > 0 && value <= (this.getMaxPingTicks() + 2) * 2;
    }

    public boolean hasJumpBoost() {
        return this.tickerMap.get(TickerType.JUMP_BOOST) <= this.getMaxPingTicks() * 2;
    }

    public boolean hadJumpBoost() {
        int value = this.tickerMap.get(TickerType.JUMP_BOOST);
        return value > 0 && value <= (this.getMaxPingTicks() + 2) * 2;
    }

    public boolean hadSpeedBoost() {
        int value = this.tickerMap.get(TickerType.SPEED_BOOST);
        return value > 0 && value <= (this.getMaxPingTicks() + 2) * 2;
    }

    public boolean hadAttributes() {
        int value = this.tickerMap.get(TickerType.ATTRIBUTES);
        return value > 0 && value <= (this.getMaxPingTicks() + 2) * 2;
    }

    public boolean isSlimePush() {
        return this.tickerMap.get(TickerType.SLIME_PUSH) < (this.getMaxPingTicks() + 2) * 2;
    }

    public boolean isSuffocating() {
        return this.tickerMap.get(TickerType.SUFFOCATING) <= this.getMaxPingTicks() * 2;
    }

    public boolean isRiptiding() {
        return this.tickerMap.get(TickerType.RIPTIDE) <= (this.getMaxPingTicks() + 2) * 3;
    }

    public boolean isVehicle() {
        return this.tickerMap.get(TickerType.VEHICLE) <= (this.getMaxPingTicks() + 2) * 2;
    }

    @Deprecated
    public boolean isSurvival(int multiplier) {
        return this.survivalTicks > this.getMaxPingTicks() * multiplier;
    }

    @Deprecated
    public boolean isSurvival() {
        return this.isSurvival(1);
    }

    @Deprecated
    public boolean isFlying() {
        return this.flyingTicks <= (this.getMaxPingTicks() + 2) * 3;
    }

    @Deprecated
    public boolean canFly() {
        return this.allowFlightTicks <= (this.getMaxPingTicks() + 2) * 3;
    }

    public boolean isGliding() {
        return this.tickerMap.get(TickerType.GLIDING) <= (this.getMaxPingTicks() + 2) * 5 || this.metadataHandler != null && (this.metadataHandler.isElytraFlying() || this.metadataHandler.isToggledElytra());
    }

    public boolean isFallFlying() {
        int ticks = (this.getMaxPingTicks() + 2) * 5;
        return this.fallFlying || this.tickerMap.get(TickerType.FALL_FLYING) <= ticks || this.tickerMap.get(TickerType.ELYTRA_EXIT) <= ticks;
    }

    public boolean isBoosting() {
        return this.tickerMap.get(TickerType.ELYTRA_BOOST) <= (this.getMaxPingTicks() + 2) * 5;
    }

    public boolean isHooked() {
        return this.tickerMap.get(TickerType.HOOKED) <= (this.getMaxPingTicks() + 2) * 3;
    }

    public boolean hasPlacedBlock(boolean withPing) {
        return this.tickerMap.get(TickerType.PLACED_BLOCK_UNDER) < (this.getPingTicks() + 1) * 2;
    }

    public boolean hasPlacedBucket() {
        return this.tickerMap.get(TickerType.WATER_BUCKET) <= this.getMaxPingTicks() * 2;
    }

    public boolean isSpawned() {
        return this.spawned > 600;
    }

    public boolean isSprinting(boolean definite) {
        return this.sprinting == null ? !definite : this.sprinting != false && (!definite || this.tickerMap.get(TickerType.SPRINT) + 2 < this.tickerMap.get(TickerType.TELEPORT)) || !definite && this.player.isSprinting();
    }

    public double getTotalViolations() {
        double total = 0.0;
        for (Check check : this.checkData.getChecks()) {
            if (!CheckManager.getInstance().isEnabled(check)) continue;
            total += Math.max((double)check.getViolations(), (double)0.0);
        }
        return total;
    }

    public double getMovementSpeed() {
        double walkSpeed = this.player.getWalkSpeed();
        double movementSpeed = NMSManager.getInstance().getMovementSpeed(this.player) * 2.0;
        if (movementSpeed > walkSpeed) {
            this.tickerMap.reset(TickerType.ATTRIBUTES);
        }
        return movementSpeed;
    }

    public boolean isSneaking() {
        return this.sneaking != null && this.sneaking != false;
    }

    public String getInfo() {
        Object[] effects;
        String brand;
        ClientType clientType;
        String toReturn = VerusPlugin.COLOR + this.name + "'s Information: \n";
        if (this.version != null) {
            toReturn = toReturn + ChatColor.GRAY + "- " + VerusPlugin.COLOR + "Version: " + ChatColor.WHITE + this.version.getName() + "\n";
        }
        if (!(clientType = this.clientData.getType()).isUnknown()) {
            toReturn = toReturn + ChatColor.GRAY + "- " + VerusPlugin.COLOR + "Client Data: " + ChatColor.WHITE + clientType.getDisplay() + "\n";
        }
        if ((brand = this.clientData.getBrand()) != null && !brand.isEmpty() && !brand.equals((Object)"Unknown")) {
            toReturn = toReturn + ChatColor.GRAY + "- " + VerusPlugin.COLOR + "Brand: " + ChatColor.WHITE + brand + "\n";
        }
        if (this.enabled) {
            toReturn = toReturn + ChatColor.GRAY + "- " + VerusPlugin.COLOR + "Average Ping: " + ChatColor.WHITE + this.getAverageTransactionPing() + "\n";
            toReturn = toReturn + ChatColor.GRAY + "- " + VerusPlugin.COLOR + "Lag: " + ChatColor.WHITE + this.getLag() + ChatColor.GRAY + " (" + this.getConnectionFrequency().size() + ")\n";
        }
        toReturn = (effects = (PotionEffect[])this.effects.get()).length > 0 ? toReturn + ChatColor.GRAY + "- " + VerusPlugin.COLOR + "Effects: " + ChatColor.WHITE + (String)Arrays.stream((Object[])effects).map(StringUtil::formatEffect).collect(Collectors.joining((CharSequence)(ChatColor.GRAY + ", " + ChatColor.WHITE))) : toReturn + ChatColor.GRAY + "- " + VerusPlugin.COLOR + "Effects: " + ChatColor.WHITE + "None";
        return toReturn;
    }

    public Player getPlayer() {
        return this.player;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public CheckData getCheckData() {
        return this.checkData;
    }

    public TickerMap getTickerMap() {
        return this.tickerMap;
    }

    public ClientData getClientData() {
        return this.clientData;
    }

    public ClientVersion getVersion() {
        return this.version;
    }

    @Deprecated
    public Queue<PlayerLocation> getTeleportList() {
        return this.teleportList;
    }

    @Deprecated
    public BasicDeque<Teleport> getRecentTeleports() {
        return this.recentTeleports;
    }

    @Deprecated
    public Queue<Velocity> getVelocityQueue() {
        return this.velocityQueue;
    }

    @Deprecated
    public Queue<BiConsumer<Integer, Double>> getPingQueue() {
        return this.pingQueue;
    }

    @Deprecated
    public BasicDeque<Integer> getConnectionFrequency() {
        return this.connectionFrequency;
    }

    @Deprecated
    public Teleport getLastTeleport2() {
        return this.lastTeleport2;
    }

    @Deprecated
    public Queue<Teleport> getTeleports() {
        return this.teleports;
    }

    public Queue<Alert> getSpoofedAlerts() {
        return this.spoofedAlerts;
    }

    public Map<Integer, AtomicCappedQueue<PacketLocation>> getRecentMoveMap() {
        return this.recentMoveMap;
    }

    public CheckLocalQueue<ClientVelocity> getVelocityData() {
        return this.velocityData;
    }

    public Map<Long, Long> getKeepAliveMap() {
        return this.keepAliveMap;
    }

    public Map<Short, Transaction> getTransactionMap() {
        return this.transactionMap;
    }

    public Queue<ReachBase> getReachData() {
        return this.reachData;
    }

    public PlayerLocation getLocation() {
        return this.location;
    }

    public PlayerLocation getLastLocation() {
        return this.lastLocation;
    }

    public PlayerLocation getLastLastLocation() {
        return this.lastLastLocation;
    }

    public me.levansj01.verus.util.location.PacketLocation getCurrentLocation2() {
        return this.currentLocation2;
    }

    public me.levansj01.verus.util.location.PacketLocation getLastLocation2() {
        return this.lastLocation2;
    }

    public me.levansj01.verus.util.location.PacketLocation getLastLastLocation2() {
        return this.lastLastLocation2;
    }

    public BlockPosition getSpawnLocation() {
        return this.spawnLocation;
    }

    public PlayerData getLastAttacked() {
        return this.lastAttacked;
    }

    public long getLastFlying() {
        return this.lastFlying;
    }

    public long getLastLastFlying() {
        return this.lastLastFlying;
    }

    public long getLastDelayed() {
        return this.lastDelayed;
    }

    public long getLastFast() {
        return this.lastFast;
    }

    public long getLastTeleport() {
        return this.lastTeleport;
    }

    public long getLastClientTransaction() {
        return this.lastClientTransaction;
    }

    public long getLastRespawn() {
        return this.lastRespawn;
    }

    public long getLastKeepAliveTimestamp() {
        return this.lastKeepAliveTimestamp;
    }

    public int getLastTeleportTicks() {
        return this.lastTeleportTicks;
    }

    public int getFlyingTicks() {
        return this.flyingTicks;
    }

    public int getAllowFlightTicks() {
        return this.allowFlightTicks;
    }

    public int getVelocityTicks() {
        return this.velocityTicks;
    }

    public int getVerticalVelocityTicks() {
        return this.verticalVelocityTicks;
    }

    public int getHorizontalVelocityTicks() {
        return this.horizontalVelocityTicks;
    }

    public int getHorizontalSpeedTicks() {
        return this.horizontalSpeedTicks;
    }

    public int getTicks() {
        return this.ticks;
    }

    public int getTotalTicks() {
        return this.totalTicks;
    }

    public int getLastSentTransaction() {
        return this.lastSentTransaction;
    }

    public int getLastKeepAlive() {
        return this.lastKeepAlive;
    }

    public int getSurvivalTicks() {
        return this.survivalTicks;
    }

    public int getLastAttackTicks() {
        return this.lastAttackTicks;
    }

    public int getNonMoveTicks() {
        return this.nonMoveTicks;
    }

    public int getLastNonMoveTicks() {
        return this.lastNonMoveTicks;
    }

    public int getLastInventoryTick() {
        return this.lastInventoryTick;
    }

    public int getLastInventoryOutTick() {
        return this.lastInventoryOutTick;
    }

    public int getLastSetSlot() {
        return this.lastSetSlot;
    }

    public int getLastTransactionPing() {
        return this.lastTransactionPing;
    }

    public int getAverageTransactionPing() {
        return this.averageTransactionPing;
    }

    public int getPing() {
        return this.ping;
    }

    public int getLastPing() {
        return this.lastPing;
    }

    public int getAveragePing() {
        return this.averagePing;
    }

    public int getLastAttackedId() {
        return this.lastAttackedId;
    }

    public int getBlockTicks() {
        return this.blockTicks;
    }

    public int getLastFace() {
        return this.lastFace;
    }

    public int getReceivedTransactions() {
        return this.receivedTransactions;
    }

    public boolean isReceivedTransaction() {
        return this.receivedTransaction;
    }

    public boolean isWasVehicle() {
        return this.wasVehicle;
    }

    public boolean isSentTransaction() {
        return this.sentTransaction;
    }

    public boolean isMoved() {
        return this.moved;
    }

    public boolean isAimed() {
        return this.aimed;
    }

    public boolean isPlacing() {
        return this.placing;
    }

    public boolean isSwingDigging() {
        return this.swingDigging;
    }

    public boolean isAbortedDigging() {
        return this.abortedDigging;
    }

    public boolean isStoppedDigging() {
        return this.stoppedDigging;
    }

    public boolean isReady() {
        return this.ready;
    }

    public BlockPosition getDiggingBlock() {
        return this.diggingBlock;
    }

    public Direction getDiggingBlockFace() {
        return this.diggingBlockFace;
    }

    public Boolean getSprinting() {
        return this.sprinting;
    }

    public Boolean getSneaking() {
        return this.sneaking;
    }

    public ResetState<PotionEffect[]> getEffects() {
        return this.effects;
    }

    public ResetState<Boolean> getShouldHaveReceivedPing() {
        return this.shouldHaveReceivedPing;
    }

    public ResetState<Integer> getSpeedLevel() {
        return this.speedLevel;
    }

    public ResetState<Integer> getSlowLevel() {
        return this.slowLevel;
    }

    public ResetState<Integer> getJumpLevel() {
        return this.jumpLevel;
    }

    public ResetState<Integer> getMaxPingTick2() {
        return this.maxPingTick2;
    }

    public BlockPosition getLastBlockPosition() {
        return this.lastBlockPosition;
    }

    public boolean isBanned() {
        return this.banned;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isCheckSpoofing() {
        return this.checkSpoofing;
    }

    public boolean isSpoofBan() {
        return this.spoofBan;
    }

    public boolean isResetVelocity() {
        return this.resetVelocity;
    }

    public boolean isDigging() {
        return this.digging;
    }

    public boolean isAlerts() {
        return this.alerts;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public boolean isInventoryOpen() {
        return this.inventoryOpen;
    }

    public short getLastTransactionID() {
        return this.lastTransactionID;
    }

    public int getTeleportTicks() {
        return this.teleportTicks;
    }

    public int getLastFakeEntityDamageTicks() {
        return this.lastFakeEntityDamageTicks;
    }

    public int getSuffocationTicks() {
        return this.suffocationTicks;
    }

    public int getTransactionPing() {
        return this.transactionPing;
    }

    public int getSpawned() {
        return this.spawned;
    }

    public int getVehicleId() {
        return this.vehicleId;
    }

    public int getFallDamage() {
        return this.fallDamage;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public long getNanos() {
        return this.nanos;
    }

    public double getLastVelY() {
        return this.lastVelY;
    }

    public double getLastVelX() {
        return this.lastVelX;
    }

    public double getLastVelZ() {
        return this.lastVelZ;
    }

    public double getVelY() {
        return this.velY;
    }

    public String getBrand() {
        return this.brand;
    }

    public Check getSpoofBanCheck() {
        return this.spoofBanCheck;
    }

    public CheckType getFocus() {
        return this.focus;
    }

    public String getFocusSubType() {
        return this.focusSubType;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    public Transaction getLastTransaction() {
        return this.lastTransaction;
    }

    public Transaction getNextTransaction() {
        return this.nextTransaction;
    }

    public Queue<Runnable> getStart() {
        return this.start;
    }

    public Queue<Runnable> getEnd() {
        return this.end;
    }

    public Queue<Runnable> getNextTrans() {
        return this.nextTrans;
    }

    public IVerusWorld getWorld() {
        return this.world;
    }

    public ITeleportHandler getTeleportHandler() {
        return this.teleportHandler;
    }

    public IVelocityHandler getVelocityHandler() {
        return this.velocityHandler;
    }

    public IAbilityHandler getAbilityHandler() {
        return this.abilityHandler;
    }

    public IEffectHandler getEffectHandler() {
        return this.effectHandler;
    }

    public ITracker getTracker() {
        return this.tracker;
    }

    public IAttributeHandler getAttributeHandler() {
        return this.attributeHandler;
    }

    public IMetadataHandler getMetadataHandler() {
        return this.metadataHandler;
    }

    public PlayerLocation getVehicleLocation() {
        return this.vehicleLocation;
    }

    public PlayerLocation getLastVehicleLocation() {
        return this.lastVehicleLocation;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCheckSpoofing(boolean checkSpoofing) {
        this.checkSpoofing = checkSpoofing;
    }

    public void setSpoofBan(boolean spoofBan) {
        this.spoofBan = spoofBan;
    }

    public void setResetVelocity(boolean resetVelocity) {
        this.resetVelocity = resetVelocity;
    }

    public void setDigging(boolean digging) {
        this.digging = digging;
    }

    public void setAlerts(boolean alerts) {
        this.alerts = alerts;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setInventoryOpen(boolean inventoryOpen) {
        this.inventoryOpen = inventoryOpen;
    }

    public void setLastTransactionID(short lastTransactionID) {
        this.lastTransactionID = lastTransactionID;
    }

    public void setTeleportTicks(int teleportTicks) {
        this.teleportTicks = teleportTicks;
    }

    public void setLastFakeEntityDamageTicks(int lastFakeEntityDamageTicks) {
        this.lastFakeEntityDamageTicks = lastFakeEntityDamageTicks;
    }

    public void setSuffocationTicks(int suffocationTicks) {
        this.suffocationTicks = suffocationTicks;
    }

    public void setTransactionPing(int transactionPing) {
        this.transactionPing = transactionPing;
    }

    public void setSpawned(int spawned) {
        this.spawned = spawned;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setFallDamage(int fallDamage) {
        this.fallDamage = fallDamage;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setNanos(long nanos) {
        this.nanos = nanos;
    }

    public void setLastVelY(double lastVelY) {
        this.lastVelY = lastVelY;
    }

    public void setLastVelX(double lastVelX) {
        this.lastVelX = lastVelX;
    }

    public void setLastVelZ(double lastVelZ) {
        this.lastVelZ = lastVelZ;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public void setSpoofBanCheck(Check spoofBanCheck) {
        this.spoofBanCheck = spoofBanCheck;
    }

    public void setFocus(CheckType focus) {
        this.focus = focus;
    }

    public void setFocusSubType(String focusSubType) {
        this.focusSubType = focusSubType;
    }
}
