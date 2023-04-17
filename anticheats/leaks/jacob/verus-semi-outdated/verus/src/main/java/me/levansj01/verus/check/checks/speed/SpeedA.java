package me.levansj01.verus.check.checks.speed;

import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.data.transaction.velocity.Velocity;
import me.levansj01.verus.type.VerusTypeLoader;
import me.levansj01.verus.util.BukkitUtil;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.java.DoubleWrapper;
import me.levansj01.verus.util.java.MathUtil;
import me.levansj01.verus.util.location.PlayerLocation;
import org.bukkit.World;
import org.bukkit.inventory.PlayerInventory;

import java.util.concurrent.atomic.AtomicInteger;

@CheckInfo(
        type = CheckType.SPEED,
        subType = "A",
        friendlyName = "Speed",
        version = CheckVersion.RELEASE,
        minViolations = -5.0,
        maxViolations = 30,
        logData = true
)
public class SpeedA extends MovementCheck {

    private int bypassFallbackTicks;
    public static final double AIR_SLOWDOWN_TICK = 0.125;
    public static final double STRAIGHT_NON_SPRINT = 0.217;
    public static final double NON_SPRINT = 0.2325;
    private int lastGroundTick;
    public static final double NORMAL_AIR = 0.36;
    public static final double SPRINT = 0.2865;
    public static final double FAST_AIR = 0.6125;
    private int lastAirTick;
    public static final double SPEED_POTION_AIR = 0.0175;
    public static final double SPEED_POTION_GROUND = 0.0573;
    public static final double STRAIGHT_SPRINT = 0.281;
    private int lastSpeed;
    public static final double SPEED_CUBIC_AMOUNT = 0.0018;
    private int lastFastAirTick;
    private int lastBlockAboveTick = -20;
    private int lastBypassTick = -50;
    public static final double SPEED_POTION_AIR_FAST = 0.0375;

    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation2, long l) {
        if (!(playerLocation2.getX() == playerLocation.getX() && playerLocation2.getZ() == playerLocation.getZ()
                || this.playerData.isTeleportingV2() || this.playerData.isVehicle() || this.playerData.isGliding()
                || this.playerData.isFallFlying() || this.playerData.isHooked() || this.playerData.isRiptiding())) {
            if (this.playerData.canFly() || this.playerData.isFlying()) {
                if (this.playerData.isFlying()) {
                    this.bypassFallbackTicks = 20;
                }
                return;
            }
            double d = MathUtil.hypot(playerLocation2.getX() - playerLocation.getX(), playerLocation2.getZ() - playerLocation.getZ());
            DoubleWrapper doubleWrapper = new DoubleWrapper(0.0);
            int n = Math.max((Integer)this.playerData.getSpeedLevel().get(), 0);
            if (this.lastSpeed > n) {
                n = this.lastSpeed - 1;
            }
            if (playerLocation2.getGround()) {
                if (this.bypassFallbackTicks > 0) {
                    this.bypassFallbackTicks -= 10;
                }
                this.lastGroundTick = this.playerData.getTotalTicks();
                double d3 = Math.toDegrees(-Math.atan2(playerLocation2.getX() - playerLocation.getX(), playerLocation2.getZ() - playerLocation.getZ()));
                double d4 = MathUtil.getDistanceBetweenAngles360(d3, playerLocation2.getYaw());
                boolean bl = d4 < 5.0;
                double d2 = bl ? 0.281 : 0.2865;
                doubleWrapper.addAndGet((double)n * 0.0573);
                doubleWrapper.addAndGet(d2);
                if (this.playerData.getMovementSpeed() > 0.2) {
                    doubleWrapper.set(doubleWrapper.get() * this.playerData.getMovementSpeed() / 0.2);
                }
                if (this.lastAirTick >= this.lastGroundTick - 10) {
                    doubleWrapper.addAndGet((double)(this.lastGroundTick - this.lastAirTick) * 0.125);
                }
            } else {
                double d5;
                if (this.bypassFallbackTicks > 0) {
                    doubleWrapper.addAndGet(0.1);
                    --this.bypassFallbackTicks;
                }
                this.lastAirTick = this.playerData.getTotalTicks();
                boolean bl = false;
                if (d > 0.36 && this.lastFastAirTick < this.lastGroundTick) {
                    this.lastFastAirTick = this.playerData.getTotalTicks();
                    doubleWrapper.addAndGet(0.6125);
                    bl = true;
                } else {
                    doubleWrapper.addAndGet(0.36);
                }
                if (this.playerData.isFallFlying()) {
                    this.bypassFallbackTicks = 100;
                    doubleWrapper.set(doubleWrapper.get() * 5.0);
                }
                double d6 = n;
                if (this.lastAirTick - this.lastGroundTick < 1 + (n - 1) / 2) {
                    d6 += (double)(n * n * n) * 0.0018;
                }
                if (bl) {
                    d5 = 0.0375;
                } else {
                    d5 = 0.0175;
                }
                doubleWrapper.addAndGet(d6 * d5);
                if (this.playerData.getMovementSpeed() > 0.2) {
                    doubleWrapper.addAndGet(doubleWrapper.get() * (this.playerData.getMovementSpeed() - 0.2) * 2.0);
                }
            }
            this.lastSpeed = n;
            if (!VerusTypeLoader.isDev() && this.playerData.getHorizontalSpeedTicks() < (this.playerData.getPingTicks() + 10) * 2) {
                return;
            }
            boolean bl = this.playerData.hasLag();
            Velocity velocity = (Velocity) MathUtil.max(this.playerData.getVelocityQueue(), velocity1 -> ((Velocity)velocity1).getHypotSquaredXZ());
            if (velocity != null) {
                doubleWrapper.addAndGet(Math.sqrt(velocity.getHypotSquaredXZ()));
            }
            if (NMSManager.getInstance().getServerVersion().afterEq(ServerVersion.v1_13_R2) && BukkitUtil.hasEffect(this.playerData.getEffects(), 30)
                    || this.playerData.hadAttributes() || this.playerData.hadSpeedBoost()) {
                doubleWrapper.set(doubleWrapper.get() * 2.0);
            }
            if (d > doubleWrapper.get()) {
                World world = this.player.getWorld();
                PlayerLocation playerLocation3 = this.playerData.getLocation();
                Cuboid cuboid = new Cuboid(playerLocation3).move(0.0, 2.0, 0.0).expand(0.5, 0.5, 0.5);
                Cuboid cuboid2 = new Cuboid(playerLocation3).add(-0.5, 0.5, -1.99, 0.5, -0.5, 0.5);
                int n2 = this.playerData.getTotalTicks();
                AtomicInteger n3 = new AtomicInteger(this.lastFastAirTick);
                this.run(() -> {
                    boolean bl2;
                    boolean bl3;
                    int n6;
                    ServerVersion serverVersion = NMSManager.getInstance().getServerVersion();
                    if (serverVersion.after(ServerVersion.v1_7_R4)) {
                        PlayerInventory playerInventory = this.player.getInventory();
                        if (BukkitUtil.hasEnchantment(playerInventory.getBoots(), "DEPTH_STRIDER")
                                && !cuboid2.checkBlocks(this.player, world, material -> !MaterialList.LIQUIDS.contains(material))) {
                            return;
                        }
                        if (serverVersion.afterEq(ServerVersion.v1_16_R1) && (n6 = BukkitUtil.getEnchantment(playerInventory.getBoots(), "SOUL_SPEED")) > 0) {
                            doubleWrapper.addAndGet(doubleWrapper.get() * (0.9 + (double)n6 * 0.125));
                        }
                    }
                    bl3 = n2 - 20 < this.lastBlockAboveTick;
                    if (!(bl2 = bl3) && !cuboid.checkBlocks(this.player, world, material -> material == MaterialList.AIR)) {
                        bl2 = true;
                        this.lastBlockAboveTick = n2;
                    }
                    int n5 = n2 - 60 < this.lastBypassTick ? 1 : 0;
                    if ((n6 = n5) == 0 && !cuboid2.checkBlocks(this.player, world,
                            material -> !(MaterialList.ICE.contains(material) || MaterialList.SLABS.contains(material)
                                    || MaterialList.STAIRS.contains(material) || MaterialList.STICKY.contains(material)))) {
                        n6 = 1;
                        this.lastBypassTick = n2;
                    }
                    int n4 = bl ? 2 : n3.getAndSet(1);
                    if (bl2) {
                        doubleWrapper.addAndGet(0.3 * (double)n3.get());
                    }
                    if (n6 != 0) {
                        double d2;
                        if (playerLocation2.getGround()) {
                            d2 = 0.25;
                        } else {
                            d2 = 0.3;
                        }
                        doubleWrapper.addAndGet(d2 * (double)n3.get());
                        this.bypassFallbackTicks = 60;
                    }
                    if (d > doubleWrapper.get()) {
                        double d3;
                        if (bl) {
                            d3 = (d - doubleWrapper.get()) * 0.5 + 0.1;
                        } else {
                            d3 = d - doubleWrapper.get();
                        }
                        double d4 = d3 + 0.3;
                        if (this.playerData.isTeleporting()) {
                            d4 = 0.15;
                        }
                        this.handleViolation(() -> {
                            double d2;
                            if (velocity == null) {
                                d2 = 0.0;
                            } else {
                                d2 = Math.sqrt(velocity.getHypotSquaredXZ());
                            }
                            return String.format("DST: %.3f LM %.3f G: %s L: %s V: %s FA: %s",
                                    d, doubleWrapper.get(), playerLocation2.getGround(), bl, d2, n2 - n3.get());
                        }, d4);
                    } else {
                        this.decreaseVL(0.02);
                    }
                });
            } else {
                this.decreaseVL(0.02);
            }
        }
    }
}