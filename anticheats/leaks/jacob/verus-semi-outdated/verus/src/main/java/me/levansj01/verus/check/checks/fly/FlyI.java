package me.levansj01.verus.check.checks.fly;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.type.VerusTypeLoader;
import me.levansj01.verus.util.BukkitUtil;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.java.MathUtil;
import me.levansj01.verus.util.location.PlayerLocation;
import me.levansj01.verus.verus2.data.player.TickerType;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        type = CheckType.FLY,
        subType = "I",
        friendlyName = "Fly",
        version = CheckVersion.RELEASE,
        minViolations = -2.5,
        maxViolations = 50,
        logData = true
)
public class FlyI extends MovementCheck {

    private int lastTPTick;
    private Double lastYDiff = null;
    private int lastBypassTick;
    private double threshold;

    @Override
    public void handle(PlayerLocation location, PlayerLocation location2, long l) {
        if (this.playerData.getTickerMap().get(TickerType.TELEPORT) <= 1) {
            this.lastYDiff = null;
            this.threshold = 0.0D;
            return;
        }
        if (!location.getGround() || !location2.getGround()) {
            double d1 = location2.getY() - location.getY();
            ClientVersion clientVersion = this.playerData.getVersion();
            double d2 = d1 - 0.41999998688697815D;
            if (this.lastYDiff != null && this.playerData.getTotalTicks() - 40 > this.lastBypassTick && !this.playerData.hasPlacedBucket()
                    && !this.playerData.hasPlacedBlock(true) && !this.playerData.isFallFlying() && !this.playerData.isGliding()
                    && !this.playerData.canFly() && this.playerData.isSpawned() && !this.playerData.isVehicle()
                    && this.playerData.getVelocityTicks() > (2 + this.playerData.getMaxPingTicks()) * 2 && !this.playerData.isLevitating()
                    && !this.playerData.isRiptiding() && !this.playerData.hadJumpBoost() && (NMSManager.getInstance().getServerVersion().before(ServerVersion.v1_13_R2)
                    || (location2.getY() < location.getY() && !BukkitUtil.hasEffect(this.playerData.getEffects(), 28)))
                    && Math.abs(d1 + 0.9800000190734863D) > 1.0E-11D && Math.abs(d1 + 0.09800000190735147D) > 1.0E-11D
                    && Math.abs(d1 - 0.0030162615090425504D) > 1.0E-9D && (!StorageEngine.getInstance().getVerusConfig().isUnloadedChunkFix()
                    || Math.abs(d1 / 0.9800000190734863D + 0.08D) > 1.0E-11D) && Math.abs(d2) > 9.999999960041972E-13D
                    && Math.abs(d2 - (Integer) this.playerData.getJumpLevel().get() * 0.1D) > 1.0000000116860974E-7D
                    && (clientVersion.before(ClientVersion.V1_9) || (Math.abs(d1 + 0.15233518685055714D) > 1.0E-11D
                    && Math.abs(d1 + 0.07242780368044421D) > 1.0E-11D)) && (clientVersion.before(ClientVersion.V1_13)
                    || Math.max(location2.getY(), location.getY()) < 255.0D)) {
                boolean b = location.getX() != location2.getX() && location.getZ() != location2.getZ();
                double d3 = (this.lastYDiff - 0.08D) * 0.9800000190734863D;
                if ((location2.getGround() && d1 < 0.0D && d3 < d1 && MathUtil.onGround(location2.getY()))
                        || (location.distanceXZSquared(location2) < 0.0025D
                        && BukkitUtil.hasEffect(this.playerData.getEffects(), PotionEffectType.JUMP))) {
                    d3 = d1;
                } else if ((VerusTypeLoader.isDev() || (this.playerData.getVersion() != ClientVersion.V1_9
                        && !BukkitUtil.hasEffect(this.playerData.getEffects(), PotionEffectType.JUMP))) && Math.abs(d3) < 0.005D) {
                    d3 = 0.0D;
                }
                double d4 = Math.abs(d3 - d1);
                double d5 = (d3 - d1) / d3;
                int i = this.playerData.getTotalTicks();
                if (d4 > 2.0D && Math.abs(d5) > 300.0D) {
                    if (i - this.lastTPTick <= 20 && i - this.lastTPTick > 1) {
                    } else {

                    }
                    handleViolation(String.format("%s %s %.3f, %.3f%s", this.playerData.getTeleportTicks(),
                            location.getGround(), d4, d5 * 100.0D, "%"), 0.5D);
                    this.lastTPTick = i;
                }
                if (!this.playerData.isTeleporting() && !location.getGround())
                    if (d4 > 1.0E-7D) {
                        World world = this.player.getWorld();
                        Cuboid cuboid1 = (new Cuboid(location2)).add(-0.5D, 0.5D, -1.0D, 1.5D, -0.5D, 0.5D);
                        if (VerusTypeLoader.isDev()) {
                        } else {
                        }
                        double d6 = 0.5D;
                        double d7 = this.playerData.getLocation().getY();
                        Cuboid cuboid2 = Cuboid.withLimit(location, location2, 16)
                                .move(0.0D, 2.0D, 0.0D).add(-d6, d6, -0.5D, 0.5D, -d6, d6);
                        run(() -> {
                            boolean bool = location.toBlock().isWaterLogged(world);
                            if (cuboid1.checkBlocks(this.player, world, material -> !MaterialList.BAD_VELOCITY.contains(material) && !MaterialList.INVALID_SHAPE.contains(material))
                                    && cuboid2.checkBlocks(this.player, world, material -> material == Material.AIR) && !bool) {
                                for (Entity entity : this.player.getNearbyEntities(2.5D, 2.5D, 2.5D)) {
                                    if (entity instanceof org.bukkit.entity.Boat || entity instanceof org.bukkit.entity.Minecart) {
                                        this.threshold = 0.0D;
                                        this.lastBypassTick = i - 100;
                                        return;
                                    }
                                }
                                this.threshold++;
                                if (!this.playerData.hasLag()) {
                                    this.playerData.hasFast();
                                }
                                handleViolation(() -> String.format("D: %s D2: %s P: %s V: %s", d4, d1, location2.getY() % 1.0D,
                                        b), this.threshold);
                            } else {
                                decreaseVL(0.1D);
                                this.lastBypassTick = i;
                                this.threshold = 0.0D;
                            }
                        });
                    } else {
                        decreaseVL(0.025D);
                        this.threshold = 0.0D;
                    }
            }
        }
        if (!location2.getGround() || !location.getGround()) {
            this.lastYDiff = location2.getY() - location.getY();
        } else {
            this.lastYDiff = null;
        }
    }
}
