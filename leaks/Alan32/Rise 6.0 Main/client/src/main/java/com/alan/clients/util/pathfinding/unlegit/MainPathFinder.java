package com.alan.clients.util.pathfinding.unlegit;

import com.alan.clients.util.interfaces.InstanceAccess;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Auth, FinalException
 * @since 28/06/2022
 */

@UtilityClass
public class MainPathFinder implements InstanceAccess {

    public List<Vec3> computePath(final Vec3 from, final Vec3 to, final boolean exact) {
        return computePath(from, to, exact, 9.5);
    }

    public List<Vec3> computePath(Vec3 from, final Vec3 to, final boolean exact, final double step) {
        final BlockPos blockPos = new BlockPos(from.mc());
        final IBlockState state = mc.theWorld.getBlockState(blockPos);

        if (state == null) {
            return null;
        }

        final Block block = state.getBlock();

        if (block == null) {
            return null;
        }

        if (!canPassThroughMaterial(block)) {
            from = from.addVector(0, 1, 0);
        }

        final PathFinder pathFinder = new PathFinder(from, to);
        pathFinder.compute();

        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        final ArrayList<Vec3> path = new ArrayList<>();
        final ArrayList<Vec3> pathFinderPath = pathFinder.getPath();
        for (final Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                path.add(pathElm.addVector(0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > step * step) {
                    canContinue = false;
                } else {
                    final double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    final double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    final double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    final double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    final double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    final double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!PathFinder.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }
                }

                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }

        if (exact) {
            path.add(to);
        }

        return path;
    }

    private boolean canPassThroughMaterial(final Block block) {
        final Material material = block.getMaterial();

        return material == Material.air || material == Material.plants || material == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }
}
