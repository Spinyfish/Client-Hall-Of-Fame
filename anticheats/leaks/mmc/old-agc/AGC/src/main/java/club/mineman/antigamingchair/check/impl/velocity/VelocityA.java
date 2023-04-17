package club.mineman.antigamingchair.check.impl.velocity;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PositionCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.PlayerUpdatePositionEvent;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import club.mineman.antigamingchair.util.MathUtil;
import org.bukkit.entity.Player;

public class VelocityA extends PositionCheck {
    public VelocityA(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final PlayerUpdatePositionEvent event) {
        int vl = (int) this.playerData.getCheckVl(this);
        if (this.playerData.getVelocityY() > 0.0 && !this.playerData.isUnderBlock() && !this.playerData.isInLiquid()) {
            final int threshold = 12 + MathUtil.pingFormula(this.playerData.getPing()) * 2;
            if (++vl >= threshold) {
                if (this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "failed Velocity Check A. VL " + vl + ".")) {
                    final int violations = this.playerData.getViolations(this, 60000L);
                    if (!this.playerData.isBanning() && violations > Math.max(this.playerData.getPing() / 10L, 15L)) {
                        this.ban(player, "Velocity Check A");
                    }
                }
                this.playerData.setVelocityY(0.0);
                vl = 0;
            }
        } else {
            vl = 0;
        }
        this.playerData.setCheckVl(vl, this);
    }
}
