package com.alan.clients.security.impl;

import com.alan.clients.security.SecurityFeature;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2APacketParticles;

public final class ParticleCheck extends SecurityFeature {

    private int particles;

    public ParticleCheck() {
        super("ParticleCheck", "Server attempted to crash the client with a large amount of particles");
    }

    @Override
    public boolean handle(final Packet<?> packet) {
        if (packet instanceof S2APacketParticles) {
            final S2APacketParticles wrapper = ((S2APacketParticles) packet);

            particles += wrapper.getParticleCount();
            particles -= 6;
            particles = Math.min(particles, 150);

            return particles > 100 || wrapper.getParticleCount() < 1 || Math.abs(wrapper.getParticleCount()) > 20 ||
                    wrapper.getParticleSpeed() < 0 || wrapper.getParticleSpeed() > 1000;
        }

        return false;
    }
}
