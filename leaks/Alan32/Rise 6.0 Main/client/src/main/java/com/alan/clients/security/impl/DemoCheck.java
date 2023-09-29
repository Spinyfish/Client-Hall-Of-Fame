package com.alan.clients.security.impl;

import com.alan.clients.security.SecurityFeature;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2BPacketChangeGameState;

public final class DemoCheck extends SecurityFeature {

    public DemoCheck() {
        super("Demo Check", "Server attempted to prevent gameplay with a demo screen");
    }

    @Override
    public boolean handle(final Packet<?> packet) {
        if (packet instanceof S2BPacketChangeGameState) {
            final S2BPacketChangeGameState wrapper = ((S2BPacketChangeGameState) packet);

            return wrapper.getGameState() == 5 && wrapper.func_149137_d() == 0;
        }

        return false;
    }
}
