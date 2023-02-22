package com.alan.clients.util.pathfinding.alan.api;

import net.minecraft.util.Vec3i;

import java.util.ArrayList;

public class Path {
    public ArrayList<Point> points;

    public Path(ArrayList<Point> points) {
        this.points = points;
    }

    public Path getIncrementalVersion(Vec3i offset) {
        ArrayList<Point> points = new ArrayList<>(this.points);
        points.add(new Point(this.points.get(this.points.size() - 1).point.add(offset)));
        return new Path(points);
    }

    public Vec3i getLastPoint() {
        return points.get(points.size() - 1).point;
    }
}
