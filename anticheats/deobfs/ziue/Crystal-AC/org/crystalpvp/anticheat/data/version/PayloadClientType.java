package org.crystalpvp.anticheat.data.version;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@AllArgsConstructor
@Getter
public class PayloadClientType implements ClientType {

    private final String name;
    private final String payload;
    private final boolean hacked;

}
