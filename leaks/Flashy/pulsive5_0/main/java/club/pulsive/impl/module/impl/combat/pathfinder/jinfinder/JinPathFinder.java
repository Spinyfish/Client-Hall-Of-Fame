package club.pulsive.impl.module.impl.combat.pathfinder.jinfinder;

import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.player.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.ArrayList;

public class JinPathFinder {
    private final ArrayList<Path> paths = new ArrayList<>();
    private Vec3 destination = new Vec3(0, 0, 0);

    private Path destinationPath;
    private boolean foundDestination;
    private float startX, startY, startZ;
    private double closestDistance, lastClosestDistance, distanceProgressed, positiveProgressionRow;
    private final ArrayList<Block> nonCollisionBlocks = new ArrayList() {{
        add(Blocks.air);
        add(Blocks.tallgrass);
        add(Blocks.lava);
        add(Blocks.flowing_water);
        add(Blocks.flowing_lava);
        add(Blocks.red_flower);
        add(Blocks.yellow_flower);
        add(Blocks.standing_sign);
        add(Blocks.wall_sign);
        add(Blocks.stone_pressure_plate);
        add(Blocks.wooden_pressure_plate);
        add(Blocks.heavy_weighted_pressure_plate);
        add(Blocks.light_weighted_pressure_plate);
        add(Blocks.stone_button);
        add(Blocks.wooden_button);
        add(Blocks.ladder);
    }};

    private Prioritise prioritise;
    private Mode mode;

    public ArrayList<Vec3> getPath(final Vec3 startPos, final Vec3 endPos, final boolean optimiseTeleports, final Prioritise prioritise, final Mode mode) {

        //This vec3 is the location the pathfinder wants to go to
        destination = endPos;
        this.mode = mode;
        foundDestination = false;
        destinationPath = null;
        this.prioritise = prioritise;

        startX = (float) (MathHelper.floor_double(startPos.xCoord) + 0.5);
        startY = (float) MathHelper.floor_double(startPos.yCoord);
        startZ = (float) (MathHelper.floor_double(startPos.zCoord) + 0.5);

        paths.clear();
        paths.add(new Path(
                new ArrayList<Vec3>() {{
                    //Setting starting location, for now its using the players position
                    add(new Vec3(startX, startY, startZ));
                }},
                destination,
                prioritise
        ));

        final long time = System.nanoTime() / 1000000L;
        final long l = System.currentTimeMillis();
        int amount = 0;
        while (!foundDestination) {
            amount++;
            lastClosestDistance = closestDistance;
            closestDistance = closestDistance();

            distanceProgressed = lastClosestDistance - closestDistance;

            if (distanceProgressed == 1) {
                positiveProgressionRow++;
            } else {
                positiveProgressionRow = 0;
            }

            if (amount > 5000) return null;

            step();
            if (System.nanoTime() / 1000000L - time > 1000) return null;
        }

        paths.clear();

        if (optimiseTeleports) setOptimiseTeleports();

        //Rise.addChatMessage(System.currentTimeMillis() - l + "");

        return destinationPath.getPoints();
    }

    private void setOptimiseTeleports() {
        Vec3 lastPoint = null;
        Vec3 offset = null, lastOffset;

        final ArrayList<Vec3> destinationPathClone = new ArrayList<>(destinationPath.getPoints());
        int row = 0;

        for (final Vec3 point : destinationPathClone) {
            if (lastPoint != null) {
                lastOffset = offset;
                offset = new Vec3(lastPoint.xCoord - point.xCoord, lastPoint.yCoord - point.yCoord, lastPoint.zCoord - point.zCoord);

                if (lastOffset != null) {
                    if (pointsEqual(lastOffset, offset) && row < 6) {
                        destinationPath.getPoints().remove(lastPoint);
                        row++;
                        lastPoint = point;
                        continue;
                    }
                }

                row = 0;
            }

            lastPoint = point;
        }
    }

    private void step() {
        final ArrayList<Path> clonedPaths = new ArrayList<>(paths);

        double averageDistance = combinedDistance() / paths.size();

        if (distanceProgressed < 0) {
            averageDistance += averageDistance / 2;
        }

        int positiveProgressionRowToDeleteExtraPoints = 0;
        switch (this.mode) {
            case NORMAL:
                positiveProgressionRowToDeleteExtraPoints = 100;
                break;
            case LEGIT:
                positiveProgressionRowToDeleteExtraPoints = 3;
                break;
        }

        if (positiveProgressionRow >= positiveProgressionRowToDeleteExtraPoints) {
            averageDistance -= averageDistance / 2;

            for (final Path path : clonedPaths) {
                if (path.getDistanceFromDestination() != closestDistance) {
                    paths.remove(path);
                }
            }
        }

        for (final Path path : clonedPaths) {
            if (path.getDistanceFromDestination() > averageDistance) {
                continue;
            }

            final ArrayList<Vec3> points = path.getPoints();
            final Vec3 lastPoint = points.get(points.size() - 1);

            path.setBeingUsed(false);

            //Adding new paths to all directions
            for (int x = -1; x <= 1; x += 2) {
                addPath(x, 0, 0, points, lastPoint, path);
            }

            for (int y = -1; y <= 1; y += 2) {
                addPath(0, y, 0, points, lastPoint, path);
            }

            for (int z = -1; z <= 1; z += 2) {
                addPath(0, 0, z, points, lastPoint, path);
            }

            //Removes redundant paths
            if (!path.isBeingUsed()) {
                paths.remove(path);
            }
        }
    }

    private double combinedDistance() {
        double distance = 0;
        for (final Path path : paths) {
            distance += path.getDistanceFromDestination();
        }
        return distance;
    }

    private double closestDistance() {
        double distance = Double.MAX_VALUE;
        for (final Path path : paths) {
            if (path.getDistanceFromDestination() < distance) distance = path.getDistanceFromDestination();
        }
        return distance;
    }

    private void addPath(final int offsetX, final int offsetY, final int offsetZ, final ArrayList<Vec3> points, final Vec3 lastPoint, final Path testingPath) {
        if (foundDestination) return;
        final ArrayList<Vec3> clonedPoints = new ArrayList<>(points);
        final Vec3 newPoint = new Vec3(lastPoint.xCoord + offsetX, lastPoint.yCoord + offsetY, lastPoint.zCoord + offsetZ);

        //Cancels new point placement if it doesn't follow required rules
        if (!rules(newPoint, lastPoint)) return;

        checkPathReachedDestination(newPoint, testingPath);

        clonedPoints.add(newPoint);

        paths.add(new Path(
                clonedPoints,
                destination,
                prioritise
        ));

        testingPath.setBeingUsed(true);
    }

    //Checks if the new point overlaps with the destination then it sets it as the path needed to get to the destination
    private void checkPathReachedDestination(final Vec3 nextPoint, final Path testingPath) {
        if (pointsEqual(nextPoint, destination)) {
            foundDestination = true;
            destinationPath = testingPath;
        }
    }

    private boolean rules(final Vec3 possibleNewPoint, final Vec3 lastPoint) {
        final Block pointsBlock = BlockUtils.getBlock(possibleNewPoint.xCoord, possibleNewPoint.yCoord, possibleNewPoint.zCoord);

        if (!nonCollisionBlocks.contains(pointsBlock))
            return false;

        final Block pointsBlockAbove = BlockUtils.getBlock(possibleNewPoint.xCoord, possibleNewPoint.yCoord + 1, possibleNewPoint.zCoord);

        if (!nonCollisionBlocks.contains(pointsBlockAbove))
            return false;

        switch (this.mode) {
            case LEGIT:
                final Block pointBlock = BlockUtils.getBlock(possibleNewPoint.xCoord, possibleNewPoint.yCoord, possibleNewPoint.zCoord);
                final Block lastPointsBlockBelow = BlockUtils.getBlock(lastPoint.xCoord, lastPoint.yCoord - 1, lastPoint.zCoord);
                final Block lastPointsBlockBelowBelow = BlockUtils.getBlock(lastPoint.xCoord, lastPoint.yCoord - 2, lastPoint.zCoord);

                if (nonCollisionBlocks.contains(lastPointsBlockBelowBelow)
                        && nonCollisionBlocks.contains(lastPointsBlockBelow)
                        && !pointsEqual(possibleNewPoint.addVector(0, 1, 0), lastPoint)
                        && !pointBlock.equals(Blocks.ladder)) {
                    if (pointBlock.equals(Blocks.ladder)) Logger.print("" + ApacheMath.random());
                    return false;
                }
                break;
        }

        //Returns false if there is already a point there
        for (final Path path : paths) {
            for (final Vec3 point : path.getPoints()) {
                if (pointsEqual(point, possibleNewPoint)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean pointsEqual(final Vec3 point1, final Vec3 point2) {
        return ApacheMath.abs(point1.xCoord - point2.xCoord) <= 0.5 && ApacheMath.abs(point1.yCoord - point2.yCoord) <= 0.5 && ApacheMath.abs(point1.zCoord - point2.zCoord) <= 0.5;
    }
}