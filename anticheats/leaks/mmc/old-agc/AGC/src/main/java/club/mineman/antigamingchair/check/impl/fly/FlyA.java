package club.mineman.antigamingchair.check.impl.fly;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PositionCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.PlayerUpdatePositionEvent;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlyA extends PositionCheck {
    public FlyA(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final PlayerUpdatePositionEvent event) {
        int vl = (int) this.playerData.getCheckVl(this);
        if (!player.getAllowFlight() && !player.isInsideVehicle() && !this.playerData.isInLiquid() && !this.playerData.isOnGround() && this.playerData.getVelocityV() == 0 && event.getTo().getY() > event.getFrom().getY()) {
            final double distance = event.getTo().getY() - this.playerData.getLastGroundY();
            double limit = 2.0;
            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                for (final PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType().equals(PotionEffectType.JUMP)) {
                        final int level = effect.getAmplifier() + 1;
                        limit += Math.pow((double) level + 4.2, 2.0) / 16.0;
                        break;
                    }
                }
            }
            if (distance > limit) {
                if (++vl >= 10 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "failed Fly Check A. VL " + vl + ".")) {
                    final int violations = this.playerData.getViolations(this, 60000L);
                    if (!this.playerData.isBanning() && violations > 8) {
                        this.ban(player, "Fly Check A");
                    }
                }
            } else {
                vl = 0;
            }
        } else {
            vl = 0;
        }
        this.playerData.setCheckVl(vl, this);
    }
}
