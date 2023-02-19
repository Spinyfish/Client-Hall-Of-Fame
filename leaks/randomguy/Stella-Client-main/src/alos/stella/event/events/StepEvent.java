package alos.stella.event.events;

import alos.stella.event.Event;

public final class StepEvent extends Event {
    private float stepHeight;

    public final float getStepHeight() {
        return this.stepHeight;
    }

    public final void setStepHeight(float var1) {
        this.stepHeight = var1;
    }

    public StepEvent(float stepHeight) {
        this.stepHeight = stepHeight;
    }
}