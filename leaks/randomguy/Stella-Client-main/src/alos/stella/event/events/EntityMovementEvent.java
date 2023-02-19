package alos.stella.event.events;

import kotlin.jvm.internal.Intrinsics;
import alos.stella.event.Event;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EntityMovementEvent extends Event {
    @NotNull
    private final Entity movedEntity;

    @NotNull
    public final Entity getMovedEntity() {
        return this.movedEntity;
    }

    public EntityMovementEvent(@NotNull Entity movedEntity) {
        super();
        Intrinsics.checkNotNullParameter(movedEntity, "movedEntity");
        this.movedEntity = movedEntity;
    }

    @NotNull
    public final Entity component1() {
        return this.movedEntity;
    }

    @NotNull
    public final EntityMovementEvent copy(@NotNull Entity movedEntity) {
        Intrinsics.checkNotNullParameter(movedEntity, "movedEntity");
        return new EntityMovementEvent(movedEntity);
    }

    // $FF: synthetic method
    public static EntityMovementEvent copy$default(EntityMovementEvent var0, Entity var1, int var2, Object var3) {
        if ((var2 & 1) != 0) {
            var1 = var0.movedEntity;
        }

        return var0.copy(var1);
    }

    @NotNull
    public String toString() {
        return "EntityMovementEvent(movedEntity=" + this.movedEntity + ")";
    }

    public int hashCode() {
        Entity var10000 = this.movedEntity;
        return var10000 != null ? var10000.hashCode() : 0;
    }

    public boolean equals(@Nullable Object var1) {
        if (this != var1) {
            if (var1 instanceof EntityMovementEvent) {
                EntityMovementEvent var2 = (EntityMovementEvent)var1;
                if (Intrinsics.areEqual(this.movedEntity, var2.movedEntity)) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }
}
