package com.alan.clients.util.vector;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Vector2i {

    public int x, y;

    public Vector2i() {
    }

    public Vector2i(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
}
