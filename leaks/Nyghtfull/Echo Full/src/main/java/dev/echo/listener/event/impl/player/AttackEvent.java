package dev.echo.listener.event.impl.player;

import dev.echo.listener.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.entity.EntityLivingBase;


@AllArgsConstructor
public class AttackEvent extends Event {

    private final EntityLivingBase targetEntity;


    public EntityLivingBase getTargetEntity() {
        return targetEntity;
    }

}
