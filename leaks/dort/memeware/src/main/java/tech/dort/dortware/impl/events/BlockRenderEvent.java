package tech.dort.dortware.impl.events;

import tech.dort.dortware.api.event.Event;

public class BlockRenderEvent extends Event {

    private final double x;
    private final double y;
    private final double z;

    public BlockRenderEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
