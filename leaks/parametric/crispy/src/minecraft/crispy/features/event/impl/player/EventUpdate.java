package crispy.features.event.impl.player;

import crispy.features.event.Event;
import net.minecraft.client.entity.EntityPlayerSP;

public class EventUpdate
        extends Event<EventUpdate> {
    public static float yaw;
    public static float pitch;
    public double y;
    public double x;
    public double z;
    public boolean pre;
    public boolean ground;
    public boolean sneak;


    public EventUpdate(double x, double y, double z, float yaw, float pitch, boolean ground, boolean pre, boolean sneak) {

        this.y = y;


        EventUpdate.yaw = yaw;

        EventUpdate.pitch = pitch;

        this.pre = pre;

        this.ground = ground;
        this.sneak = sneak;

    }


    public boolean ground() {

        return this.ground;

    }


    public void ground(boolean newGround) {

        this.ground = newGround;

    }


    public double getY() {

        return this.y;

    }

    public void setY(double newy) {

        this.y = newy;

    }

    public double getX() {
        return this.x;
    }

    public void setX(double newX) {
        this.x = newX;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double newZ) {
        this.z = newZ;
    }

    public float getYaw() {

        return yaw;

    }


    public void setYaw(float newyaw) {

        yaw = newyaw;
        EntityPlayerSP.PostYaw = newyaw;
    }


    public float getPitch() {

        return pitch;

    }


    public void setPitch(float newpitch) {
        pitch = newpitch;
        EntityPlayerSP.PostPitch = newpitch;
    }


    public boolean isPre() {
        return this.pre;
    }
}
