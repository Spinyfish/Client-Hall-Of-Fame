package dev.eternal.client.module.impl.movement.longjump;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.event.events.EventTeleport;
import dev.eternal.client.module.impl.movement.LongJump;
import dev.eternal.client.module.impl.movement.Speed;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.movement.data.Position;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class AACMode extends Mode {

  public AACMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    if (Client.singleton().moduleManager().getByClass(Speed.class).isEnabled())
      return;
    // aac movement check strictness is also based off timer speed
    if (MovementUtil.isMovingOnGround()) {
      mc.thePlayer.motionY = 0.7;
    } else {
      MovementUtil.setMotion(eventMove, 1.3);
    }
  }

  @Subscribe
  public void handleTeleport(EventTeleport eventTeleport) {

  }

  @Subscribe
  public void handlePacket(EventPacket eventPacket) {
    if (eventPacket.getPacket() instanceof S12PacketEntityVelocity packetEntityVelocity) {
      if (packetEntityVelocity.getEntityID() == mc.thePlayer.getEntityId()) {
        // MAKE SURE VELOCITY IS CANCELLED AS IT ABUSES A BUG IN AAC V4+
        eventPacket.cancelled(true);
      }
    }
  }
}
