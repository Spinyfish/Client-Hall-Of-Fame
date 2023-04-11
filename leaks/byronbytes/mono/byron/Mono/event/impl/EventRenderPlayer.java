package byron.Mono.event.impl;

import byron.Mono.event.Event;

public class EventRenderPlayer extends Event { // This one is 50% implomented. (I think.)

    public /* synthetic */ float yaw;
    private /* synthetic */ float partialTicks;
    public /* synthetic */ float pitch;
    public /* synthetic */ float yawChange;

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }

    public float getPitch() {
        return this.pitch;
    }

    public EventRenderPlayer(final float yaw, final float pitch, final float yawChange, final float partialTicks) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.yawChange = yawChange;
        this.partialTicks = partialTicks;
    }
}
