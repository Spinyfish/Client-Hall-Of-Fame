package dev.eternal.client.module.impl.combat;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.util.combat.CombatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Comparator;
import java.util.function.Predicate;

@ModuleInfo(name = "AimAssist", category = Module.Category.COMBAT)
public class AimAssist extends Module {
  private final NumberSetting reach = new NumberSetting(this, "Reach", "The minimum distance from the entity to start aiming towards them.", 3, 3, 6, 0.1);
  private final NumberSetting speed = new NumberSetting(this, "Speed", "How fast you aim.", 1, 0.1, 100, 0.1);

  //Aura Code (sorry guys)
  private final Predicate<Entity> entityPredicate = entity ->
      entity.isEntityAlive() &&
          mc.thePlayer.getDistanceToEntity(entity) < reach.value() &&
          entity instanceof EntityPlayer &&
          !entity.isInvisible() &&
          entity != mc.thePlayer;

  private Entity target;

  private void setTarget() {
    target = mc.theWorld.loadedEntityList.stream()
        .filter(entityPredicate)
        .min(Comparator.comparingDouble(value -> mc.thePlayer.getHealth() + mc.thePlayer.hurtTime + mc.thePlayer.getDistanceToEntity(value)))
        .orElse(null);
  }

  @Subscribe
  public void handleMovement(EventUpdate eventUpdate) {
    if (eventUpdate.pre()) {
      setTarget();
      if (target != null) {
        EntityLivingBase lbTarget = (EntityLivingBase) target;
        float[] predictedRotations = new float[]{CombatUtil.getAngleForEntityStatic(lbTarget).rotationYaw(), CombatUtil.getAngleForEntityStatic(lbTarget).rotationPitch()};
        predictedRotations[1] = CombatUtil.getAngleForEntityStatic(lbTarget).rotationPitch() - 5;
        double yawDiff = mc.thePlayer.rotationYaw - predictedRotations[0];
        double yawDiff2 = mc.thePlayer.rotationYaw - predictedRotations[0];
        double pitchDiff = mc.thePlayer.rotationPitch - predictedRotations[1];
        if (mc.thePlayer.rotationYaw > predictedRotations[0]) {
          if (Math.abs(yawDiff) > speed.value() / 15) {
            mc.thePlayer.rotationYaw -= speed.value() / 10;
          }
        } else if (mc.thePlayer.rotationYaw < predictedRotations[0]) {
          if (Math.abs(yawDiff) > speed.value() / 15) {
            mc.thePlayer.rotationYaw += speed.value() / 10;
          }
        }
        if (mc.thePlayer.rotationPitch > predictedRotations[1]) {
          if (Math.abs(pitchDiff) > speed.value() / 15) {
            mc.thePlayer.rotationPitch -= speed.value() / 25;
          }
        } else if (mc.thePlayer.rotationPitch < predictedRotations[1]) {
          if (Math.abs(pitchDiff) > speed.value() / 15) {
            mc.thePlayer.rotationPitch += speed.value() / 25;
          }
        }
      }
    }
  }

}
