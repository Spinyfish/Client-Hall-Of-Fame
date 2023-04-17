package club.mineman.antigamingchair.check.impl.fly;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PositionCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.PlayerUpdatePositionEvent;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlyC extends PositionCheck {
    private int illegalMovements;
    private int legalMovements;

    public FlyC(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final PlayerUpdatePositionEvent event) {
        if (!player.getAllowFlight() && !player.isInsideVehicle() && this.playerData.getVelocityH() == 0) {
            final double offsetH = Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ());
            int speed = 0;
            for (final PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED)) {
                    speed = effect.getAmplifier() + 1;
                    break;
                }
            }
            double limit;
            if (this.playerData.isOnGround()) {
                limit = 0.34;
                if (this.playerData.isOnStairs()) {
                    limit = 0.45;
                } else if (this.playerData.isOnIce()) {
                    if (this.playerData.isUnderBlock()) {
                        limit = 1.3;
                    } else {
                        limit = 0.65;
                    }
                } else if (this.playerData.isUnderBlock()) {
                    limit = 0.7;
                }
                limit += ((player.getWalkSpeed() > 0.2f) ? (player.getWalkSpeed() * 10.0f * 0.33f) : 0.0f);
                limit += 0.06 * speed;
            } else {
                limit = 0.36;
                if (this.playerData.isOnStairs()) {
                    limit = 0.45;
                } else if (this.playerData.isOnIce()) {
                    if (this.playerData.isUnderBlock()) {
                        limit = 1.3;
                    } else {
                        limit = 0.65;
                    }
                } else if (this.playerData.isUnderBlock()) {
                    limit = 0.7;
                }
                limit += ((player.getWalkSpeed() > 0.2f) ? (player.getWalkSpeed() * 10.0f * 0.33f) : 0.0f);
                limit += 0.02 * speed;
            }
            if (offsetH > limit) {
                ++this.illegalMovements;
            } else {
                ++this.legalMovements;
            }
            final int total = this.illegalMovements + this.legalMovements;
            if (total >= 20) {
                final double percentage = this.illegalMovements / 20.0 * 100.0;
                if (percentage >= 45.0 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, String.format("failed Fly Check C. P %.1f.", percentage))) {
                    final int violations = this.playerData.getViolations(this, 30000L);
                    if (!this.playerData.isBanning() && violations > 5) {
                        this.ban(player, "Fly Check C");
                    }
                }
                this.illegalMovements = 0;
                this.legalMovements = 0;
            }
        }
    }
}
