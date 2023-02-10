/**
 * @project Myth
 * @author CodeMan
 * @at 08.08.22, 16:48
 */
package dev.myth.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.entity.EntityPlayerSP;

@AllArgsConstructor
public class MoveFlyingEvent extends Event {

    @Getter @Setter
    private float yaw, strafe, forward, friction;

    public void setSpeedPartialStrafe(EntityPlayerSP player, float speed, float strafeMotion) {
        float remainder = 1.0F - strafeMotion;
        if (player.onGround) {
            player.motionX = player.motionZ = 0.0;
            setFriction(speed);
        } else {
            player.motionX *= strafeMotion;
            player.motionZ *= strafeMotion;
            setFriction(speed * remainder);
        }
    }

}
