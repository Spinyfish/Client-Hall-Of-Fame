package org.crystalpvp.anticheat.check.misc.velocity;


import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.BlockUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class VelocityE extends PositionCheck {

    private double lastY;

    public VelocityE(PlayerData playerData) {
        super(playerData, "Velocity/AntiKB (E)");
    }

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("VelocityE")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        final double offsetX = update.getTo().getX() - update.getFrom().getX();
        final double offsetZ = update.getTo().getZ() - update.getFrom().getZ();
        final double offsetY = update.getTo().getY() - update.getFrom().getY();

        if (lastY == 0.0 && playerData.isWasOnGround() && !playerData.isUnderBlock()
                && !playerData.isWasUnderBlock() && !playerData.isInLiquid()
                && !playerData.isWasInLiquid() && !playerData.isInWeb()
                && !playerData.isWasInWeb() && !playerData.isOnStairs()
                && offsetX == 0.0
                && offsetZ == 0.0
                && offsetY == 0.0
                && update.getFrom().getY() % 1.0 == 0.0
                &&!BlockUtil.isBlockingVelocityH(update.getPlayer().getLocation())) {


            if (++vl >= 5) {
                alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);
            }

            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("VelocityE") && vl >= ConfigurationManager.banVL("VelocityE")) {
                punish(player);
            }


        } else {
            vl -= 0.2;
        }

        lastY = update.getFrom().getY() - update.getTo().getY();
        playerData.setCheckVl(vl, this);
    }
}
