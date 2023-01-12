package dev.eternal.client.module.impl.movement.speed;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventAbsoluteMove;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.network.PacketUtil;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;

public class TestMode extends Mode {

  private double x, y, z, prevX, prevY, prevZ, d1, d2, d3, speed, prevSpeed;
  private float yaw, pitch, prevYaw, prevPitch;
  boolean bool1, bool2, bool3, bool4;

  public TestMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Override
  public void onEnable() {
    super.onEnable();
    speed = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    x = prevX = mc.thePlayer.posX;
    y = prevY = mc.thePlayer.posY;
    z = prevZ = mc.thePlayer.posZ;
    yaw = prevYaw = mc.thePlayer.rotationYaw;
    pitch = prevPitch = mc.thePlayer.rotationPitch;
  }

  @Override
  public void onDisable() {
    super.onDisable();
  }

  @Subscribe
  public void handleAbsoluteMove(EventAbsoluteMove eventAbsoluteMove) {
  }

  @Subscribe
  public void handlePacket(EventPacket eventPacket) {

  }

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {

  }

  @Subscribe
  public void handleMove(EventMove eventMove) {
    if (MovementUtil.isMoving()) {
      if (mc.thePlayer.onGround) {
        mc.thePlayer.motionY = 0.4f;
        MovementUtil.setMotion(eventMove, MovementUtil.getBaseGroundSpeed() + 0.98);
      } else {
        if (mc.thePlayer.motionY < 0.25 && mc.thePlayer.motionY > 0.0) {
          mc.thePlayer.motionY -= 0.0784;
        }
        MovementUtil.setMotion(MovementUtil.getBaseGroundSpeed() + 0.6);
      }
    }
  }

}
