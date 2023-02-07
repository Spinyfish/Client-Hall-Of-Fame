package org.crystalpvp.anticheat.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.crystalpvp.anticheat.data.location.CrystalLocation;


public class Cuboid {
    private double x1;
    private double x2;
    private double y1;
    private double y2;
    private double z1;
    private double z2;

    public Cuboid(CrystalLocation crystalLocation) {
        this(crystalLocation.getX(), crystalLocation.getY(), crystalLocation.getZ());
    }

    public Cuboid(double x, double y, double z) {
        this(x, x, y, y, z, z);
    }

    public Cuboid add(Cuboid other) {
        this.x1 += other.x1;
        this.x2 += other.x2;
        this.y1 += other.y1;
        this.y2 += other.y2;
        this.z1 += other.z1;
        this.z2 += other.z2;
        return this;
    }

    public Cuboid move(double x, double y, double z) {
        this.x1 += x;
        this.x2 += x;
        this.y1 += y;
        this.y2 += y;
        this.z1 += z;
        this.z2 += z;
        return this;
    }

    public Cuboid shrink(double x, double y, double z) {
        this.x1 += x;
        this.x2 -= x;
        this.y1 += y;
        this.y2 -= y;
        this.z1 += z;
        this.z2 -= z;
        return this;
    }

    public Cuboid expand(double x, double y, double z) {
        this.x1 -= x;
        this.x2 += x;
        this.y1 -= y;
        this.y2 += y;
        this.z1 -= z;
        this.z2 += z;
        return this;
    }

    public List<Block> getBlocks(World world) {
        int x1 = (int)Math.floor(this.x1);
        int x2 = (int)Math.ceil(this.x2);
        int y1 = (int)Math.floor(this.y1);
        int y2 = (int)Math.ceil(this.y2);
        int z1 = (int)Math.floor(this.z1);
        int z2 = (int)Math.ceil(this.z2);
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(world.getBlockAt(x1, y1, z1));
        for (int x = x1; x < x2; ++x) {
            for (int y = y1; y < y2; ++y) {
                for (int z = z1; z < z2; ++z) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public boolean phase(Cuboid cuboid) {
        return this.x1 <= cuboid.x1 && this.x2 >= cuboid.x2 || this.z1 <= cuboid.z1 && this.z2 >= cuboid.z2;
    }

    public boolean contains(CrystalLocation crystalLocation) {
        return this.x1 <= crystalLocation.getX() && this.x2 >= crystalLocation.getX() && this.y1 <= crystalLocation.getY() && this.y2 >= crystalLocation.getY() && this.z1 <= crystalLocation.getZ() && this.z2 >= crystalLocation.getZ();
    }

    public boolean containsXZ(double x, double z) {
        return this.x1 <= x && this.x2 >= x && this.z1 <= z && this.z2 >= z;
    }

    public double distanceXZ(double x, double z) {
        double dx = Math.min(Math.pow(x - this.x1, 2.0), Math.pow(x - this.x2, 2.0));
        double dz = Math.min(Math.pow(z - this.z1, 2.0), Math.pow(z - this.z2, 2.0));
        return MathHelper.sqrt_double(dx + dz);
    }

    public Cuboid combine(Cuboid other) {
        return new Cuboid(Math.min(this.x1, other.x1), Math.max(this.x2, other.x2), Math.min(this.y1, other.y1), Math.max(this.y2, other.y2), Math.min(this.z1, other.z1), Math.max(this.z2, other.z2));
    }

    public static boolean checkBlocks(Collection<Block> blocks, Predicate<Material> predicate) {
        Iterator<Block> var2 = blocks.iterator();
        do {
            if (var2.hasNext()) continue;
            return true;
        } while (predicate.test((var2.next()).getType()));
        return false;
    }

    public boolean checkBlocks(World world, Predicate<Material> predicate) {
        return Cuboid.checkBlocks(this.getBlocks(world), predicate);
    }

    public double cX() {
        return (this.x1 + this.x2) * 0.5;
    }

    public double cY() {
        return (this.y1 + this.y2) * 0.5;
    }

    public double cZ() {
        return (this.z1 + this.z2) * 0.5;
    }

    public Cuboid(double x1, double x2, double y1, double y2, double z1, double z2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z1 = z1;
        this.z2 = z2;
    }

    public double getX1() {
        return this.x1;
    }

    public double getX2() {
        return this.x2;
    }

    public double getY1() {
        return this.y1;
    }

    public double getY2() {
        return this.y2;
    }

    public double getZ1() {
        return this.z1;
    }

    public double getZ2() {
        return this.z2;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public void setZ1(double z1) {
        this.z1 = z1;
    }

    public void setZ2(double z2) {
        this.z2 = z2;
    }
}
