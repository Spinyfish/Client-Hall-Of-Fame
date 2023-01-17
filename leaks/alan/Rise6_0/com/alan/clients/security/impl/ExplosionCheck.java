package com.alan.clients.security.impl;

import com.alan.clients.security.SecurityFeature;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S27PacketExplosion;

public final class ExplosionCheck extends SecurityFeature {

    public ExplosionCheck() {
        super("Explosion Checker", "Server attempted to crash the client with a large explosion");
    }

    @Override
    public boolean handle(final Packet<?> packet) {
        if (packet instanceof S27PacketExplosion) {
            final S27PacketExplosion wrapper = ((S27PacketExplosion) packet);

            return wrapper.func_149149_c() >= Byte.MAX_VALUE
                    || wrapper.func_149144_d() >= Byte.MAX_VALUE
                    || wrapper.func_149147_e() >= Byte.MAX_VALUE;
        }

        return false;
    }
}
