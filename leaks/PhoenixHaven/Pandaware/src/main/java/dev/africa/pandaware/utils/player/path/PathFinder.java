package dev.africa.pandaware.utils.player.path;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Getter
public class PathFinder implements MinecraftInstance {
    private final Vec3[] flatCardinalDirections = new Vec3[]{
            new Vec3(1.0D, 0.0D, 0.0D),
            new Vec3(-1.0D, 0.0D, 0.0D),
            new Vec3(0.0D, 0.0D, 1.0D),
            new Vec3(0.0D, 0.0D, -1.0D)
    };

    private final Vec3 startVec3;
    private final Vec3 endVec3;
    private final List<Hub> hubs = new ArrayList<>();
    private final List<Hub> hubsToWork = new ArrayList<>();
    private List<Vec3> path = new ArrayList<>();

    public PathFinder(Vec3 startVec3, Vec3 endVec3) {
        this.startVec3 = startVec3.addVector(0.0D, 0.0D, 0.0D).floor();
        this.endVec3 = endVec3.addVector(0.0D, 0.0D, 0.0D).floor();
    }

    boolean checkPositionValidity(Vec3 loc) {
        return checkPositionValidity((int) loc.xCoord, (int) loc.yCoord, (int) loc.zCoord);
    }

    static boolean checkPositionValidity(int x, int y, int z) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);

        if (isBlockSolid(block1) || isBlockSolid(block2)) {
            return false;
        }

        isBlockSolid(block3);

        return isSafeToWalkOn(block3);
    }

    static boolean isBlockSolid(BlockPos block) {
        Block block1 = mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock();

        return block1.isSolidFullCube()
                || block1 instanceof BlockSlab
                || block1 instanceof BlockEnchantmentTable
                || block1 instanceof BlockStairs
                || block1 instanceof BlockCactus
                || block1 instanceof BlockChest
                || block1 instanceof BlockEnderChest
                || block1 instanceof BlockSkull
                || block1 instanceof BlockPane
                || block1 instanceof BlockFence
                || block1 instanceof BlockWall
                || block1 instanceof BlockGlass
                || block1 instanceof BlockPistonBase
                || block1 instanceof BlockPistonExtension
                || block1 instanceof BlockAnvil
                || block1 instanceof BlockFenceGate
                || block1 instanceof BlockPistonMoving
                || block1 instanceof BlockStainedGlass
                || block1 instanceof BlockTrapDoor
                || block1 instanceof BlockBed
                || block1 instanceof BlockBeacon
                || block1 instanceof BlockWeb
                || block1 instanceof BlockCarpet
                || block1 instanceof BlockBarrier
                || block1 instanceof BlockSnow
                || block1 instanceof BlockDaylightDetector
                || block1 instanceof BlockBrewingStand
                || block1 instanceof BlockCake
                || block1 instanceof BlockFire;
    }

    static boolean isSafeToWalkOn(BlockPos block) {
        Block block1 = mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock();

        return !(block1 instanceof BlockFence)
                && !(block1 instanceof BlockWall);
    }

    public void compute() {
        this.path.clear();
        this.hubsToWork.clear();
        List<Vec3> initPath = new ArrayList<>();
        initPath.add(this.startVec3);
        this.hubsToWork.add(new Hub(this.startVec3, null, initPath,
                this.startVec3.squareDistanceTo(this.endVec3), 0.0D, 0.0D));

        label57:
        for (int i = 0; i < 1000; ++i) {
            this.hubsToWork.sort(new CompareHub());
            int j = 0;
            if (this.hubsToWork.size() == 0) {
                break;
            }

            Iterator<Hub> hubIterator = new ArrayList<>(this.hubsToWork).iterator();

            while (hubIterator.hasNext()) {
                Hub hub = hubIterator.next();

                ++j;
                if (j > 4) {
                    break;
                }

                this.hubsToWork.remove(hub);
                this.hubs.add(hub);
                Vec3[] vecArray;
                int length = (vecArray = flatCardinalDirections).length;

                Vec3 loc1;
                for (int i1 = 0; i1 < length; ++i1) {
                    loc1 = vecArray[i1];
                    Vec3 loc = hub.getLoc().add(loc1).floor();
                    if (this.checkPositionValidity(loc) && this.addHub(hub, loc)) {
                        break label57;
                    }
                }

                loc1 = hub.getLoc().addVector(0.0D, 1.0D, 0.0D).floor();
                if (this.checkPositionValidity(loc1) && this.addHub(hub, loc1)) {
                    break label57;
                }

                Vec3 loc2 = hub.getLoc().addVector(0.0D, -1.0D, 0.0D).floor();
                if (this.checkPositionValidity(loc2) && this.addHub(hub, loc2)) {
                    break label57;
                }
            }
        }

        this.hubs.sort(new CompareHub());

        this.path = this.hubs.get(0).getPath();
    }

    public Hub isHubExisting(Vec3 loc) {
        Iterator<Hub> hubIterator = this.hubs.iterator();

        Hub hub;
        do {
            if (!hubIterator.hasNext()) {
                hubIterator = this.hubsToWork.iterator();

                do {
                    if (!hubIterator.hasNext()) {
                        return null;
                    }

                    hub = hubIterator.next();
                } while (hub.getLoc().xCoord != loc.zCoord
                        || hub.getLoc().yCoord != loc.yCoord || hub.getLoc().zCoord != loc.zCoord);

                return hub;
            }

            hub = hubIterator.next();
        } while (hub.getLoc().xCoord != loc.xCoord ||
                hub.getLoc().yCoord != loc.yCoord || hub.getLoc().zCoord != loc.zCoord);

        return hub;
    }

    boolean addHub(Hub parent, Vec3 loc) {
        Hub existingHub = this.isHubExisting(loc);
        double totalCost = 0.0;

        if (parent != null) {
            totalCost = parent.getTotalCost();
        }

        if (parent != null) {
            List<Vec3> path;
            if (existingHub == null) {
                double minDistanceSquared = 9.0D;
                if (loc.xCoord == this.endVec3.xCoord && loc.yCoord == this.endVec3.yCoord && loc.zCoord
                        == this.endVec3.zCoord || loc.squareDistanceTo(this.endVec3) <= minDistanceSquared) {
                    this.path.clear();
                    this.path = parent.getPath();
                    this.path.add(loc);
                    return true;
                }

                path = new ArrayList<>(parent.getPath());
                path.add(loc);
                this.hubsToWork.add(new Hub(loc, parent, path, loc.squareDistanceTo(this.endVec3), 0.0, totalCost));
            } else if (existingHub.getCost() > 0.0) {
                path = new ArrayList<>(parent.getPath());
                path.add(loc);
                existingHub.setLoc(loc);
                existingHub.setParent(parent);
                existingHub.setPath(path);
                existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(this.endVec3));
                existingHub.setCost(0.0);
                existingHub.setTotalCost(totalCost);
            }
        }

        return false;
    }

    static class CompareHub implements Comparator<Hub> {
        public int compare(Hub o1, Hub o2) {
            return (int) (o1.getSquareDistanceToFromTarget() + o1.getTotalCost()
                    - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost()));
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    static class Hub {
        private Vec3 loc;
        private Hub parent;
        private List<Vec3> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;
    }
}