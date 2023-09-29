package me.jinthium.straight.impl.event.render;

import me.jinthium.straight.api.event.Event;
import me.jinthium.straight.impl.utils.vector.Vector2f;

public final class PlayerLookEvent extends Event {
    private Vector2f rotation;

    public PlayerLookEvent(Vector2f rotation) {
        this.rotation = rotation;
    }

    public Vector2f getRotation() {
        return rotation;
    }

    public void setRotation(Vector2f rotation) {
        this.rotation = rotation;
    }
}