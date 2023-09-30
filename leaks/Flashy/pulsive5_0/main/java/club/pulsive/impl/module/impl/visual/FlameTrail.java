package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.event.player.WorldLoadEvent;
import club.pulsive.impl.event.render.Render3DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.math.EvictingList;
import club.pulsive.impl.util.render.Particle;
import club.pulsive.impl.util.render.RenderUtil;
import net.minecraft.util.Vec3;

import java.util.List;

@ModuleInfo(name = "FlameTrail", description = "Flashy is gay", category = Category.VISUALS)
public class FlameTrail extends Module {
    private final List<Particle> flameParticleTrail = new EvictingList<>(1000);
    private final TimerUtil timerUtil = new TimerUtil();
    
    @Override
    public void onEnable() {
        super.onEnable();
        flameParticleTrail.clear();
        timerUtil.reset();
    }
    
    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX || mc.thePlayer.lastTickPosY != mc.thePlayer.posY || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ) {
            flameParticleTrail.add(new Particle(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)));
        }
        flameParticleTrail.removeIf(particle -> mc.thePlayer.getDistance(particle.getPosition().xCoord, particle.getPosition().yCoord, particle.getPosition().zCoord) >= 50);
    };
    
    @EventHandler
    private final Listener<Render3DEvent> render3DEventListener = event -> {
        if(flameParticleTrail.isEmpty()) return;
        
        for(int i = 0; i < timerUtil.elapsed() / 1e+11; i++) {
            flameParticleTrail.forEach(Particle::update);
        }
        
        timerUtil.reset();
        RenderUtil.renderParticles(flameParticleTrail);  
    };
    
    @EventHandler
    private final Listener<WorldLoadEvent> worldLoadEventListener = event -> {
        flameParticleTrail.clear();
        timerUtil.reset();
    };
}
