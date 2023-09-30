package felix.events.render;

public class EventRender3D {

    private float partialTicks;

    public EventRender3D(final float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
