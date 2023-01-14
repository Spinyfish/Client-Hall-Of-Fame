package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;

public class EventSneak extends Event {
    private boolean isSneaking;
    private boolean forcingSlowDown;

    public boolean isSneaking() {
        return this.isSneaking;
    }

    public boolean isForcingSlowDown() {
        return this.forcingSlowDown;
    }

    public void setSneaking(boolean isSneaking) {
        this.isSneaking = isSneaking;
    }

    public void setForcingSlowDown(boolean forcingSlowDown) {
        this.forcingSlowDown = forcingSlowDown;
    }

    public EventSneak(boolean isSneaking, boolean forcingSlowDown) {
        this.isSneaking = isSneaking;
        this.forcingSlowDown = forcingSlowDown;
    }
}
