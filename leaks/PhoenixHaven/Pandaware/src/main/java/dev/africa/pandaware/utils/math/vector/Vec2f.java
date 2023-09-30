package dev.africa.pandaware.utils.math.vector;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Vec2f {
    private float x, y;

    public Vec2f() {
        this(0, 0);
    }

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
