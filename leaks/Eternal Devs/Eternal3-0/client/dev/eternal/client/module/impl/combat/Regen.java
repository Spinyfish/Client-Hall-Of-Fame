package dev.eternal.client.module.impl.combat;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import dev.eternal.client.util.network.PacketUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;

@ModuleInfo(name = "Regen", description = "", category = Module.Category.COMBAT)
public class Regen extends Module {

  private final EnumSetting<Mode> mode = new EnumSetting<>(this, "Mode", Mode.values());

  private final NumberSetting health = new NumberSetting(this, "Health", 19.5, 1, 20, .5);
  private final NumberSetting packets = new NumberSetting(this, "Packets", 20, 1, 120, 1);

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if (mc.thePlayer.onGround && eventUpdate.pre() && mc.thePlayer.getHealth() <= health.value() && !mc.thePlayer.isDead) {
      switch (mode.value()) {
        case CLIP -> PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition());
        case GHOSTLY -> PacketUtil.sendSilent(new C0CPacketInput());
        case OLD_VERUS -> PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
            mc.thePlayer.posY - 0.5D, mc.thePlayer.posZ, true));
      }
      for (int i = 0; i < packets.value().intValue(); i++) {
        PacketUtil.sendSilent(new C0CPacketInput());
        PacketUtil.sendSilent(new C03PacketPlayer.C06PacketPlayerPosLook(Math.random(), Math.random(), Math.random(), (float) Math.random(), 0, true));
        PacketUtil.sendSilent(new C03PacketPlayer.C05PacketPlayerLook((float) Math.random(), 0, true));
      }
    }
  }


  @AllArgsConstructor
  @Getter
  private enum Mode implements INameable {
    GHOSTLY("Ghostly"),
    OLD_VERUS("Old Verus"),
    VANILLA("Vanilla"),
    CLIP("Clip");
    private final String getName;
  }

}
