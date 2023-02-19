package alos.stella.event.events;

import kotlin.jvm.internal.Intrinsics;
import alos.stella.event.Event;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

public final class EntityDamageEvent extends Event {
    @NotNull
    private final Entity damagedEntity;

    @NotNull
    public final Entity getDamagedEntity() {
        return this.damagedEntity;
    }

    public EntityDamageEvent(@NotNull Entity damagedEntity) {
        super();
        Intrinsics.checkNotNullParameter(damagedEntity, "damagedEntity");
        this.damagedEntity = damagedEntity;
    }
}
