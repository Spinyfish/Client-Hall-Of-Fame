package org.crystalpvp.anticheat.data.player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class ReachData
{
    private final PlayerData playerData;
    private final DistanceData distanceData;
    private final double movement;
    private final double horizontal;
    private final double extra;
    private final double vertical;
    private final double reach;

    public ReachData(final PlayerData playerData,  final DistanceData distanceData, final double movement, final double horizontal, final double extra, final double vertical, final double reach) {
        this.playerData = playerData;
        this.distanceData = distanceData;
        this.movement = movement;
        this.horizontal = horizontal;
        this.extra = extra;
        this.vertical = vertical;
        this.reach = reach;
    }

    public PlayerData getPlayerData() {
        return this.playerData;
    }

    public DistanceData getDistanceData() {
        return this.distanceData;
    }

    public double getMovement() {
        return this.movement;
    }

    public double getHorizontal() {
        return this.horizontal;
    }

    public double getExtra() {
        return this.extra;
    }

    public double getVertical() {
        return this.vertical;
    }

    public double getReach() {
        return this.reach;
    }
}
