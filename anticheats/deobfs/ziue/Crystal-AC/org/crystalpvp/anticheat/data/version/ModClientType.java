package org.crystalpvp.anticheat.data.version;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@AllArgsConstructor
@Getter
public class ModClientType implements ClientType {

    private final String name;
    private final String modId;
    private final String modVersion;
    
    @Override
    public boolean isHacked() {
        return true;
    }

}
