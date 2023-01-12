package dev.eternal.client.module.impl.combat;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventRender3D;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.module.impl.movement.Flight;
import dev.eternal.client.module.impl.movement.Speed;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.util.combat.CombatUtil;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.movement.data.Rotation;
import dev.eternal.client.util.render.RenderUtil;
import lombok.Getter;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.PartialDisk;

import java.util.Comparator;

@Getter
@ModuleInfo(name = "TargetStrafe", description = "Strafes around KillAura's current target.", category = Module.Category.MOVEMENT, defaultKey = Keyboard.KEY_X)
public class TargetStrafe extends Module {

  private EntityLivingBase target;
  private final NumberSetting distance = new NumberSetting(this, "Distance", "The distance to circle at.", 3, 1, 6, 0.1);
  private float currentStrafeInput = 1;
  private Speed speed;
  private Flight flight;

  @Override
  public void init() {
    speed = Client.singleton().moduleManager().getByClass(Speed.class);
    flight = Client.singleton().moduleManager().getByClass(Flight.class);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    final EntityPlayerSP localPlayer = mc.thePlayer;
    if (target == null || !isValid(target)) {
      target = fromWorld();
      return;
    }
    final Rotation rotation = CombatUtil.getAngleForEntityRandomized(target);
    final float distance = target.getDistanceToEntity(localPlayer);
    final float currentForwardInput = distance >= this.distance.value() ? 1F : 0F;
    if (mc.thePlayer.isCollidedHorizontally) {
      currentStrafeInput = -currentStrafeInput;
    }
    if (mc.thePlayer.movementInput.moveStrafe != 0) {
      currentStrafeInput = mc.thePlayer.movementInput.moveStrafe;
    }

    if(canStrafe()) {
      eventMove.forward(currentForwardInput);
      eventMove.strafe(currentStrafeInput);
      eventMove.yaw(rotation.rotationYaw());
    }
  }

  @Subscribe
  public void onRender(EventRender3D eventRender3D) {
    if(target != null) {
      float x = (float) ((float) (target.lastTickPosX
          + (target.posX - target.lastTickPosX) * eventRender3D.partialTicks())
          - RenderManager.renderPosX);
      float y = (float) ((float) ((float) (target.lastTickPosY
          + (target.posY - target.lastTickPosY) * eventRender3D.partialTicks())
          - RenderManager.renderPosY));
      float z = (float) ((float) (target.lastTickPosZ
          + (target.posZ - target.lastTickPosZ) * eventRender3D.partialTicks())
          - RenderManager.renderPosZ);

      RenderUtil.pre3D();
      GL11.glLineWidth(1f);
      GL11.glTranslated(x, y, z);
      PartialDisk partialDisk = new PartialDisk();
      GL11.glColor4f(0, 0, 0, 1.0F);
      GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);

      partialDisk.setDrawStyle(100011);
      partialDisk.draw(distance.value().floatValue() - 0.03f, distance.value().floatValue() + 0.03f, 8, 1, 0f, 360f);
      if (canStrafe()) GL11.glColor4f(0.15F, 0.25F, 0.5F, 1.0F);
      else GL11.glColor4f(1F, 1F, 1F, 1.0F);
      partialDisk.draw(distance.value().floatValue() - 0.02f, distance.value().floatValue() + 0.02f, 8, 1, 0f, 360f);

      GL11.glColor4f(0, 0, 0, 1.0F);

      partialDisk.setDrawStyle(100012);
      partialDisk.draw(distance.value().floatValue() - 0.03f, distance.value().floatValue() + 0.03f, 8, 1, 0f, 360f);
      if (canStrafe()) GL11.glColor4f(0.15F, 0.25F, 0.5F, 1.0F);
      else GL11.glColor4f(1F, 1F, 1F, 1.0F);
      partialDisk.draw(distance.value().floatValue() - 0.02f, distance.value().floatValue() + 0.02f, 8, 1, 0f, 360f);


      RenderUtil.post3D();
    }
  }

  private EntityLivingBase fromWorld() {
    return (EntityLivingBase) mc.theWorld.loadedEntityList.stream()
        .filter(EntityPlayer.class::isInstance)
        .filter(this::isValid)
        .max(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity)).orElse(null);
  }

  private boolean isValid(Entity entity) {
    return mc.theWorld.loadedEntityList.contains(entity) &&
        entity != mc.thePlayer &&
        mc.thePlayer.getDistanceToEntity(entity) < distance.value() + 2;
  }

  private boolean canStrafe() {
    return MovementUtil.isMoving() && (speed.enabled() || flight.enabled());
  }

}
