package club.mineman.antigamingchair.data;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.ICheck;
import club.mineman.antigamingchair.check.impl.aimassist.AimAssistA;
import club.mineman.antigamingchair.check.impl.aimassist.AimAssistB;
import club.mineman.antigamingchair.check.impl.aimassist.AimAssistC;
import club.mineman.antigamingchair.check.impl.autoclicker.*;
import club.mineman.antigamingchair.check.impl.badpackets.*;
import club.mineman.antigamingchair.check.impl.fly.FlyA;
import club.mineman.antigamingchair.check.impl.fly.FlyB;
import club.mineman.antigamingchair.check.impl.fly.FlyC;
import club.mineman.antigamingchair.check.impl.inventory.InventoryA;
import club.mineman.antigamingchair.check.impl.inventory.InventoryB;
import club.mineman.antigamingchair.check.impl.killaura.*;
import club.mineman.antigamingchair.check.impl.range.RangeA;
import club.mineman.antigamingchair.check.impl.timer.TimerA;
import club.mineman.antigamingchair.check.impl.velocity.VelocityA;
import club.mineman.antigamingchair.check.impl.velocity.VelocityB;
import club.mineman.antigamingchair.check.impl.velocity.VelocityC;
import club.mineman.antigamingchair.check.impl.velocity.VelocityD;
import club.mineman.antigamingchair.client.ClientType;
import club.mineman.antigamingchair.location.CustomLocation;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {
    public static final Class[] CHECKS;
    private static final Map<Class, Constructor> CONSTRUCTORS;

    static {
        CHECKS = new Class[]{AimAssistB.class, AimAssistA.class, AimAssistC.class, AutoClickerA.class, AutoClickerB.class, AutoClickerC.class, AutoClickerD.class, AutoClickerE.class, AutoClickerF.class, AutoClickerG.class, BadPacketsA.class, BadPacketsB.class, BadPacketsC.class, BadPacketsD.class, BadPacketsE.class, BadPacketsF.class, FlyA.class, FlyB.class, FlyC.class, InventoryA.class, InventoryB.class, KillAuraA.class, KillAuraB.class, KillAuraC.class, KillAuraD.class, KillAuraE.class, KillAuraF.class, RangeA.class, TimerA.class, VelocityA.class, VelocityB.class, VelocityC.class, VelocityD.class};
        CONSTRUCTORS = new ConcurrentHashMap<>();
        Class<? extends ICheck>[] checks;
        for (int length = (checks = PlayerData.CHECKS).length, i = 0; i < length; ++i) {
            final Class<? extends ICheck> check = checks[i];
            try {
                PlayerData.CONSTRUCTORS.put(check, check.getConstructor(AntiGamingChair.class, PlayerData.class));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    private final Map<UUID, List<CustomLocation>> recentPlayerPackets;
    private final Map<ICheck, Set<Long>> checkViolationTimes;
    private final Map<Class, ICheck> checkMap;
    private final Map<Integer, Long> keepAliveTimes;
    private final Map<ICheck, Double> checkVlMap;
    private final Set<UUID> playersWatching;
    private final Set<String> filteredPhrases;
    private final Set<CustomLocation> teleportLocations;
    private StringBuilder sniffedPacketBuilder;
    private CustomLocation lastMovePacket;
    private ClientType client;
    private boolean allowTeleport;
    private boolean inventoryOpen;
    private boolean sendingVape;
    private boolean attackedSinceVelocity;
    private boolean underBlock;
    private boolean sprinting;
    private boolean inLiquid;
    private boolean onGround;
    private boolean sniffing;
    private boolean onStairs;
    private boolean banWave;
    private boolean placing;
    private boolean banning;
    private boolean digging;
    private boolean inWeb;
    private boolean onIce;
    private double lastGroundY;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private long lastDelayedMovePacket;
    private long lastAttackPacket;
    private long lastTeleportTime;
    private long lastVelocity;
    private long ping;
    private int velocityH;
    private int velocityV;
    private int cps;

    public PlayerData(final AntiGamingChair plugin) {
        this.recentPlayerPackets = new HashMap<>();
        this.checkViolationTimes = new HashMap<>();
        this.checkMap = new HashMap<>();
        this.keepAliveTimes = new HashMap<>();
        this.checkVlMap = new HashMap<>();
        this.playersWatching = new HashSet<>();
        this.filteredPhrases = new HashSet<>();
        this.teleportLocations = new HashSet<>();
        this.sniffedPacketBuilder = new StringBuilder();
        this.client = ClientType.VANILLA;
        for (final Class<? extends ICheck> check : PlayerData.CONSTRUCTORS.keySet()) {
            final Constructor<? extends ICheck> constructor = PlayerData.CONSTRUCTORS.get(check);
            try {
                this.checkMap.put(check, constructor.newInstance(plugin, this));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex4) {
                ex4.printStackTrace();
            }
        }
    }

    public ICheck getCheck(final Class clazz) {
        return this.checkMap.get(clazz);
    }

    public CustomLocation getLastPlayerPacket(final UUID playerUUID, final int index) {
        if (this.recentPlayerPackets.containsKey(playerUUID) && this.recentPlayerPackets.get(playerUUID).size() > index) {
            return this.recentPlayerPackets.get(playerUUID).get(this.recentPlayerPackets.get(playerUUID).size() - index);
        }
        return null;
    }

    public void addPlayerPacket(final UUID playerUUID, final CustomLocation customLocation) {
        List<CustomLocation> customLocations = this.recentPlayerPackets.get(playerUUID);
        if (customLocations == null) {
            customLocations = new ArrayList<>();
        }
        if (customLocations.size() == 20) {
            customLocations.remove(0);
        }
        customLocations.add(customLocation);
        this.recentPlayerPackets.put(playerUUID, customLocations);
    }

    public void addTeleportLocation(final CustomLocation teleportLocation) {
        this.teleportLocations.add(teleportLocation);
    }

    public boolean allowTeleport(final CustomLocation teleportLocation) {
        for (final CustomLocation customLocation : this.teleportLocations) {
            final double delta = Math.pow(teleportLocation.getX() - customLocation.getX(), 2.0) + Math.pow(teleportLocation.getY() - customLocation.getY(), 2.0) + Math.pow(teleportLocation.getZ() - customLocation.getZ(), 2.0);
            if (delta == 0.0) {
                this.teleportLocations.remove(customLocation);
                return true;
            }
        }
        return false;
    }

    public double getCheckVl(final ICheck check) {
        if (!this.checkVlMap.containsKey(check)) {
            this.checkVlMap.put(check, 0.0);
        }
        return this.checkVlMap.get(check);
    }

    public void setCheckVl(double vl, final ICheck check) {
        if (vl < 0.0) {
            vl = 0.0;
        }
        this.checkVlMap.put(check, vl);
    }

    public boolean isPlayerWatching(final Player player) {
        return this.playersWatching.contains(player.getUniqueId());
    }

    public void togglePlayerWatching(final Player player) {
        if (!this.playersWatching.remove(player.getUniqueId())) {
            this.playersWatching.add(player.getUniqueId());
        }
    }

    public boolean isKeywordFiltered(final String keyword) {
        return this.filteredPhrases.contains(keyword);
    }

    public void toggleKeywordFilter(final String keyword) {
        if (!this.filteredPhrases.remove(keyword)) {
            this.filteredPhrases.add(keyword);
        }
    }

    public boolean keepAliveExists(final int id) {
        return this.keepAliveTimes.containsKey(id);
    }

    public long getKeepAliveTime(final int id) {
        final long time = this.keepAliveTimes.get(id);
        this.keepAliveTimes.remove(id);
        return time;
    }

    public void addKeepAliveTime(final int id) {
        this.keepAliveTimes.put(id, System.currentTimeMillis());
    }

    public int getViolations(final ICheck check, final Long time) {
        final Set<Long> timestamps = this.checkViolationTimes.get(check);
        if (timestamps != null) {
            int violations = 0;
            for (final long timestamp : timestamps) {
                if (System.currentTimeMillis() - timestamp <= time) {
                    ++violations;
                }
            }
            return violations;
        }
        return 0;
    }

    public void addViolation(final ICheck check) {
        Set<Long> timestamps = this.checkViolationTimes.get(check);
        if (timestamps == null) {
            timestamps = new HashSet<>();
        }
        timestamps.add(System.currentTimeMillis());
        this.checkViolationTimes.put(check, timestamps);
    }

    public Map<UUID, List<CustomLocation>> getRecentPlayerPackets() {
        return this.recentPlayerPackets;
    }

    public Map<ICheck, Set<Long>> getCheckViolationTimes() {
        return this.checkViolationTimes;
    }

    public Map<Class, ICheck> getCheckMap() {
        return this.checkMap;
    }

    public Map<Integer, Long> getKeepAliveTimes() {
        return this.keepAliveTimes;
    }

    public Map<ICheck, Double> getCheckVlMap() {
        return this.checkVlMap;
    }

    public Set<UUID> getPlayersWatching() {
        return this.playersWatching;
    }

    public Set<String> getFilteredPhrases() {
        return this.filteredPhrases;
    }

    public Set<CustomLocation> getTeleportLocations() {
        return this.teleportLocations;
    }

    public StringBuilder getSniffedPacketBuilder() {
        return this.sniffedPacketBuilder;
    }

    public void setSniffedPacketBuilder(final StringBuilder sniffedPacketBuilder) {
        this.sniffedPacketBuilder = sniffedPacketBuilder;
    }

    public CustomLocation getLastMovePacket() {
        return this.lastMovePacket;
    }

    public void setLastMovePacket(final CustomLocation lastMovePacket) {
        this.lastMovePacket = lastMovePacket;
    }

    public ClientType getClient() {
        return this.client;
    }

    public void setClient(final ClientType client) {
        this.client = client;
    }

    public boolean isAllowTeleport() {
        return this.allowTeleport;
    }

    public void setAllowTeleport(final boolean allowTeleport) {
        this.allowTeleport = allowTeleport;
    }

    public boolean isInventoryOpen() {
        return this.inventoryOpen;
    }

    public void setInventoryOpen(final boolean inventoryOpen) {
        this.inventoryOpen = inventoryOpen;
    }

    public boolean isSendingVape() {
        return this.sendingVape;
    }

    public void setSendingVape(final boolean sendingVape) {
        this.sendingVape = sendingVape;
    }

    public boolean isAttackedSinceVelocity() {
        return this.attackedSinceVelocity;
    }

    public void setAttackedSinceVelocity(final boolean attackedSinceVelocity) {
        this.attackedSinceVelocity = attackedSinceVelocity;
    }

    public boolean isUnderBlock() {
        return this.underBlock;
    }

    public void setUnderBlock(final boolean underBlock) {
        this.underBlock = underBlock;
    }

    public boolean isSprinting() {
        return this.sprinting;
    }

    public void setSprinting(final boolean sprinting) {
        this.sprinting = sprinting;
    }

    public boolean isInLiquid() {
        return this.inLiquid;
    }

    public void setInLiquid(final boolean inLiquid) {
        this.inLiquid = inLiquid;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(final boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isSniffing() {
        return this.sniffing;
    }

    public void setSniffing(final boolean sniffing) {
        this.sniffing = sniffing;
    }

    public boolean isOnStairs() {
        return this.onStairs;
    }

    public void setOnStairs(final boolean onStairs) {
        this.onStairs = onStairs;
    }

    public boolean isBanWave() {
        return this.banWave;
    }

    public void setBanWave(final boolean banWave) {
        this.banWave = banWave;
    }

    public boolean isPlacing() {
        return this.placing;
    }

    public void setPlacing(final boolean placing) {
        this.placing = placing;
    }

    public boolean isBanning() {
        return this.banning;
    }

    public void setBanning(final boolean banning) {
        this.banning = banning;
    }

    public boolean isDigging() {
        return this.digging;
    }

    public void setDigging(final boolean digging) {
        this.digging = digging;
    }

    public boolean isInWeb() {
        return this.inWeb;
    }

    public void setInWeb(final boolean inWeb) {
        this.inWeb = inWeb;
    }

    public boolean isOnIce() {
        return this.onIce;
    }

    public void setOnIce(final boolean onIce) {
        this.onIce = onIce;
    }

    public double getLastGroundY() {
        return this.lastGroundY;
    }

    public void setLastGroundY(final double lastGroundY) {
        this.lastGroundY = lastGroundY;
    }

    public double getVelocityX() {
        return this.velocityX;
    }

    public void setVelocityX(final double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return this.velocityY;
    }

    public void setVelocityY(final double velocityY) {
        this.velocityY = velocityY;
    }

    public double getVelocityZ() {
        return this.velocityZ;
    }

    public void setVelocityZ(final double velocityZ) {
        this.velocityZ = velocityZ;
    }

    public long getLastDelayedMovePacket() {
        return this.lastDelayedMovePacket;
    }

    public void setLastDelayedMovePacket(final long lastDelayedMovePacket) {
        this.lastDelayedMovePacket = lastDelayedMovePacket;
    }

    public long getLastAttackPacket() {
        return this.lastAttackPacket;
    }

    public void setLastAttackPacket(final long lastAttackPacket) {
        this.lastAttackPacket = lastAttackPacket;
    }

    public long getLastTeleportTime() {
        return this.lastTeleportTime;
    }

    public void setLastTeleportTime(final long lastTeleportTime) {
        this.lastTeleportTime = lastTeleportTime;
    }

    public long getLastVelocity() {
        return this.lastVelocity;
    }

    public void setLastVelocity(final long lastVelocity) {
        this.lastVelocity = lastVelocity;
    }

    public long getPing() {
        return this.ping;
    }

    public void setPing(final long ping) {
        this.ping = ping;
    }

    public int getVelocityH() {
        return this.velocityH;
    }

    public void setVelocityH(final int velocityH) {
        this.velocityH = velocityH;
    }

    public int getVelocityV() {
        return this.velocityV;
    }

    public void setVelocityV(final int velocityV) {
        this.velocityV = velocityV;
    }

    public int getCps() {
        return this.cps;
    }

    public void setCps(final int cps) {
        this.cps = cps;
    }
}