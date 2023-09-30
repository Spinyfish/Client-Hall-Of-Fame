package dev.africa.pandaware.impl.module.combat.criticals;

import dev.africa.pandaware.impl.event.player.MotionEvent;

public interface ICriticalsMode {
    void handle(MotionEvent event, int ticksExisted);

    default void entityIsNull() {

    }
}
