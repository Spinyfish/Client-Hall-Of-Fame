package org.crystalpvp.anticheat.data.version;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@AllArgsConstructor
@Getter
public enum EnumClientType implements ClientType {

    VANILLA(false, "Vanilla"),
    COSMIC_CLIENT(false, "Cosmic Client"),
    CHEAT_BREAKER(false, "CheatBreaker"),
    LUNAR_CLIENT(false, "Lunar Client"),
    FORGE(true, "Forge"),
    OCMC(false, "OCMC Client"),
    LABYMOD(false, "LabyMod"),

    HACKED_CLIENT(true, "Hacked-Client");
    
    private boolean hacked;
    private String name;

}
