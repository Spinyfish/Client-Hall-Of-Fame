package alos.stella.event.events;

import kotlin.jvm.internal.Intrinsics;
import alos.stella.event.Event;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

public final class RenderEntityEvent extends Event {
    @NotNull
    private final Entity entity;
    private final double x;
    private final double y;
    private final double z;
    private final float entityYaw;
    private final float partialTicks;

    @NotNull
    public final Entity getEntity() {
        return this.entity;
    }

    public final double getX() {
        return this.x;
    }

    public final double getY() {
        return this.y;
    }

    public final double getZ() {
        return this.z;
    }

    public final float getEntityYaw() {
        return this.entityYaw;
    }

    public final float getPartialTicks() {
        return this.partialTicks;
    }

    public RenderEntityEvent(@NotNull Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super();
        Intrinsics.checkNotNullParameter(entity, "entity");
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityYaw = entityYaw;
        this.partialTicks = partialTicks;
    }
}
