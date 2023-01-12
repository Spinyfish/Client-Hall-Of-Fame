package dev.eternal.client.module.impl.movement.flight;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.module.impl.movement.Flight;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;

public class VanillaMode extends Mode {

  private final BooleanSetting antiKick = new BooleanSetting(this, "Anti Kick", true);

  public VanillaMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    Flight flight = this.getOwner();
    NumberSetting speed = flight.speedSetting();
    EntityPlayerSP playerInstance = mc.thePlayer;
    MovementInput movementInput = playerInstance.movementInput;
    playerInstance.motionY = movementInput.jump ? speed.value() : movementInput.sneak ? -speed.value() : 0;
    MovementUtil.setMotion(eventMove, speed.value());
  }

  @Override
  public void onDisable() {
    mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
  }

}
