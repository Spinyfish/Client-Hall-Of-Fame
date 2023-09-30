package alos.stella.event.events;

import alos.stella.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.jetbrains.annotations.Nullable;

public final class AttackEvent extends Event {
    @Nullable
    private final Entity targetEntity;
    @Nullable
    public static EntityLivingBase asEntityLivingBase;

    @Nullable
    public final Entity getTargetEntity() {
        return this.targetEntity;
    }

    public AttackEvent(@Nullable Entity targetEntity) {
        this.targetEntity = targetEntity;
        this.asEntityLivingBase = asEntityLivingBase;
    }
}
