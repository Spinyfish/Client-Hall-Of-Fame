package dev.eternal.client.module.impl.render;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.util.movement.MovementUtil;
import lombok.Getter;

@Getter
@ModuleInfo(name = "ViewBobbing", description = "Changes how view bobbing works", category = Module.Category.RENDER)
public class ViewBobbing extends Module {

  private final BooleanSetting handOnlySetting = new BooleanSetting(this, "Hand Only", true);
  private final NumberSetting intensitySetting = new NumberSetting(this, "Intensity", 0.15, 0.05, 0.4, 0.05);

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if (MovementUtil.isMovingOnGround())
      mc.thePlayer.cameraYaw = intensitySetting.value().floatValue();
  }
}
