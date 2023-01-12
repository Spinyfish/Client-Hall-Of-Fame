package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class VulcanMode extends Mode {

  private double speed;

  public VulcanMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Override
  public void onEnable() {
    speed = 0.2;
  }

  @Subscribe
  public void onMove(EventMove eventMove) {
    if(MovementUtil.isMoving()) {
      speed -= speed / 50;
      if(mc.thePlayer.onGround) {
        mc.thePlayer.jump();
        speed = MovementUtil.getUpdatedNCPBaseSpeed() * 1.3;
      } else if (mc.thePlayer.motionY > 0 && mc.thePlayer.motionY < 0.1) {
        mc.thePlayer.motionY = -.37;
      }
      MovementUtil.setMotion(eventMove, mc.thePlayer.onGround ? speed * 1.7 : speed);
    }
  }

  @Subscribe
  public void onUpdate(EventUpdate eventUpdate) {

  }

}