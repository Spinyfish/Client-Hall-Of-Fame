package org.crystalpvp.anticheat.api.manager;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public enum PunishmentType {
    KICK,
    BAN,
    NONE;

    public static PunishmentType getPunishmenttype(String rankName) {
        PunishmentType punishmentType;

        try {
            punishmentType = PunishmentType.valueOf(rankName.toUpperCase());
        }
        catch (Exception e) {
            punishmentType = PunishmentType.NONE;
        }

        return punishmentType;
    }
}
