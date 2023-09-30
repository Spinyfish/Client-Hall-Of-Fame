package me.tojatta.api.utilities.vector.impl;

import me.tojatta.api.utilities.vector.Vector;

/**
 * Created by Tojatta on 12/17/2016.
 */
public class Vector3<T extends Number> extends Vector<Number> {

    public Vector3(T x, T y, T z) {
        super(x, y, z);
    }

    public Vector2<T> toVector2() {
        return new Vector2<>(((T) getX()), ((T) getY()));
    }

}
