package dev.tenacity.ui.mainmenu.particles;

import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.utils.tuples.mutable.MutablePair;
import dev.tenacity.utils.Utils;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author cedo
 * @since 05/23/2022
 */
public class ParticleEngine implements Utils {

    private final List<ParticleImage> particleImages = new ArrayList<>();

    private MutablePair<Integer, Integer> particleTypes = MutablePair.of(0, 0);


    public ParticleEngine() {
        particleImages.add(new ParticleImage(1, Pair.of(297, 301)));
        particleImages.add(new ParticleImage(2, Pair.of(303, 310)));
        particleImages.add(new ParticleImage(3, Pair.of(748, 781)));
        particleImages.add(new ParticleImage(4, Pair.of(227, 283)));
        particleImages.add(new ParticleImage(5, Pair.of(251, 302)));
        particleImages.add(new ParticleImage(6, Pair.of(253, 228)));
        particleImages.add(new ParticleImage(7, Pair.of(419, 476)));
        particleImages.add(new ParticleImage(8, Pair.of(564, 626)));
    }

    private final List<Particle> particles = new ArrayList<>();

    private final List<Particle> toRemove = new ArrayList<>();


    public void render() {
        ScaledResolution sr = new ScaledResolution(mc);


        if (particles.size() < 6) {
            particles.add(new Particle(sr, getParticleImage()));
        }

        particles.sort(Comparator.<Particle>comparingDouble(p -> {
            ParticleImage pImg = p.getParticleImage();
            return pImg.getDimensions().getFirst() + pImg.getDimensions().getSecond();
        }).reversed());


        for (Particle particle : particles) {
            particle.setX(particle.getInitialX() - (particle.getTicks() * 20));
            particle.setY(particle.getInitialY() + ((particle.getTicks()) * ((particle.getTicks() * particle.getSpeed()) / 7)));

            particle.draw();

            float particleHeight = particle.getParticleImage().getDimensions().getSecond() / 2f;
            float particleWidth = particle.getParticleImage().getDimensions().getFirst() / 2f;

            if (particle.getX() + particleWidth < 0 || particle.getY() > sr.getScaledHeight() || particle.getX() > sr.getScaledWidth()) {
                toRemove.add(particle);

                if (particle.getParticleImage().getParticleType().equals(ParticleType.BIG)) {
                    particleTypes.computeSecond(s -> Math.max(0, s - 1));
                } else {
                    particleTypes.computeFirst(f -> Math.max(0, f - 1));
                }

            }
            particle.setTicks(particle.getTicks() + .03f);
        }

        if (toRemove.size() > 0) {

            particles.removeAll(toRemove);
            toRemove.clear();
        }

    }

    private ParticleImage getParticleImage() {
        ParticleType particleType = getParticleType();

        List<ParticleImage> particleList = particleImages.stream().filter(
                particleImg -> particleImg.getParticleType().equals(particleType)).collect(Collectors.toList());
        return particleList.get((int) (new Random().nextFloat() * (particleList.size() - 1)));

    }

    private static final int BIG_LIMIT = 2;
    private static final int SMALL_LIMIT = 4;

    private ParticleType getParticleType() {
        if (particleTypes.getFirst() == 0 && particleTypes.getSecond() == 0) {
            particleTypes.computeSecond(s -> s + 1);
            return ParticleType.BIG;
        } else if (particleTypes.getFirst() < SMALL_LIMIT) {
            particleTypes.computeFirst(f -> f + 1);
            return ParticleType.SMALL;
        } else if (particleTypes.getSecond() < BIG_LIMIT) {
            particleTypes.computeSecond(s -> s + 1);
            return ParticleType.BIG;
        } else {
            System.out.println(particleTypes);
            throw new RuntimeException("pranked gg.");
        }
    }


}
