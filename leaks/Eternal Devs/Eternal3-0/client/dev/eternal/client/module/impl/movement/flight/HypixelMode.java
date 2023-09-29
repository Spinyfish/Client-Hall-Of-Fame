package dev.eternal.client.module.impl.movement.flight;

import dev.eternal.client.Client;
import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventTeleport;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.movement.data.Motion;
import dev.eternal.client.util.movement.data.Rotation;
import dev.eternal.client.util.network.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HypixelMode extends Mode {
  private double lastPosX, lastPosY, lastPosZ;
  private float lastYaw, lastPitch;

  private final Queue<Packet<?>> packetList = new ConcurrentLinkedQueue<>();

  public HypixelMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Override
  public void onEnable() {
    lastPosX = mc.thePlayer.posX;
    lastPosY = mc.thePlayer.posY;
    lastPosZ = mc.thePlayer.posZ;
    lastYaw = mc.thePlayer.rotationYaw;
    lastPitch = mc.thePlayer.rotationPitch;
    packetList.clear();
  }

  @Override
  public void onDisable() {
    while (!packetList.isEmpty())
      PacketUtil.sendSilent(packetList.poll());
    PacketUtil.sendSilent(new C03PacketPlayer.C06PacketPlayerPosLook(lastPosX, lastPosY, lastPosZ, lastYaw, lastPitch, false));
    mc.timer.timerSpeed = 1;
    mc.thePlayer.motionX = 0;
    mc.thePlayer.motionZ = 0;
    mc.thePlayer.setPosition(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ);
  }

  @Subscribe
  public void handleMove(EventMove event) {
    MovementUtil.setMotion(event, MovementUtil.getBaseGroundSpeed());
  }

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if (!eventUpdate.pre()) {
      if (mc.thePlayer.onGround) {
        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 11, mc.thePlayer.posZ, false));
        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        mc.thePlayer.motionY = 0.42F;
        mc.thePlayer.fallDistance = 1;
      } else if (mc.thePlayer.fallDistance > 1) {
        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 11, mc.thePlayer.posZ, false));
        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        mc.thePlayer.motionY = -0.00000132438575;
        mc.timer.timerSpeed = 0.6f;
        for (int j = 0; j < (mc.thePlayer.ticksExisted % 6 >= 4 ? 8 : 1); j++) {
          Motion motion = MovementUtil.getMotion(MovementUtil.getBaseGroundSpeed() * 0.95);
          if (!mc.thePlayer.movementInput.jump) {
            mc.thePlayer.moveEntity(motion.motionX(), -0.00000003458946587368, motion.motionZ());
            PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 11, mc.thePlayer.posZ, false));
            PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
          }
        }
      }
      if (mc.thePlayer.movementInput.jump)
        mc.thePlayer.motionY = 0.42f;
    }
    if (mc.thePlayer.getDistance(this.lastPosX, this.lastPosY, this.lastPosZ) >= 10) {
      while (!packetList.isEmpty())
        PacketUtil.sendNoEvent(packetList.poll());
    }
  }

  @Subscribe
  public void handleTeleport(EventTeleport eventTeleport) {
    var px = eventTeleport.position().posX();
    var py = eventTeleport.position().posY();
    var pz = eventTeleport.position().posZ();
    packetList.add(new C03PacketPlayer.C06PacketPlayerPosLook(px, py, pz, eventTeleport.rotation().rotationYaw(), eventTeleport.rotation().rotationPitch(), false));
    Client.singleton().displayMessage("HACKING IN PROGRESS ! ! ! (SEND C400PACKETDISABLEWATCHDOGV6 (REAL) ONG) " + RandomStringUtils.randomAlphabetic(12));
    var yaw = eventTeleport.rotation().rotationYaw();
    var pitch = eventTeleport.rotation().rotationPitch();
    this.lastPosX = px;
    this.lastPosY = py;
    this.lastPosZ = pz;
    this.lastYaw = yaw;
    this.lastPitch = pitch;
    eventTeleport.cancelled(mc.thePlayer.getDistance(this.lastPosX, this.lastPosY, this.lastPosZ) <= 10);
  }


}
