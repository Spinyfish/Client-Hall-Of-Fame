package dev.eternal.client.module.impl.movement.flight;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventTeleport;
import dev.eternal.client.module.impl.movement.Flight;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.network.PacketUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C03PacketPlayer;

;

public class TeleportMode extends Mode {
  public TeleportMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleTeleport(EventTeleport eventTeleport) {
    eventTeleport.handleSilently(10);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    Flight flight = this.getOwner();
    NumberSetting speed = flight.speedSetting();
    EntityPlayerSP playerInstance = mc.thePlayer;
    GameSettings gameSettings = mc.gameSettings;
    KeyBinding keyBindSneak = gameSettings.keyBindSneak, keyBindJump = gameSettings.keyBindJump;
    mc.thePlayer.motionY = 0;
    MovementUtil.setMotion(eventMove, speed.value());
    playerInstance.setPosition(playerInstance.posX, playerInstance.posY + (keyBindJump.isKeyDown() ? keyBindSneak.isKeyDown() ? 0 : speed.value() / 2 : keyBindSneak.isKeyDown() ? -speed.value() / 2 : 0), playerInstance.posZ);
    PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 11, mc.thePlayer.posZ, true));
    PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
  }
}
