package org.crystalpvp.anticheat.api.checks;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.event.player.PlayerAlertEvent;
import org.crystalpvp.anticheat.api.event.player.PlayerBanEvent;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.manager.PunishmentType;
import org.crystalpvp.anticheat.data.player.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.crystalpvp.anticheat.data.player.ReachData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@AllArgsConstructor
@Getter
public abstract class AbstractCheck<T> implements ICheck<T> {


    protected final PlayerData playerData;
    private final Class<T> clazz;
    private final String name;

    @Override
    public Class<? extends T> getType() {
        return this.clazz;
    }

    protected CrystalAC getPlugin() {
        return CrystalAC.getInstance();
    }

    protected double getVl() {
        return this.playerData.getCheckVl(this);
    }

    protected void setVl(final double vl) {
        this.playerData.setCheckVl(vl, this);
    }



    public boolean alert(AlertType alertType, Player player, String extra, boolean violation) {

        PlayerAlertEvent event = new PlayerAlertEvent(alertType, player, this.name, extra);

        this.getPlugin().getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (violation) {
                this.playerData.addViolation(this);
            }

            return true;
        }

        return false;
    }
    public boolean ban(final Player player) {

        this.playerData.setBanned(true);

        final PlayerBanEvent event = new PlayerBanEvent(player, this.name);

        this.getPlugin().getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    public boolean punish(final Player player) {
        final PunishmentType ptype = ConfigurationManager.getPunishmentType();
        if(ptype == PunishmentType.BAN){
            return this.ban(player);
        }
        else if(ptype == PunishmentType.KICK){
            return this.kick(player);
        }
        else{
            return true;
        }

    }



    protected boolean kick (final Player player) {

        final PlayerKickEvent event = new PlayerKickEvent(player, "", "");

        this.getPlugin().getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }
}
