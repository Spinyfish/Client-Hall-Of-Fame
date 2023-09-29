package dev.eternal.client.module.impl.movement.flight;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.impl.movement.Flight;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.Property;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.movement.data.Position;

public class CollideMode extends Mode {

  private final BooleanSetting packetSpeed = new BooleanSetting(this, "Packet Speed", false);
  private final NumberSetting incrementPerPacket = new NumberSetting(this, "Increment Per Packet", 0, 0, 0.45, 0.05);
  private final NumberSetting packets = new NumberSetting(this, "Packets", 1, 1, 10, 1);

  public CollideMode(IToggleable toggleable, String name) {
    super(toggleable, name);
    Property<?>[] propertyArray = new Property<?>[]{
        this.incrementPerPacket, this.packets
    };
    for (Property<?> property : propertyArray) {
      property.visible(packetSpeed::value);
    }
  }

  @Subscribe
  public void handleMovement(EventMove eventMove) {
  }


  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    MovementUtil.setMotion(packetSpeed.value() ? 0 : this.<Flight>getOwner().speedSetting().value());
    if (eventUpdate.pre()) {
      Position position = eventUpdate.position();
      eventUpdate.groundState(true);
      double oldPosY = position.posY();
      double newPosY = oldPosY - oldPosY % 0.0625D;
      position.posY(newPosY);
      mc.thePlayer.setPosition(mc.thePlayer.posX, newPosY, mc.thePlayer.posZ);
      mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 1.0F : mc.thePlayer.movementInput.sneak ? -1.0F : 0;
    } else if (packetSpeed.value()) {
      MovementUtil.setMotionPacket(MovementUtil.getBaseGroundSpeed(), packets.value().intValue(),
          incrementPerPacket.value().intValue()
      );
    }
  }

}
