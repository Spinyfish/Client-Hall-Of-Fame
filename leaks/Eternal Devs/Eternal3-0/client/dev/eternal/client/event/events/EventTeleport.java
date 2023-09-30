package dev.eternal.client.event.events;

import dev.eternal.client.event.AbstractEvent;
import dev.eternal.client.util.movement.data.Position;
import dev.eternal.client.util.movement.data.Rotation;
import dev.eternal.client.util.network.PacketUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class EventTeleport extends AbstractEvent {

  private Position position;

  private Rotation rotation;

  private Set<S08PacketPlayerPosLook.EnumFlags> flags;


  public void handleSilently(double maxDistance) {
    cancelled(true);

    double x = position.posX();
    double y = position.posY();
    double z = position.posZ();

    float yaw = rotation.rotationYaw();
    float pitch = rotation.rotationPitch();

    EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;

    if (entityPlayerSP.getDistance(x, y, z) >= maxDistance) {
      entityPlayerSP.setPosition(x, y, z);
    }

    PacketUtil.sendSilent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, false));
  }

  public void trySafeReject(double maxSilentDistance) {
    cancelled(true);

    double x = position.posX();
    double y = position.posY();
    double z = position.posZ();

    EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;

    if (entityPlayerSP.getDistance(x, y, z) >= maxSilentDistance) {
      entityPlayerSP.setPosition(x, y, z);
    }
    PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
  }

}
