/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 14:12
 */
package dev.myth.events;

import dev.codeman.eventbus.Event;
import dev.myth.api.event.EventState;
import lombok.Getter;
import lombok.Setter;

public class UpdateEvent extends Event {

    @Getter @Setter private EventState state;
    @Getter @Setter private double posX, posY, posZ;
    @Getter @Setter private double lastX, lastY, lastZ;
    @Getter @Setter private float yaw, pitch, lastYaw, lastPitch;
    @Getter @Setter private boolean onGround, sprinting, sneaking;

    public UpdateEvent(EventState state, double posX, double posY, double posZ, float yaw, float pitch, boolean onGround, boolean sprinting, boolean sneaking) {
        this.state = state;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.sprinting = sprinting;
        this.sneaking = sneaking;
    }

}
