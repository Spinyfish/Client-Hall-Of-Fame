/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import wtf.monsoon.api.event.Event;

public class EventStep
extends Event {
    private AxisAlignedBB axisAlignedBB;
    private Entity entity;
    private float height;

    public AxisAlignedBB getAxisAlignedBB() {
        return this.axisAlignedBB;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public float getHeight() {
        return this.height;
    }

    public void setAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        this.axisAlignedBB = axisAlignedBB;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventStep)) {
            return false;
        }
        EventStep other = (EventStep)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getHeight(), other.getHeight()) != 0) {
            return false;
        }
        AxisAlignedBB this$axisAlignedBB = this.getAxisAlignedBB();
        AxisAlignedBB other$axisAlignedBB = other.getAxisAlignedBB();
        if (this$axisAlignedBB == null ? other$axisAlignedBB != null : !this$axisAlignedBB.equals(other$axisAlignedBB)) {
            return false;
        }
        Entity this$entity = this.getEntity();
        Entity other$entity = other.getEntity();
        return !(this$entity == null ? other$entity != null : !((Object)this$entity).equals(other$entity));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventStep;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getHeight());
        AxisAlignedBB $axisAlignedBB = this.getAxisAlignedBB();
        result = result * 59 + ($axisAlignedBB == null ? 43 : $axisAlignedBB.hashCode());
        Entity $entity = this.getEntity();
        result = result * 59 + ($entity == null ? 43 : ((Object)$entity).hashCode());
        return result;
    }

    public String toString() {
        return "EventStep(axisAlignedBB=" + this.getAxisAlignedBB() + ", entity=" + this.getEntity() + ", height=" + this.getHeight() + ")";
    }

    public EventStep(AxisAlignedBB axisAlignedBB, Entity entity, float height) {
        this.axisAlignedBB = axisAlignedBB;
        this.entity = entity;
        this.height = height;
    }
}

