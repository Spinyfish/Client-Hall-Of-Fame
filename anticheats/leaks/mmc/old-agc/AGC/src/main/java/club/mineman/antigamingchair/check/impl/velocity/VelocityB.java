package club.mineman.antigamingchair.check.impl.velocity;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PositionCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.PlayerUpdatePositionEvent;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import org.bukkit.entity.Player;

public class VelocityB extends PositionCheck {
    public VelocityB(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final PlayerUpdatePositionEvent event) {
        final double offsetY = event.getTo().getY() - event.getFrom().getY();
        if (this.playerData.getVelocityY() > 0.0 && this.playerData.isOnGround() && event.getFrom().getY() % 1.0 == 0.0 && !this.playerData.isUnderBlock() && !this.playerData.isInLiquid() && offsetY > 0.0 && offsetY < 0.41999998688697815) {
            final double ratioY = offsetY / this.playerData.getVelocityY();
            int vl = (int) this.playerData.getCheckVl(this);
            if (ratioY < 0.99) {
                final int percent = (int) Math.round(ratioY * 100.0);
                if (++vl >= 5 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "failed Velocity Check B. P " + percent + ". VL " + vl + ".") && vl >= 15) {
                    if (ratioY < 0.6 && !this.playerData.isBanning()) {
                        this.ban(player, "Velocity Check B");
                    } else if (ratioY > 0.6 && !this.playerData.isBanWave()) {
                        this.banWave(player, "Velocity Check B");
                    }
                    vl = 0;
                }
            } else {
                --vl;
            }
            this.playerData.setCheckVl(vl, this);
        }
    }
}
