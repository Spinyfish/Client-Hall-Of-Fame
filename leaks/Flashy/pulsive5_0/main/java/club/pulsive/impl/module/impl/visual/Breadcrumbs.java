package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.event.player.WorldLoadEvent;
import club.pulsive.impl.event.render.Render3DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.math.EvictingList;
import club.pulsive.impl.util.render.Particle;
import club.pulsive.impl.util.render.RenderUtil;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Breadcrumbs", description = "Breadcrumbs", category = Category.VISUALS)
public class Breadcrumbs extends Module {
    private final Property<Boolean> timeout = new Property<>("Timeout", false);
    private final DoubleProperty maxParticles = new DoubleProperty("Max Particles", 50, 10, 1000, 1, timeout::getValue);
    private final List<Vec3> path = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();
        path.clear();
    }
    
    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX || mc.thePlayer.lastTickPosY != mc.thePlayer.posY || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ) {
            path.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
        }
        
        if(timeout.getValue()) {
            while (path.size() > maxParticles.getValue()) {
                path.remove(0);
            }
        }
    };
    
    @EventHandler
    private final Listener<Render3DEvent> render3DEventListener = event -> {
      if(path.isEmpty()) return;
        RenderUtil.renderBreadCrumbs(path);
    };
    
    @EventHandler
    private final Listener<WorldLoadEvent> worldLoadEventListener = event -> {
        path.clear();
    };
}
