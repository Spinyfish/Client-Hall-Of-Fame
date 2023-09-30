package dev.tenacity.event.impl.player.input;

import dev.tenacity.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.entity.EntityLivingBase;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

@AllArgsConstructor
public class AttackEvent extends Event {

    public final EntityLivingBase targetEntity;

    @Exclude(Strategy.NAME_REMAPPING)
    public EntityLivingBase getTargetEntity() {
        return targetEntity;
    }

}
