package dev.eternal.client.module.impl.movement;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventSlowdown;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import dev.eternal.client.util.network.PacketUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "NoSlowdown", description = "Prevents item slowdown", category = Module.Category.MOVEMENT)
public class NoSlowdown extends Module {

  private final EnumSetting<NoSlowMode> mode = new EnumSetting<>(this, "Mode", NoSlowMode.values());

  @Subscribe
  public void onUpdate(EventUpdate event) {
    if (mode.value() == NoSlowMode.NCP && mc.thePlayer.isUsingItem() && (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
      if (event.pre()) {
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      } else {
        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
      }
    }
  }

  @Subscribe
  public void handleItemSlowdown(EventSlowdown eventSlowdown) {
    eventSlowdown.cancelled(true);
  }

  @Getter
  @AllArgsConstructor
  public enum NoSlowMode implements INameable {
    VANILLA("Vanilla"),
    NCP("NCP");
    private final String getName;
  }

}
