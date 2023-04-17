package club.mineman.antigamingchair.check.impl.velocity;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PositionCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.PlayerUpdatePositionEvent;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import org.bukkit.entity.Player;

public class VelocityC extends PositionCheck {
    public VelocityC(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final PlayerUpdatePositionEvent event) {
        final double offsetY = event.getTo().getY() - event.getFrom().getY();
        if (this.playerData.getVelocityY() > 0.0 && this.playerData.isOnGround() && event.getFrom().getY() % 1.0 == 0.0 && !this.playerData.isUnderBlock() && !this.playerData.isInLiquid() && offsetY > 0.0 && offsetY < 0.41999998688697815) {
            final long time = System.currentTimeMillis() - this.playerData.getLastVelocity();
            final long difference = this.playerData.getPing() - time;
            int vl = (int) this.playerData.getCheckVl(this);
            if (difference > 10L) {
                if (++vl >= 5) {
                    this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "failed Velocity Check C (Experimental). D " + difference + ". VL " + vl + ".");
                }
            } else {
                vl = 0;
            }
            this.playerData.setCheckVl(vl, this);
        }
    }
}
