package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.event.player.WorldLoadEvent;
import club.pulsive.impl.event.render.Render3DEvent;
import club.pulsive.impl.event.render.Render3DEvent2;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.combat.Aura;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.math.EvictingList;
import club.pulsive.impl.util.math.MathUtil;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.PlayerUtil;
import club.pulsive.impl.util.render.Particle;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.world.WorldUtil;
import com.google.common.collect.Lists;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "CumESP", renderName = "Cum ESP", category = Category.VISUALS)
public class CumESP extends Module {
    private final DoubleProperty maxDistance = new DoubleProperty("Max Distance", 40, 10, 100, 1);
    private final Property<Boolean> randomize = new Property<>("Randomize", false);
    EntityLivingBase target;
    private final List<Particle> particles = new EvictingList<>(2000);
    private final TimerUtil timerUtil = new TimerUtil();

    private final static class DistanceSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2));
        }
    }
    
    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
      if(event.isPre()) {
          List<EntityLivingBase> entities = WorldUtil.getEntities(maxDistance.getValue(), true, false, false, false, false);
          
          if(entities == null) return;
          entities.sort(new DistanceSorter());
          
          target = entities.get(0);
              final double x = target.posX - mc.thePlayer.posX;
              final double z = target.posZ - mc.thePlayer.posZ;


          float yaw = MathHelper.wrapAngleTo180_float((float) ApacheMath.toDegrees(ApacheMath.atan2(z, x)) - 90.0F);

          double angle = ApacheMath.toRadians(yaw);

          double distance = ApacheMath.min(mc.thePlayer.getDistanceToEntity(target), 5);

          particles.add(new Particle(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 0.4, mc.thePlayer.posZ),
                  new Vec3(-MathHelper.sin((float) angle) * 0.3 * distance + (ApacheMath.random() - 0.5) / 10, 0.3 + (ApacheMath.random() - 0.5) / 10 + ApacheMath.max(0, distance - 5) + (target.posY - mc.thePlayer.posY) / 7,
                          MathHelper.cos((float) angle) * 0.3 * distance + (ApacheMath.random() - 0.5) / 10)));
          }
    };
    
    @EventHandler
    private final Listener<Render3DEvent2> render3DEventListener = event -> {
      if(particles.isEmpty()) return;

      //particles.removeIf(particle -> mc.thePlayer.getDistance(particle.getPosition().xCoord, particle.position.yCoord, particle.position.zCoord) > maxDistance.getValue());
        //Logger.printSysLog("a");
      
      for(int i = 0; i <= timerUtil.elapsed() / 1.01; i++){
          particles.forEach(Particle::update);
      }
      
      timerUtil.reset();
      RenderUtil.renderParticles1(particles);
    };

    @Override
    public void onEnable() {
        super.onEnable();
        timerUtil.reset();
        particles.clear();
       // PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ, false));
       // mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ);
        target = null;
    }
    
    @EventHandler
    private final Listener<WorldLoadEvent> worldLoadEventListener = event -> {
        timerUtil.reset();
        particles.clear();
        target = null;
    };
}
