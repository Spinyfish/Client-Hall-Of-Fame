package club.pulsive.impl.event.player;

import club.pulsive.api.event.Event;
import club.pulsive.impl.util.Position;
import club.pulsive.impl.util.Rotation;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.MovementUtil;
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
public class PlayerTeleportEvent extends Event {

    private Position position;

    private Rotation rotation;

    private Set<S08PacketPlayerPosLook.EnumFlags> flags;


    public void handleSilently(double maxDistance) {
        setCancelled(true);

        double x = position.getPosX();
        double y = position.getPosY();
        double z = position.getPosZ();

        float yaw = rotation.getRotationYaw();
        float pitch = rotation.getRotationPitch();

        EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;

        if (entityPlayerSP.getDistance(x, y, z) >= maxDistance) {
            entityPlayerSP.setPositionAndRotation(x, y, z, yaw, pitch);
        }

        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
    }

    public void trySafeReject(double maxSilentDistance) {
        setCancelled(true);

        double x = position.getPosX();
        double y = position.getPosY();
        double z = position.getPosZ();

        EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;

        if (entityPlayerSP.getDistance(x, y, z) >= maxSilentDistance) {
            entityPlayerSP.setPosition(x, y, z);
        }

        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y - y % 0.015625, z, true));
    }

}