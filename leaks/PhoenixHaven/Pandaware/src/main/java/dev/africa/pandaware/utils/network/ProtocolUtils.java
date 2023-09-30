package dev.africa.pandaware.utils.network;

import dev.africa.pandaware.switcher.ViaMCP;
import dev.africa.pandaware.switcher.protocols.ProtocolCollection;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProtocolUtils {
    public boolean isOneDotEight() {
        return ViaMCP.getInstance().getVersion() == ProtocolCollection.R1_8.getVersion().getVersion();
    }

    public boolean isMoreOrEqual(ProtocolCollection protocol) {
        return ViaMCP.getInstance().getVersion() >= protocol.getVersion().getVersion();
    }
}