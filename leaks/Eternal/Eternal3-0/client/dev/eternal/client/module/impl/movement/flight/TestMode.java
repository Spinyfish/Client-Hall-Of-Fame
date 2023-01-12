package dev.eternal.client.module.impl.movement.flight;

import dev.eternal.client.event.Subscribe;;
import com.viaversion.viaversion.libs.kyori.adventure.text.KeybindComponent;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventTeleport;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.impl.movement.Flight;
import dev.eternal.client.module.impl.movement.Speed;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.network.PacketUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;

public class TestMode extends Mode {
  private boolean bool, bool2, bool3, bool4;
  private int stage, count;
  private double x, y, z, px, py, pz, speed;
  private float yaw, pitch, pyaw, ppitch;

  public TestMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Override
  public void onEnable() {
    px = mc.thePlayer.posX;
    py = mc.thePlayer.posY;
    pz = mc.thePlayer.posZ;
    pyaw = mc.thePlayer.rotationYaw;
    ppitch = mc.thePlayer.rotationPitch;
    stage = 0;
  }

  @Override
  public void onDisable() {
    mc.timer.timerSpeed = 1;
    mc.thePlayer.motionX = 0;
    mc.thePlayer.motionZ = 0;
    mc.thePlayer.setPosition(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ);
  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    Flight flight = this.getOwner();
    mc.timer.timerSpeed = 0.005f;
    NumberSetting speed = flight.speedSetting();
    EntityPlayerSP playerInstance = mc.thePlayer;
    MovementInput movementInput = playerInstance.movementInput;
    playerInstance.motionY = movementInput.jump ? speed.value() : movementInput.sneak ? -speed.value() : 0;
    eventMove.friction(10);
  }

  @Subscribe
  public void handleTeleport(EventTeleport eventTeleport) {
  }


}
