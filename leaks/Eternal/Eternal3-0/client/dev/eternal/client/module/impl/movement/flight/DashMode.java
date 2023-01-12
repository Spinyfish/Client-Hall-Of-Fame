package dev.eternal.client.module.impl.movement.flight;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class DashMode extends Mode {
  public DashMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void onUpdate(EventMove eventMove) {
    MovementUtil.setMotion(0);
    eventMove.friction(0);
    mc.thePlayer.motionY = 0;
    MovementUtil.sendPacketForward(2, -1);
  }

}
