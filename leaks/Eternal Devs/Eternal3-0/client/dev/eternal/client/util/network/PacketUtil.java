package dev.eternal.client.util.network;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

@UtilityClass
public class PacketUtil {

  private final Minecraft minecraftInstance = Minecraft.getMinecraft();

  public void sendSilent(Packet<?> packet) {
    minecraftInstance.getNetHandler().getNetworkManager().sendPacket(packet);
  }

  public void send(Packet<?> packet) {
    minecraftInstance.getNetHandler().addToSendQueue(packet);
  }

  public C03PacketPlayer.C06PacketPlayerPosLook translate(C03PacketPlayer input) {
    EntityPlayerSP playerInstance = minecraftInstance.thePlayer;
    double x = playerInstance.posX,
        y = playerInstance.posY,
        z = playerInstance.posZ;
    float yaw = playerInstance.rotationYaw,
        pitch = playerInstance.rotationPitch;
    final boolean onGround = input.isOnGround();
    if (input.isMoving()) {
      x = input.getPositionX();
      y = input.getPositionY();
      z = input.getPositionZ();
    }
    if (input.isRotating()) {
      yaw = input.getYaw();
      pitch = input.getPitch();
    }
    return new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, onGround);
  }

  public void sendNoEvent(Packet<?> packet) {
    minecraftInstance.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
  }
}
