/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.utils.math;

import de.gerrygames.viarewind.utils.math.Vector3d;

public class Ray3d {
    Vector3d start;
    Vector3d dir;

    public Ray3d(Vector3d start, Vector3d dir) {
        this.start = start;
        this.dir = dir;
    }

    public Vector3d getStart() {
        return this.start;
    }

    public Vector3d getDir() {
        return this.dir;
    }
}

