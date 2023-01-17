package com.alan.clients.util.pathfinding.alan;

import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.pathfinding.alan.api.Path;
import com.alan.clients.util.profiling.Profiler;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.util.pathfinding.alan.api.Point;
import net.minecraft.util.Vec3i;

import java.util.ArrayList;

public class Pathfinder {
    public ArrayList<Path> paths = new ArrayList<>();

    boolean[][][] taken = new boolean[Integer.MAX_VALUE][Integer.MAX_VALUE][Integer.MAX_VALUE];

    public Point end;

    public Profiler totalProfiler = new Profiler();
    public Profiler checkingProfiler = new Profiler();
    public Profiler availableProfiler = new Profiler();
    public Profiler getPointsProfiler = new Profiler();

    public Pathfinder(Point start, Point end) {
        ArrayList<Point> points = new ArrayList<>();
        points.add(start);
        paths.add(new Path(points));
        this.setTaken(start.point);
        this.end = end;
    }

    public void increment() {
        totalProfiler.reset();
        checkingProfiler.reset();
        availableProfiler.reset();
        getPointsProfiler.reset();

        totalProfiler.start();
        ArrayList<Path> paths = new ArrayList<>();

        for (Path path : this.paths) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    for (int y = -1; y <= 1; y++) {
                        paths.addAll(add(path, new Vec3i(x, y, z)));
                    }
                }
            }
        }

        totalProfiler.stop();

        ChatUtil.display(" Points: " + this.paths.size());
        ChatUtil.display(" Checking Time: " + checkingProfiler.getTotalTime());
        ChatUtil.display(" Get Points Time: " + getPointsProfiler.getTotalTime());
        ChatUtil.display(" Total Time: " + totalProfiler.getTotalTime());

        this.paths = paths;
    }

    public Path check() {
        for (Path path : this.paths) {
            if (path.getLastPoint().equals(end.point)) {
                return path;
            }
        }

        return null;
    }

    public ArrayList<Path> add(Path path, Vec3i offset) {
        ArrayList<Path> paths = new ArrayList<>();

        getPointsProfiler.start();
        Path p = new Path(path.points).getIncrementalVersion(offset);
        Point p2 = p.points.get(p.points.size() - 1);
        getPointsProfiler.stop();

        checkingProfiler.start();
        if (available(p2.point)) {
            paths.add(p);
            this.setTaken(p2.point);
        }
        checkingProfiler.stop();

        return paths;
    }

    public boolean available(Vec3i position) {

        if (this.getTaken(position)) {
            return false;
        }

        return true;
    }

    public void setTaken(Vec3i position) {
        taken[position.getX()][position.getY()][position.getZ()] = true;
    }

    public boolean getTaken(Vec3i position) {
        return taken[position.getX()][position.getY()][position.getZ()];
    }
}
