package tech.dort.dortware.impl.utils.pathfinding;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DortPathFinder {
    private final Vec3 startVec3;
    private final Vec3 endVec3;
    private ArrayList<Vec3> path = new ArrayList();
    private final ArrayList<PathHub> pathHubs = new ArrayList();
    private final ArrayList<PathHub> workingPathHubList = new ArrayList();
    private final double minDistanceSquared = 5.0;
    private final boolean nearest = true;
    private static final Vec3[] directions = new Vec3[]{new Vec3(1.0, 0.0, 0.0), new Vec3(-1.0, 0.0, 0.0),
            new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, 0.0, -1.0)};

    public DortPathFinder(Vec3 startVec3, Vec3 endVec3) {
        this.startVec3 = startVec3.addVector(0.0, 0.0, 0.0).floor();
        this.endVec3 = endVec3.addVector(0.0, 0.0, 0.0).floor();
    }

    public ArrayList<Vec3> getPath() {
        return this.path;
    }

    public void compute() {
        this.compute(1000, 4);
    }

    public void compute(int loops, int depth) {
        this.path.clear();
        this.workingPathHubList.clear();
        ArrayList<Vec3> initPath = new ArrayList<>();
        initPath.add(this.startVec3);
        this.workingPathHubList
                .add(new PathHub(this.startVec3, null, initPath, this.startVec3.squareDistanceTo(this.endVec3), 0.0, 0.0));
        block0:
        for (int i = 0; i < loops; ++i) {
            Collections.sort(this.workingPathHubList, new CompareHub());
            int j = 0;
            if (this.workingPathHubList.size() == 0) {
                break;
            }
            for (PathHub pathHub : new ArrayList<>(this.workingPathHubList)) {
                Vec3 loc2;
                if (++j > depth) {
                    continue block0;
                }
                this.workingPathHubList.remove(pathHub);
                this.pathHubs.add(pathHub);
                for (Vec3 direction : directions) {
                    Vec3 loc = pathHub.getLoc().add(direction).floor();
                    if (DortPathFinder.isValid(loc, false) && this.putHub(pathHub, loc, 0.0)) {
                        break block0;
                    }
                }
                Vec3 loc1 = pathHub.getLoc().addVector(0.0, 1.0, 0.0).floor();
                if (DortPathFinder.isValid(loc1, false) && this.putHub(pathHub, loc1, 0.0)
                        || DortPathFinder
                        .isValid(loc2 = pathHub.getLoc().addVector(0.0, -1.0, 0.0).floor(), false)
                        && this.putHub(pathHub, loc2, 0.0)) {
                    break block0;
                }
            }
        }
        if (this.nearest) {
            Collections.sort(this.pathHubs, new CompareHub());
            this.path = this.pathHubs.get(0).getPathway();
        }
    }

    public static boolean isValid(Vec3 loc, boolean checkGround) {
        return DortPathFinder.isValid((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(),
                checkGround);
    }

    public static boolean isValid(int x, int y, int z, boolean checkGround) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);
        return !DortPathFinder.isNotPassable(block1) && !DortPathFinder.isNotPassable(block2)
                && (DortPathFinder.isNotPassable(block3) || !checkGround)
                && DortPathFinder.canWalkOn(block3);
    }

    private static boolean isNotPassable(BlockPos block) {
        return Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(), block.getZ()).isSolidFullCube()
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockSlab
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockStairs
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockCactus
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockChest
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockEnderChest
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockSkull
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockPane
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockFence
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockWall
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockGlass
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockPistonBase
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockPistonExtension
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockPistonMoving
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockStainedGlass
                || Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockTrapDoor;
    }

    private static boolean canWalkOn(BlockPos block) {
        return !(Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockFence)
                && !(Minecraft.getMinecraft().theWorld.getBlock(block.getX(), block.getY(),
                block.getZ()) instanceof BlockWall);
    }

    public PathHub doesHubExistAt(Vec3 loc) {
        for (PathHub pathHub : this.pathHubs) {
            if (pathHub.getLoc().getX() != loc.getX() || pathHub.getLoc().getY() != loc.getY()
                    || pathHub.getLoc().getZ() != loc.getZ()) {
                continue;
            }
            return pathHub;
        }
        for (PathHub pathHub : this.workingPathHubList) {
            if (pathHub.getLoc().getX() != loc.getX() || pathHub.getLoc().getY() != loc.getY()
                    || pathHub.getLoc().getZ() != loc.getZ()) {
                continue;
            }
            return pathHub;
        }
        return null;
    }

    public boolean putHub(PathHub parent, Vec3 loc, double cost) {
        PathHub existingPathHub = this.doesHubExistAt(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getMaxCost();
        }
        if (existingPathHub == null) {
            if (loc.getX() == this.endVec3.getX() && loc.getY() == this.endVec3.getY() && loc.getZ() == this.endVec3.getZ() || loc.squareDistanceTo(this.endVec3) <= this.minDistanceSquared) {
                this.path.clear();
                this.path = parent.getPathway();
                this.path.add(loc);
                return true;
            }
            ArrayList<Vec3> path = new ArrayList<>(parent.getPathway());
            path.add(loc);
            this.workingPathHubList.add(new PathHub(loc, parent, path, loc.squareDistanceTo(this.endVec3), cost, totalCost));
        } else if (existingPathHub.getCurrentCost() > cost) {
            ArrayList<Vec3> path = new ArrayList<>(parent.getPathway());
            path.add(loc);
            existingPathHub.setLoc(loc);
            existingPathHub.setParentPathHub(parent);
            existingPathHub.setPathway(path);
            existingPathHub.setSqDist(loc.squareDistanceTo(this.endVec3));
            existingPathHub.setCurrentCost(cost);
            existingPathHub.setMaxCost(totalCost);
        }
        return false;
    }

    public static boolean canPassThrough(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld
                .getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants
                || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water
                || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }

    public static ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
        if (!canPassThrough(new BlockPos(topFrom.mc()))) {
            topFrom = topFrom.addVector(0.0, 1.0, 0.0);
        }
        PathFinder pathfinder = new PathFinder(topFrom, to);
        pathfinder.compute();
        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        ArrayList<Vec3> path = new ArrayList<>();
        ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
        for (Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0.0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > 5 * 5) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    int x = (int) smallX;
                    block1:
                    while (x <= bigX) {
                        int y2 = (int) smallY;
                        while (y2 <= bigY) {
                            int z = (int) smallZ;
                            while (z <= bigZ) {
                                if (!isValid(x, y2, z, false)) {
                                    canContinue = false;
                                    break block1;
                                }
                                ++z;
                            }
                            ++y2;
                        }
                        ++x;
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            ++i;
        }
        return path;
    }

    public class CompareHub implements Comparator<PathHub> {
        @Override
        public int compare(PathHub o1, PathHub o2) {
            return (int) (o1.getSqDist() + o1.getMaxCost()
                    - (o2.getSqDist() + o2.getMaxCost()));
        }
    }
}
