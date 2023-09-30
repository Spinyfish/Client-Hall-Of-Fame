/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.ui.particle;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.MathHelper;
import wtf.monsoon.impl.ui.particle.Particle;

public class ParticleSystem {
    private final List<Particle> particles = new ArrayList<Particle>();

    public ParticleSystem() {
        for (int i = 0; i < 100; ++i) {
            this.particles.add(new Particle(this));
        }
    }

    public void render() {
        this.particles.forEach(Particle::render);
    }

    public Particle getNearest(Particle particle) {
        Particle nearest = null;
        float nearestDist = Float.MAX_VALUE;
        for (Particle particle1 : this.particles) {
            float f = particle.getX() - particle1.getX();
            float f1 = particle.getY() - particle1.getY();
            if (particle1 == particle || !(MathHelper.sqrt_float(f * f + f1 * f1) < nearestDist)) continue;
            nearest = particle1;
            nearestDist = MathHelper.sqrt_float(f * f + f1 * f1);
        }
        return nearest;
    }

    public List<Particle> getParticles() {
        return this.particles;
    }
}

