package org.crystalpvp.anticheat.check.movement.speed;

import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.BukkitUtils;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class SpeedD extends PositionCheck {

    private int lastGroundTick;
    private int lastFastAirTick;
    private int lastAirTick;
    private int lastBlockAboveTick;
    private int lastBypassTick;
    private int bypassFallbackTicks;
    private int sprintTicks;

    public SpeedD(PlayerData playerData) {
        super(playerData, "Speed (D)");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("SpeedD")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if ((update.getTo().getX() != update.getFrom().getX() || update.getTo().getZ() != update.getFrom().getZ()) && !player.getAllowFlight()) {
            final double distance = MathUtil.hypot(update.getTo().getX() - update.getFrom().getX(), update.getTo().getZ() - update.getFrom().getZ());
            final AtomicDouble limit = new AtomicDouble(0.0);
            if(playerData.isOnGround()) {
                if (this.bypassFallbackTicks > 0) {
                    this.bypassFallbackTicks -= 10;
                }
                this.lastGroundTick = playerData.getTotalTicks();
                this.sprintTicks = ((!playerData.isSprinting()) ? (++this.sprintTicks) : 0);
                final double angle = Math.toDegrees(-Math.atan2(update.getTo().getX() - update.getFrom().getX(), update.getTo().getZ() - update.getFrom().getZ()));
                final double angleDiff = MathUtil.getDistanceBetweenAngles360(angle, update.getTo().getYaw());
                final boolean straightSprint = angleDiff < 5.0 && angleDiff < 90.0;
                limit.addAndGet(BukkitUtils.getPotionLevel(player, PotionEffectType.SPEED) * 0.0573);
                limit.addAndGet((this.sprintTicks <= 1) ? (straightSprint ? 0.281 : 0.2865) : (straightSprint ? 0.217 : 0.2325));
                if (player.getWalkSpeed() > 0.3) {
                    limit.set(limit.get() * player.getWalkSpeed());
                }
                if (this.lastAirTick >= this.lastGroundTick - 10) {
                    limit.addAndGet((this.lastGroundTick - this.lastAirTick) * 0.125);
                }
            } else {
                if (this.bypassFallbackTicks > 0) {
                    limit.addAndGet(2.1);
                    --this.bypassFallbackTicks;
                }
                this.lastAirTick = playerData.getTotalTicks();
                boolean fastAir = false;
                if (distance > 0.46 && this.lastFastAirTick < this.lastGroundTick) {
                    this.lastFastAirTick = playerData.getTotalTicks();
                    limit.addAndGet(0.6125);
                    fastAir = true;
                }
                else {
                    limit.addAndGet(0.56);
                }
                limit.addAndGet(BukkitUtils.getPotionLevel(player, PotionEffectType.SPEED) * (fastAir ? 0.0375 : 0.0175));
                if (player.getWalkSpeed() > 0.3) {
                    limit.addAndGet(limit.get() * (player.getWalkSpeed() - 0.2) * 2.0);
                }
            }

            if (distance > limit.get()) {
                alert(AlertType.RELEASE, player, String.format("D %.1f. VL %.1f/%s.", distance, ++vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("SpeedD") && vl >= ConfigurationManager.banVL("SpeedD")) {
                    punish(player);
                }
            }
        }
        playerData.setCheckVl(vl, this);
    }
}
