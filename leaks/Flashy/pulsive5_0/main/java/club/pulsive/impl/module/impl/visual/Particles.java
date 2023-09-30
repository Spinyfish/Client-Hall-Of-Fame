package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.player.AttackEvent;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.event.render.Render3DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.combat.Aura;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.render.Particle;
import club.pulsive.impl.util.render.RenderUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Particles", renderName = "Particles", category = Category.VISUALS)
public class Particles extends Module {
    private final DoubleProperty amount = new DoubleProperty("Amount", 10, 1, 20, 1);
    private final Property<Boolean> physics = new Property<Boolean>("Physics", true);
    private final List<Particle> particles = new ArrayList<>();
    private final TimerUtil timer = new TimerUtil();
    private EntityLivingBase target;
    
    @EventHandler
    private final Listener<AttackEvent> attackEventListener = event -> {
        if (event.getTarget() instanceof EntityLivingBase)
            target = (EntityLivingBase) event.getTarget();  
    };
    
    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
      if(event.isPre()){
          target = Pulsive.INSTANCE.getModuleManager().getModule(Aura.class).getTarget();
          if (target != null && target.hurtTime >= 9 && mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) < 10) {
              //Logger.print("a");
              for (int i = 0; i < amount.getValue() * 2; i++)
                  particles.add(new Particle(new Vec3(target.posX + (ApacheMath.random() - 0.5) * 0.5, target.posY + ApacheMath.random() * 1 + 0.5, target.posZ + (ApacheMath.random() - 0.5) * 0.5)));
              target = null;
          }
      }
    };
    
    @EventHandler
    private final Listener<Render3DEvent> render3DEventListener = event -> {
        if (particles.isEmpty())
            return;

        for (int i = 0; i <= timer.elapsed() / 1E+11; i++) {
            if (physics.getValue())
                particles.forEach(Particle::update);
            else
                particles.forEach(Particle::updateWithoutPhysics);
        }

        particles.removeIf(particle -> mc.thePlayer.getDistanceSq(particle.getPosition().xCoord, particle.getPosition().yCoord, particle.getPosition().zCoord) > 50 * 10);

        timer.reset();

        RenderUtil.renderParticles(particles);  
    };
    
}
