package dev.africa.pandaware.utils.player.path;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import lombok.experimental.UtilityClass;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@UtilityClass
public class PathUtils implements MinecraftInstance {

    public boolean canPassThrow(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();

        return (block.getBlockState().getBaseState() != null &&
                (block.getBlockState().getBaseState() == Material.air ||
                        block.getBlockState().getBaseState() == Material.plants ||
                        block.getBlockState().getBaseState() == Material.vine) ||
                block == Blocks.ladder || block == Blocks.water ||
                block == Blocks.flowing_water || block == Blocks.wall_sign ||
                block != Blocks.farmland || block == Blocks.standing_sign);
    }

    public List<Vec3> computePath(Vec3 topFrom, Vec3 to, double pointsDistance) {
        Block block = mc.theWorld.getBlockState(new BlockPos(topFrom)).getBlock();

        boolean sex = block instanceof BlockSlab || block instanceof BlockPane ||
                block instanceof BlockBed || block instanceof BlockWall ||
                block instanceof BlockCarpet || block instanceof BlockSkull ||
                block instanceof BlockEnchantmentTable || block instanceof BlockStairs ||
                block instanceof BlockFence || block instanceof BlockFenceGate ||
                block instanceof BlockWeb || block instanceof BlockChest ||
                block instanceof BlockEnderChest || block instanceof BlockDaylightDetector ||
                block instanceof BlockBrewingStand;

        if (!canPassThrow(new BlockPos(topFrom)) || sex) {
            topFrom = topFrom.addVector(0.0D, 1.0D, 0.0D);
        }

        if (block instanceof BlockSoulSand) {
            topFrom = topFrom.addVector(0.0D, 0.126D, 0.0D);
        }

        PathFinder pathfinder = new PathFinder(topFrom, to);
        pathfinder.compute();
        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        List<Vec3> path = new ArrayList<>();
        List<Vec3> pathFinderPath = pathfinder.getPath();

        for (Iterator<Vec3> iterator = pathFinderPath.iterator(); iterator.hasNext(); ++i) {
            Vec3 pathElm = iterator.next();
            if (i != 0 && i != pathFinderPath.size() - 1) {
                boolean canContinue = true;

                double rangeToTp = pointsDistance * 12;

                if (pathElm.squareDistanceTo(lastDashLoc) > rangeToTp) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.xCoord, pathElm.xCoord);
                    double smallY = Math.min(lastDashLoc.yCoord, pathElm.yCoord);
                    double smallZ = Math.min(lastDashLoc.zCoord, pathElm.zCoord);
                    double bigX = Math.max(lastDashLoc.xCoord, pathElm.xCoord);
                    double bigY = Math.max(lastDashLoc.yCoord, pathElm.yCoord);
                    double bigZ = Math.max(lastDashLoc.zCoord, pathElm.zCoord);

                    label54:
                    for (int x = (int) smallX; (double) x <= bigX; ++x) {
                        for (int y = (int) smallY; (double) y <= bigY; ++y) {
                            for (int z = (int) smallZ; (double) z <= bigZ; ++z) {
                                if (!PathFinder.checkPositionValidity(x, y, z)) {
                                    canContinue = false;
                                    break label54;
                                }
                            }
                        }
                    }
                }

                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5D, 0.0D, 0.5D));
                    lastDashLoc = lastLoc;
                }
            } else {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5D, 0.0D, 0.5D));
                }

                path.add(pathElm.addVector(0.5D, 0.0D, 0.5D));
                lastDashLoc = pathElm;
            }

            lastLoc = pathElm;
        }

        return path;
    }
}

