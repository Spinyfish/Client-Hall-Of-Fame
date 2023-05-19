package club.marsh.bloom.impl.events;

import club.marsh.bloom.impl.events.Event;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class UpdateEvent extends Event {

    public double x, y, z;
    public float yaw, pitch;
    public boolean ground, sneak;

    public UpdateEvent(double x, double y, double z, float yaw, float pitch, boolean ground, boolean sneak)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.ground = ground;
        this.sneak = sneak;
    }

}
