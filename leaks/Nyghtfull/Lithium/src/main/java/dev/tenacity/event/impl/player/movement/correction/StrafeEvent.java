package dev.tenacity.event.impl.player.movement.correction;

import dev.tenacity.event.Event;
import dev.tenacity.utils.Utils;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.skidded.MoveUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

@Getter
@Setter
@AllArgsConstructor
public class StrafeEvent extends Event implements Utils {

    private float strafe, forward, friction, yaw, pitch;

    public void setSpeed(final double speed, final double motionMultiplier) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        mc.thePlayer.motionX *= motionMultiplier;
        mc.thePlayer.motionZ *= motionMultiplier;
    }

    public void setSpeed(final double speed) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
    }

}