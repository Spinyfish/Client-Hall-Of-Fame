package org.crystalpvp.anticheat.data.player;


import org.crystalpvp.anticheat.api.util.Cuboid;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class DistanceData
{
    private final Cuboid hitbox;
    private final double x;
    private final double z;
    private final double y;
    private final double dist;

    public DistanceData(final Cuboid hitbox, final double x, final double z, final double y, final double dist) {
        this.hitbox = hitbox;
        this.x = x;
        this.z = z;
        this.y = y;
        this.dist = dist;
    }

    public Cuboid getHitbox() {
        return this.hitbox;
    }

    public double getX() {
        return this.x;
    }

    public double getZ() {
        return this.z;
    }

    public double getY() {
        return this.y;
    }

    public double getDist() {
        return this.dist;
    }
}

