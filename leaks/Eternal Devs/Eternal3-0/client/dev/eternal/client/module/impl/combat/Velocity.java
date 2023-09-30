package dev.eternal.client.module.impl.combat;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import dev.eternal.client.util.time.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@ModuleInfo(name = "Velocity", description = "Cancels (or reduces) knockback.", category = Module.Category.COMBAT)
public class Velocity extends Module {

  private final EnumSetting<VelocityMode> enumSetting = new EnumSetting<>(this, "Mode", VelocityMode.values());
  private final NumberSetting numberSetting = new NumberSetting(this, "Divisor", 50, 1, 100, 1);

  private final Stopwatch stopwatch = new Stopwatch();

  @Override
  protected void onEnable() {
    stopwatch.reset();
  }

  @Subscribe
  public void handlePacket(EventPacket eventPacket) {
    switch (enumSetting.value()) {
      case CANCEL -> {
        if (eventPacket.getPacket() instanceof S12PacketEntityVelocity s12) {
          eventPacket.cancelled(s12.getEntityID() == mc.thePlayer.getEntityId());
        }
        if (eventPacket.getPacket() instanceof S27PacketExplosion) {
          eventPacket.cancelled(true);
        }
      }
      case REDUCE -> {
        if (eventPacket.getPacket() instanceof S12PacketEntityVelocity) {
          S12PacketEntityVelocity entityVelocity = eventPacket.getPacket();
          if(entityVelocity.getEntityID() == mc.thePlayer.getEntityId()) {
            entityVelocity.motionX((int) (entityVelocity.getMotionX() * (numberSetting.value() / 100)));
//          entityVelocity.motionY((int) (entityVelocity.getMotionY() * (numberSetting.value() / 100)));
            entityVelocity.motionZ((int) (entityVelocity.getMotionZ() * (numberSetting.value() / 100)));
          }
        }
      }
    }
  }

  @Getter
  @AllArgsConstructor
  public enum VelocityMode implements INameable {
    CANCEL("Cancel"),
    REDUCE("Reduce");
    private final String getName;
  }
}
