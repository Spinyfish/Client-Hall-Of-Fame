package org.crystalpvp.anticheat.api.checks;

import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public interface ICheck<T> {

    void handleCheck(Player player, T type);
    
    Class<? extends T> getType();

}
