package me.jinthium.straight.impl.event.game;

import me.jinthium.straight.api.event.Event;
import net.minecraft.entity.Entity;

public final class PlayerAttackEvent extends Event {
    private Entity target;

    public PlayerAttackEvent(Entity target){
        this.target = target;
    }

    public Entity getTarget() {
        return target;
    }
}