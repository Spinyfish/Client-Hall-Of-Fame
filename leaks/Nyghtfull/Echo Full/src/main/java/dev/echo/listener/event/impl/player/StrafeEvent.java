package dev.echo.listener.event.impl.player;

import dev.echo.listener.event.Event;
import dev.echo.utils.player.MovementUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

@Getter
@Setter
@AllArgsConstructor
public class StrafeEvent extends Event {

    private float strafe, forward, friction, yaw, pitch;

    public void applyMotion(double speed, float strafeMotion) {
        float remainder = 1 - strafeMotion;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        strafeMotion *= 0.91;
        if (player.onGround) {
            MovementUtils.setSpeed(speed);
        } else {
            player.motionX = player.getMotionX() * strafeMotion;
            player.motionZ = player.getMotionZ() * strafeMotion;
            friction = (float) speed * remainder;
        }
    }

}