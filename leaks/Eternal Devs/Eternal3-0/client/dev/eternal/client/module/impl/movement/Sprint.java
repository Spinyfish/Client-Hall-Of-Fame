package dev.eternal.client.module.impl.movement;

import dev.eternal.client.event.EventPriority;
import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventForceSprint;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.module.impl.player.Scaffold;
import dev.eternal.client.property.Property;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.util.movement.MovementUtil;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import scheme.Scheme;

@ModuleInfo(name = "Sprint", description = "Automatically Sprints.", category = Module.Category.MOVEMENT)
public class Sprint extends Module {

  private boolean strafeCheck;
  private final BooleanSetting omnidirectional = new BooleanSetting(this, "Omnidirectional", true);
  private final BooleanSetting cancelSprint = new BooleanSetting(this, "Cancel Sprint", true);

  @Subscribe
  public void handleSprint(EventForceSprint eventForceSprint) {
    eventForceSprint.forced(getShouldSprint());
  }

  private boolean getShouldSprint() {
    return (omnidirectional.value() || (strafeCheck &&
                (mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying) &&
                !mc.thePlayer.isUsingItem() && !mc.thePlayer.isCollidedHorizontally));
  }

  @Subscribe(priority = EventPriority.LAST)
  public void onMove(EventMove eventMove) {
    strafeCheck = MovementUtil.checkMoveFix(eventMove);
  }

  @Subscribe
  public void handlePacket(EventPacket eventPacket) {
    if (mc.currentScreen instanceof GuiDownloadTerrain)
      mc.displayGuiScreen(null);
    if (eventPacket.getPacket() instanceof C0BPacketEntityAction && cancelSprint.value()) {
      C0BPacketEntityAction packetEntityAction = eventPacket.getPacket();
      switch (packetEntityAction.getAction()) {
        case START_SPRINTING, STOP_SPRINTING -> eventPacket.cancelled(true);
      }
    }
  }

}
