package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;

public class PacketMode extends Mode {

  private final NumberSetting incrementPerPacket = new NumberSetting(this, "Increment Per Packet", 0, 0, 0.45, 0.05);
  private final NumberSetting packets = new NumberSetting(this, "Packets", 2, 1, 10, 1);


  public PacketMode(IToggleable toggleable, String name) {
    super(toggleable, name);
  }

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if (eventUpdate.pre() || !MovementUtil.isMovingOnGround())
      return;
    if (mc.thePlayer.ticksExisted % 10 == 0) {
      MovementUtil.setMotionPacket(MovementUtil.getUpdatedNCPBaseSpeed(), this.packets.value().intValue(), this.incrementPerPacket.value());
    }
  }

}
