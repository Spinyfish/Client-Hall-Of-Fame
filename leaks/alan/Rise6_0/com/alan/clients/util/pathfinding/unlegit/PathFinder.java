package com.alan.clients.util.pathfinding.unlegit;

import com.alan.clients.util.player.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author FinalException
 */
public final class PathFinder {

    private final ArrayList<Hub> hubsToWork = new ArrayList<>();
    private ArrayList<Vec3> path = new ArrayList<>();
    private final ArrayList<Hub> hubs = new ArrayList<>();
    private final double minDistanceSquared = 9.5;
    private final boolean nearest = true;
    private final Vec3 startVec3;
    private final Vec3 endVec3;

    private static final Vec3[] flatCardinalDirections = {
            new Vec3(1, 0, 0),
            new Vec3(-1, 0, 0),
            new Vec3(0, 0, 1),
            new Vec3(0, 0, -1)
    };

    public PathFinder(final Vec3 startVec3, final Vec3 endVec3) {
        this.startVec3 = startVec3.addVector(0, 0, 0).floor();
        this.endVec3 = endVec3.addVector(0, 0, 0).floor();
    }

    public ArrayList<Vec3> getPath() {
        return path;
    }

    public void compute() {
        compute(1000, 4);
    }

    public void compute(final int loops, final int depth) {
        path.clear();
        hubsToWork.clear();
        final ArrayList<Vec3> initPath = new ArrayList<>();
        initPath.add(startVec3);
        hubsToWork.add(new Hub(startVec3, null, initPath, startVec3.squareDistanceTo(endVec3), 0, 0));
        search:
        for (int i = 0; i < loops; i++) {
            hubsToWork.sort(new CompareHub());
            int j = 0;
            if (hubsToWork.size() == 0) {
                break;
            }
            for (final Hub hub : new ArrayList<>(hubsToWork)) {
                j++;
                if (j > depth) {
                    break;
                } else {
                    hubsToWork.remove(hub);
                    hubs.add(hub);

                    for (final Vec3 direction : flatCardinalDirections) {
                        final Vec3 loc = hub.getLoc().add(direction).floor();
                        if (checkPositionValidity(loc, false)) {
                            if (addHub(hub, loc, 0)) {
                                break search;
                            }
                        }
                    }

                    final Vec3 loc1 = hub.getLoc().addVector(0, 1, 0).floor();
                    if (checkPositionValidity(loc1, false)) {
                        if (addHub(hub, loc1, 0)) {
                            break search;
                        }
                    }

                    final Vec3 loc2 = hub.getLoc().addVector(0, -1, 0).floor();
                    if (checkPositionValidity(loc2, false)) {
                        if (addHub(hub, loc2, 0)) {
                            break search;
                        }
                    }
                }
            }
        }
        if (nearest) {
            hubs.sort(new CompareHub());

            path = hubs.get(0).getPath();
        }
    }

    public static boolean checkPositionValidity(final Vec3 loc, final boolean checkGround) {
        return checkPositionValidity((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), checkGround);
    }

    public static boolean checkPositionValidity(final int x, final int y, final int z, final boolean checkGround) {
        final BlockPos block3 = new BlockPos(x, y - 1, z);

        return !isBlockSolid(new BlockPos(x, y, z)) && !isBlockSolid(new BlockPos(x, y + 1, z)) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
    }

    private static boolean isBlockSolid(final BlockPos blockPos) {
        final Block b = PlayerUtil.block(blockPos);

        return b.isFullBlock()
                || b instanceof BlockSlab
                || b instanceof BlockStairs
                || b instanceof BlockCactus
                || b instanceof BlockChest
                || b instanceof BlockEnderChest
                || b instanceof BlockSkull
                || b instanceof BlockPane
                || b instanceof BlockFence
                || b instanceof BlockWall
                || b instanceof BlockGlass
                || b instanceof BlockPistonBase
                || b instanceof BlockPistonExtension
                || b instanceof BlockPistonMoving
                || b instanceof BlockStainedGlass
                || b instanceof BlockTrapDoor
                || b instanceof BlockEndPortalFrame
                || b instanceof BlockEndPortal
                || b instanceof BlockBed
                || b instanceof BlockWeb
                || b instanceof BlockBarrier
                || b instanceof BlockLadder
                || b instanceof BlockLeaves
                || b instanceof BlockSnow
                || b instanceof BlockSnowBlock
                || b instanceof BlockCarpet
                || b instanceof BlockDoor
                || b instanceof BlockVine
                || b instanceof BlockLilyPad;
    }

    private static boolean isSafeToWalkOn(final BlockPos block) {
        final Block blockClazz = Minecraft.getMinecraft().theWorld.getBlockState(block).getBlock();

        return !(blockClazz instanceof BlockFence) &&
                !(blockClazz instanceof BlockWall);
    }

    public Hub isHubExisting(final Vec3 loc) {
        for (final Hub hub : hubs) {
            if (hub.getLoc().getX() == loc.getX() && hub.getLoc().getY() == loc.getY() && hub.getLoc().getZ() == loc.getZ()) {
                return hub;
            }
        }

        for (final Hub hub : hubsToWork) {
            if (hub.getLoc().getX() == loc.getX() && hub.getLoc().getY() == loc.getY() && hub.getLoc().getZ() == loc.getZ()) {
                return hub;
            }
        }
        return null;
    }

    public boolean addHub(final Hub parent, final Vec3 loc, final double cost) {
        final Hub existingHub = isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            if ((loc.getX() == endVec3.getX() && loc.getY() == endVec3.getY() && loc.getZ() == endVec3.getZ()) || (minDistanceSquared != 0 && loc.squareDistanceTo(endVec3) <= minDistanceSquared)) {
                if (parent != null) {
                    path.clear();
                    path = parent.getPath();
                    path.add(loc);

                    return true;
                } else {
                    return false;
                }
            } else {
                final ArrayList<Vec3> path = new ArrayList<>(parent.getPath());
                path.add(loc);
                hubsToWork.add(new Hub(loc, parent, path, loc.squareDistanceTo(endVec3), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            final ArrayList<Vec3> path = new ArrayList<>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(endVec3));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    @Getter
    @Setter
    private static class Hub {
        private Vec3 loc;
        private Hub parent;
        private ArrayList<Vec3> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(final Vec3 loc, final Hub parent, final ArrayList<Vec3> path, final double squareDistanceToFromTarget, final double cost, final double totalCost) {
            this.loc = loc;
            this.parent = parent;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }
    }

    public static class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(final Hub o1, final Hub o2) {
            return (int) (
                    (o1.getSquareDistanceToFromTarget() + o1.getTotalCost()) - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost())
            );
        }
    }
}