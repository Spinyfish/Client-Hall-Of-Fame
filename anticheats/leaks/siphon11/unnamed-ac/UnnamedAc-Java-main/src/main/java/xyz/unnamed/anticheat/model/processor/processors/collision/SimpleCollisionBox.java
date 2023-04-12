package xyz.unnamed.anticheat.model.processor.processors.collision;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.utilities.math.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


@Getter
@Setter
public class SimpleCollisionBox {
    public double xMin, yMin, zMin, xMax, yMax, zMax;


    public SimpleCollisionBox() {
        this(0, 0, 0, 0, 0, 0);
    }

    public SimpleCollisionBox(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.zMin = zMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.zMax = zMax;
    }

    public List<Block> getCollidingBlocks(PlayerData data) {

        final List<Block> blocks = new ArrayList<>();

        final int xFloor = (int) Math.floor(xMin);
        final int xCeil = (int) Math.ceil(xMax);

        final int yFloor = (int) Math.floor(yMin);
        final int yCeil = (int) Math.ceil(yMax);

        final int zFloor = (int) Math.floor(zMin);
        final int zCeil = (int) Math.ceil(zMax);

        for (int x = xFloor; x < xCeil; x++) {

            for (int y = yFloor; y < yCeil; y++) {

                for (int z = zFloor; z < zCeil; z++) {

                    final Location loc = new Location(data.getPlayer().getWorld(), x, y, z);

                    if (loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                        final Block b = loc.getBlock();
                        blocks.add(b);
                    }

                }

            }

        }

        return blocks;
    }

    private void sort() {
        double temp = 0;
        if (xMin >= xMax) {
            temp = xMin;
            this.xMin = xMax;
            this.xMax = temp;
        }
        if (yMin >= yMax) {
            temp = yMin;
            this.yMin = yMax;
            this.yMax = temp;
        }
        if (zMin >= zMax) {
            temp = zMin;
            this.zMin = zMax;
            this.zMax = temp;
        }
    }

    public SimpleCollisionBox copy() {
        return new SimpleCollisionBox(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public SimpleCollisionBox offset(double x, double y, double z) {
        this.xMin += x;
        this.yMin += y;
        this.zMin += z;
        this.xMax += x;
        this.yMax += y;
        this.zMax += z;
        return this;
    }

    public SimpleCollisionBox expandMin(double x, double y, double z) {
        this.xMin += x;
        this.yMin += y;
        this.zMin += z;
        return this;
    }

    public SimpleCollisionBox expandMax(double x, double y, double z) {
        this.xMax += x;
        this.yMax += y;
        this.zMax += z;
        return this;
    }

    public SimpleCollisionBox expand(double x, double y, double z) {
        this.xMin -= x;
        this.yMin -= y;
        this.zMin -= z;
        this.xMax += x;
        this.yMax += y;
        this.zMax += z;
        return this;
    }

    public SimpleCollisionBox expand(double value) {
        this.xMin -= value;
        this.yMin -= value;
        this.zMin -= value;
        this.xMax += value;
        this.yMax += value;
        this.zMax += value;
        return this;
    }

    public Vector[] corners() {
        sort();
        Vector[] vectors = new Vector[8];
        vectors[0] = new Vector(xMin, yMin, zMin);
        vectors[1] = new Vector(xMin, yMin, zMax);
        vectors[2] = new Vector(xMax, yMin, zMin);
        vectors[3] = new Vector(xMax, yMin, zMax);
        vectors[4] = new Vector(xMin, yMax, zMin);
        vectors[5] = new Vector(xMin, yMax, zMax);
        vectors[6] = new Vector(xMax, yMax, zMin);
        vectors[7] = new Vector(xMax, yMax, zMax);
        return vectors;
    }


    public boolean isCollided(SimpleCollisionBox box) {
        return box.xMax > this.xMin && box.xMin < this.xMax
                && box.yMax > this.yMin && box.yMin < this.yMax
                && box.zMax > this.zMin && box.zMin < this.zMax;
    }

    public double distance(SimpleCollisionBox box) {
        return MathUtil.hypot(xMax - box.xMax, zMin - box.zMin);
    }
}
