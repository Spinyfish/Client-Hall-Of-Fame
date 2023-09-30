package me.jinthium.straight.impl.event.movement;

import me.jinthium.straight.api.event.Event;

public class KeepSprintEvent extends Event {
    public double slowDown;
    public boolean sprint;

    public KeepSprintEvent(double slowDown, boolean sprint) {
        this.slowDown = slowDown;
        this.sprint = sprint;
    }

    public double getSlowDown() {
        return slowDown;
    }

    public void setSlowDown(double slowDown) {
        this.slowDown = slowDown;
    }

    public boolean isSprint() {
        return sprint;
    }

    public void setSprint(boolean sprint) {
        this.sprint = sprint;
    }
}
