package crispy.util.render.particle;

import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.Random;

@Getter
public class ParticleUtil {


    private final ArrayList<Particle> particles = new ArrayList<>();

    private int count;
    private final int width;
    private final int height;

    public ParticleUtil(int particleCount, int width, int height) {
        this.width = width;
        this.height = height;
        this.count = particleCount;
        this.count = 200;
        for (int count = 0; count <= this.count; ++count) {
            this.particles.add(new Particle(new Random().nextInt(ScaledResolution.getScaledWidth()), new Random().nextInt(height)));
        }

    }

    public void renderParticles() {
        this.particles.forEach(Particle::drawParticle);
    }


}
