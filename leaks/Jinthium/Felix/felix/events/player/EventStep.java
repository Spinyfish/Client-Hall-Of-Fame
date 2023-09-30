package felix.events.player;

import felix.events.Cancellable;
import net.minecraft.entity.Entity;

public class EventStep extends Cancellable {

    /**
     * The entity stepping.
     */
    private Entity entity;

    /**
     * The step height.
     */
    private float height;

    private boolean pre;

    public EventStep(final Entity entity, final boolean pre) {
        this.entity = entity;
        this.height = entity.stepHeight;
        this.pre = pre;
    }

    public EventStep(final Entity entity) {
        this.entity = entity;
        this.height = entity.stepHeight;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isPre() {
        return pre;
    }
}
