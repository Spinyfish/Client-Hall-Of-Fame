package me.jinthium.straight.impl.event.game;

import me.jinthium.straight.api.event.Event;

public class MouseOverEvent extends Event {

    private double range;
    private float expand;

    public MouseOverEvent(double range, float expand) {
        this.range = range;
        this.expand = expand;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public float getExpand() {
        return expand;
    }

    public void setExpand(float expand) {
        this.expand = expand;
    }
}