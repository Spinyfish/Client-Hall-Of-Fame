package club.mineman.antigamingchair.check.impl.fly;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PositionCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.PlayerUpdatePositionEvent;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import org.bukkit.entity.Player;

public class FlyB extends PositionCheck {
    public FlyB(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final PlayerUpdatePositionEvent event) {
        int vl = (int) this.playerData.getCheckVl(this);
        if (!player.getAllowFlight() && !player.isInsideVehicle() && !this.playerData.isInLiquid() && !this.playerData.isOnGround()) {
            final double offsetH = Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ());
            final double offsetY = event.getTo().getY() - event.getFrom().getY();
            if (offsetH > 0.0 && offsetY == 0.0) {
                if (++vl >= 10) {
                    this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, String.format("failed Fly Check B. H %.2f. VL %s.", offsetH, vl));
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
