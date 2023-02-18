/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.storage.utils.vector.Vector
 *  vip.astroline.client.storage.utils.vector.impl.Vector2
 */
package vip.astroline.client.storage.utils.vector.impl;

import vip.astroline.client.storage.utils.vector.Vector;
import vip.astroline.client.storage.utils.vector.impl.Vector2;

public class Vector3<T extends Number>
extends Vector<Number> {
    public Vector3(T x, T y, T z) {
        super(x, y, z);
    }

    public Vector2<T> toVector2() {
        return new Vector2(this.getX(), this.getY());
    }
}
