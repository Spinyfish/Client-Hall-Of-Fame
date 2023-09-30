package com.alan.clients.component.impl.render;

import com.alan.clients.api.Rise;
import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.render.particle.Particle;

import java.util.concurrent.ConcurrentLinkedQueue;

@Rise
public class ParticleComponent extends Component {

    public static ConcurrentLinkedQueue<Particle> particles = new ConcurrentLinkedQueue<>();
    public static int rendered;

    @EventLink(value = Priorities.VERY_HIGH)
    public final Listener<Render2DEvent> onRender2DEvent = event -> {
        if (particles.isEmpty()) return;
        NORMAL_POST_RENDER_RUNNABLES.add(ParticleComponent::render);
    };

    public static void render() {
        if (mc.ingameGUI.frame != rendered) {
            particles.forEach(particle -> {
                particle.render();

                if (particle.time.getElapsedTime() > 50 * 3 * 20) {
                    particles.remove(particle);
                }
            });

            threadPool.execute(() -> {
                particles.forEach(Particle::update);
            });

            rendered = mc.ingameGUI.frame;
        }
    }

    public static void add(final Particle particle) {
        particles.add(particle);
    }
}
