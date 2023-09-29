package com.alan.clients.util.pathfinding.alan.api;

import net.minecraft.util.Vec3i;

public class Point {
    public Vec3i point;

    public Point(Vec3i point) {
        this.point = point;
    }

    public Point(final int x, final int y, final int z) {
        this.point = new Vec3i(x, y, z);
    }
}
