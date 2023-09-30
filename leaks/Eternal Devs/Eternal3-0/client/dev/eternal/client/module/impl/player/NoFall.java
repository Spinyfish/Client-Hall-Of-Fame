package dev.eternal.client.module.impl.player;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.movement.data.Position;
import dev.eternal.client.util.pathfinder.Node;
import dev.eternal.client.util.pathfinder.PathInfo;
import dev.eternal.client.util.pathfinder.Pather;
import dev.eternal.client.util.world.WorldUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ModuleInfo(name = "NoFall", description = "Eliminates or reduces fall damage", category = Module.Category.PLAYER)
public class NoFall extends Module {

  private final EnumSetting<Mode> modeSetting = new EnumSetting<>(this, "Mode", Mode.values());
  private final BooleanSetting roundSetting = new BooleanSetting(this, "Round Position", false);

  @Override
  protected void onEnable() {
  }

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if (!eventUpdate.pre() || mc.thePlayer.fallDistance <= 3.0 && WorldUtil.getBlockBellow() != BlockPos.ORIGIN)
      return;
    switch (modeSetting.value()) {
      case GROUND -> eventUpdate.groundState(true);
      case TICK -> eventUpdate.groundState(mc.thePlayer.ticksExisted % 2 == 0);
      case COLLIDE -> {
        mc.thePlayer.motionY = 0;
        mc.thePlayer.fallDistance = 0;
        eventUpdate.groundState(true);
      }
    }
    if (roundSetting.value() && eventUpdate.groundState()) {
      Position position = eventUpdate.position();
      double oldPosY = position.posY();
      double newPosY = oldPosY - oldPosY % 0.015625;
      position.posY(newPosY);
      mc.thePlayer.setPosition(mc.thePlayer.posX, newPosY, mc.thePlayer.posZ);
    }
  }

  @Getter
  @AllArgsConstructor
  private enum Mode implements INameable {
    TICK("Tick"),
    COLLIDE("Collide"),
    GROUND("Ground Spoof");
    private final String getName;
  }

}
