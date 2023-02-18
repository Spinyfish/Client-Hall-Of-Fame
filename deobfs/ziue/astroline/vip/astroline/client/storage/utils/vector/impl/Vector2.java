/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.storage.utils.vector.Vector
 *  vip.astroline.client.storage.utils.vector.impl.Vector3
 */
package vip.astroline.client.storage.utils.vector.impl;

import vip.astroline.client.storage.utils.vector.Vector;
import vip.astroline.client.storage.utils.vector.impl.Vector3;

public class Vector2<T extends Number>
extends Vector<Number> {
    public Vector2(T x, T y) {
        super(x, y, (Number)0);
    }

    public Vector3<T> toVector3() {
        return new Vector3(this.getX(), this.getY(), this.getZ());
    }
}
