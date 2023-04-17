package club.mineman.antigamingchair.check.impl.velocity;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PositionCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.PlayerUpdatePositionEvent;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class VelocityD extends PositionCheck {
    public VelocityD(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void handleCheck(final Player player, final PlayerUpdatePositionEvent event) {
        final double offsetY = event.getTo().getY() - event.getFrom().getY();
        final double offsetH = Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ());
        final double velocityH = Math.hypot(this.playerData.getVelocityX(), this.playerData.getVelocityZ());
        final EntityPlayer entityPlayer = ((CraftPlayer) event.getPlayer()).getHandle();
        if (this.playerData.isOnGround() && event.getFrom().getY() % 1.0 == 0.0 && !this.playerData.isUnderBlock() && !this.playerData.isInLiquid() && offsetY > 0.0 && offsetY < 0.41999998688697815 && velocityH > 0.45 && !entityPlayer.world.c(entityPlayer.getBoundingBox().grow(1.0, 0.0, 1.0))) {
            final double ratio = offsetH / velocityH;
            final double threshold = 0.62;
            double vl = this.playerData.getCheckVl(this);
            if (ratio < threshold) {
                if ((vl += 1.1) >= 8.0) {
                    this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, String.format("failed Velocity Check D (Experimental). P %s. VL %.2f.", Math.round(ratio * 100.0), vl));
                }
            } else {
                vl -= 0.4;
            }
            this.playerData.setCheckVl(vl, this);
        }
    }
}
