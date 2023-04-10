package crispy.features.event.impl.movement;

import crispy.features.event.Event;


public class EventStrafe extends Event<EventStrafe> {
    private final float Strafe;
    private final float Friction;
    private final float Forward;
    private boolean isPre;

    public EventStrafe(float Strafe, float Friction, float Forward, boolean isPre) {
        this.Strafe = Strafe;
        this.Forward = Forward;
        this.Friction = Friction;
        this.isPre = isPre;

    }

    public boolean isPre() {
        return isPre;
    }

    public void setPre(boolean pre) {
        isPre = pre;
    }

    public float getStrafe() {
        return Strafe;
    }

    public float getFric() {
        return Friction;
    }

    public float getFward() {
        return Forward;
    }

}
