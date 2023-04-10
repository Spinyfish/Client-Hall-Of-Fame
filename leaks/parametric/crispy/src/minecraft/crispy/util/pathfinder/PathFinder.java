package crispy.util.pathfinder;

import crispy.Crispy;
import crispy.features.hacks.impl.player.Nofall;
import crispy.util.player.BlockUtil;
import crispy.util.render.RenderUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PathFinder {
    protected final HashMap<PathPos, PathPos> prevPosMap = new HashMap<>();
    private final PathPos start;
    private final BlockPos goal;
    private final boolean immuneToFallDamage = Crispy.INSTANCE.getHackManager().getHack(Nofall.class).isEnabled();
    private final HashMap<PathPos, Float> costMap = new HashMap<>();
    private final PathQueue queue = new PathQueue();
    private final ArrayList<PathPos> path = new ArrayList<>();
    protected boolean fallingAllowed = true;
    protected boolean divingAllowed = true;
    protected PathPos current;
    protected int thinkSpeed = 1024;
    protected int thinkTime = 200;
    protected boolean done;
    protected boolean failed;
    private int iterations;

    public PathFinder(BlockPos goal) {
        if (Minecraft.getMinecraft().thePlayer.onGround)
            start = new PathPos(new BlockPos(Minecraft.getMinecraft().thePlayer.posX,
                    Minecraft.getMinecraft().thePlayer.posY + 0.5,
                    Minecraft.getMinecraft().thePlayer.posZ));
        else
            start = new PathPos(new BlockPos(Minecraft.getMinecraft().thePlayer.getPosition()));
        this.goal = goal;

        costMap.put(start, 0F);
        queue.add(start, getHeuristic(start));
    }

    public PathFinder(PathFinder pathFinder) {
        this(pathFinder.goal);
        thinkSpeed = pathFinder.thinkSpeed;
        thinkTime = pathFinder.thinkTime;
    }

    private float getHeuristic(BlockPos pos) {
        float dx = Math.abs(pos.getX() - goal.getX());
        float dy = Math.abs(pos.getY() - goal.getY());
        float dz = Math.abs(pos.getZ() - goal.getZ());
        return 1.001F * (dx + dy + dz - 0.5857864376269049F * Math.min(dx, dz));
    }

    public void think() {
        if (done)
            throw new IllegalStateException("Path was already found!");

        int i = 0;
        for (; i < thinkSpeed && !checkFailed(); i++) {
            // get next position from queue
            current = queue.poll();

            // check if path is found
            if (checkDone())
                return;


            // add neighbors to queue
            for (PathPos next : getNeighbors(current)) {
                // check cost
                float newCost = costMap.get(current) + getCost(current, next);
                if (costMap.containsKey(next) && costMap.get(next) <= newCost)
                    continue;

                // add to queue
                costMap.put(next, newCost);
                prevPosMap.put(next, current);
                queue.add(next, newCost + getHeuristic(next));

            }
        }

        iterations += i;
    }


    private ArrayList<PathPos> getNeighbors(PathPos pos) {
        ArrayList<PathPos> neighbors = new ArrayList<>();

        // abort if too far away
        if (Math.abs(start.getX() - pos.getX()) > 256
                || Math.abs(start.getZ() - pos.getZ()) > 256)
            return neighbors;

        // get all neighbors
        BlockPos north = pos.north();
        BlockPos east = pos.east();
        BlockPos south = pos.south();
        BlockPos west = pos.west();

        BlockPos northEast = north.east();
        BlockPos southEast = south.east();
        BlockPos southWest = south.west();
        BlockPos northWest = north.west();

        BlockPos up = pos.up();
        BlockPos down = pos.down();

        // flying
        boolean flying = canFlyAt(pos);
        // walking
        boolean onGround = canBeSolid(down);

        // player can move sideways if flying, standing on the ground, jumping,
        // or inside of a block that allows sideways movement (ladders, webs,
        // etc.)
        if (flying || onGround || pos.isJumping()
                || canMoveSidewaysInMidairAt(pos) || canClimbUpAt(pos.down())) {

            // north
            if (checkHorizontalMovement(pos, north))
                neighbors.add(new PathPos(north));

            // east
            if (checkHorizontalMovement(pos, east))
                neighbors.add(new PathPos(east));

            // south
            if (checkHorizontalMovement(pos, south))
                neighbors.add(new PathPos(south));

            // west
            if (checkHorizontalMovement(pos, west))
                neighbors.add(new PathPos(west));

            // north-east
            if (checkDiagonalMovement(pos, EnumFacing.NORTH, EnumFacing.EAST))
                neighbors.add(new PathPos(northEast));

            // south-east
            if (checkDiagonalMovement(pos, EnumFacing.SOUTH, EnumFacing.EAST))
                neighbors.add(new PathPos(southEast));

            // south-west
            if (checkDiagonalMovement(pos, EnumFacing.SOUTH, EnumFacing.WEST))
                neighbors.add(new PathPos(southWest));

            // north-west
            if (checkDiagonalMovement(pos, EnumFacing.NORTH, EnumFacing.WEST))
                neighbors.add(new PathPos(northWest));
        }

        // up
        if (pos.getY() < 256 && canGoThrough(up.up())
                && (flying || onGround || canClimbUpAt(pos))
                && (flying || canClimbUpAt(pos) || goal.equals(up)
                || canSafelyStandOn(north) || canSafelyStandOn(east)
                || canSafelyStandOn(south) || canSafelyStandOn(west))
                && (divingAllowed || BlockUtil.getState(up.up())
                .getMaterial() != Material.water))
            neighbors.add(new PathPos(up, onGround));

        // down
        if (pos.getY() > 0 && canGoThrough(down) && canGoAbove(down.down())
                && (flying || canFallBelow(pos)) && (divingAllowed
                || BlockUtil.getState(pos).getMaterial() != Material.water))
            neighbors.add(new PathPos(down));

        return neighbors;
    }

    private boolean canFlyAt(BlockPos pos) {
        return Minecraft.theWorld.getBlockState(pos).getBlock().getMaterial() == Material.water;
    }


    public PathProcessor getProcessor() {
        return new WalkPathProcessor(path);
    }

    public void setThinkSpeed(int thinkSpeed) {
        this.thinkSpeed = thinkSpeed;
    }

    public void setThinkTime(int thinkTime) {
        this.thinkTime = thinkTime;
    }

    public void setFallingAllowed(boolean fallingAllowed) {
        this.fallingAllowed = fallingAllowed;
    }

    public void setDivingAllowed(boolean divingAllowed) {
        this.divingAllowed = divingAllowed;
    }

    public boolean isPathStillValid(int index) {
        if (path.isEmpty())
            return false;

        // check player abilities

        // if index is zero, check if first pos is safe
        if (index == 0) {
            PathPos pos = path.get(0);
            if (!isPassable(pos) || !canFlyAt(pos) && !canGoThrough(pos.down())
                    && !canSafelyStandOn(pos.down()))
                return false;
        }

        // check path
        for (int i = Math.max(1, index); i < path.size(); i++)
            if (!getNeighbors(path.get(i - 1)).contains(path.get(i)))
                return false;

        return true;
    }

    private boolean checkHorizontalMovement(BlockPos current, BlockPos next) {
        return isPassable(next) && (canFlyAt(current) || canGoThrough(next.down())
                || canSafelyStandOn(next.down()));
    }

    private boolean checkDiagonalMovement(BlockPos current,
                                          EnumFacing direction1, EnumFacing direction2) {
        BlockPos horizontal1 = current.offset(direction1);
        BlockPos horizontal2 = current.offset(direction2);
        BlockPos next = horizontal1.offset(direction2);

        return isPassable(horizontal1) && isPassable(horizontal2)
                && checkHorizontalMovement(current, next);
    }

    protected boolean isPassable(BlockPos pos) {
        return canGoThrough(pos) && canGoThrough(pos.up())
                && canGoAbove(pos.down()) && (divingAllowed || BlockUtil
                .getState(pos.up()).getMaterial() != Material.water);
    }

    private boolean canSafelyStandOn(BlockPos pos) {
        // check if solid
        Material material = BlockUtil.getState(pos).getMaterial();
        if (!canBeSolid(pos))
            return false;

        // check if safe
        return material != Material.cactus && material != Material.lava;
    }

    private boolean canGoThrough(BlockPos pos) {
        // check if loaded

        if (!Minecraft.theWorld.isChunkLoaded(pos.getX(), pos.getZ(), false))
            return false;

        // check if solid
        Material material = BlockUtil.getState(pos).getMaterial();
        Block block = BlockUtil.getState(pos);
        if (material.blocksMovement() && !(block instanceof BlockSign))
            return false;

        // check if trapped
        if (block instanceof BlockTripWire
                || block instanceof BlockPressurePlate)
            return false;

        // check if safe
        return material != Material.lava && material != Material.fire;
    }

    private boolean canGoAbove(BlockPos pos) {
        // check for fences, etc.
        Block block = BlockUtil.getState(pos);
        return !(block instanceof BlockFence) && !(block instanceof BlockWall)
                && !(block instanceof BlockFenceGate);
    }

    private boolean canClimbUpAt(BlockPos pos) {
        // check if this block works for climbing
        Block block = BlockUtil.getState(pos);
        if (!(block instanceof BlockLadder)
                && !(block instanceof BlockVine))
            return false;

        // check if any adjacent block is solid
        BlockPos up = pos.up();
        return canBeSolid(pos.north()) || canBeSolid(pos.east())
                || canBeSolid(pos.south()) || canBeSolid(pos.west())
                || canBeSolid(up.north()) || canBeSolid(up.east())
                || canBeSolid(up.south()) || canBeSolid(up.west());
    }
    public void renderPath(boolean debugMode, boolean depthTest)
    {
        // GL settings
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        if(!depthTest)
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);

        GL11.glPushMatrix();
        GL11.glTranslated(0.5, 0.5, 0.5);

        if(debugMode)
        {
            int renderedThings = 0;

            // queue (yellow)
            GL11.glLineWidth(2);
            GL11.glColor4f(1, 1, 0, 0.75F);
            for(PathPos element : queue.toArray())
            {
                if(renderedThings >= 5000)
                    break;

                PathRenderer.renderNode(element);
                renderedThings++;
            }

            // processed (red)
            GL11.glLineWidth(2);
            for(Map.Entry<PathPos, PathPos> entry : prevPosMap.entrySet())
            {
                if(renderedThings >= 5000)
                    break;

                if(entry.getKey().isJumping())
                    GL11.glColor4f(1, 0, 1, 0.75F);
                else
                    GL11.glColor4f(1, 0, 0, 0.75F);

                PathRenderer.renderArrow(entry.getValue(), entry.getKey());
                renderedThings++;
            }
        }

        // path (blue)
        if(debugMode)
        {
            GL11.glLineWidth(4);
            GL11.glColor4f(0, 0, 1, 0.75F);
        }else
        {
            GL11.glLineWidth(2);
            GL11.glColor4f(0, 1, 0, 0.75F);
        }
        for(int i = 0; i < path.size() - 1; i++)
            PathRenderer.renderArrow(path.get(i), path.get(i + 1));

        GL11.glPopMatrix();

        // GL resets
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    public ArrayList<PathPos> formatPath() {
        if (!done && !failed)
            throw new IllegalStateException("No path found!");
        if (!path.isEmpty())
            throw new IllegalStateException("Path was already formatted!");

        // get last position
        PathPos pos;
        if (!failed)
            pos = current;
        else {
            pos = start;
            for (PathPos next : prevPosMap.keySet())
                if (getHeuristic(next) < getHeuristic(pos)
                        && (canFlyAt(next) || canBeSolid(next.down())))
                    pos = next;
        }

        // get positions
        while (pos != null) {
            path.add(pos);
            pos = prevPosMap.get(pos);
        }

        // reverse path
        Collections.reverse(path);

        return path;
    }

    public PathPos getCurrentPos() {
        return current;
    }

    public BlockPos getGoal() {
        return goal;

    }

    public int countProcessedBlocks() {
        return prevPosMap.keySet().size();
    }

    public int getQueueSize() {
        return queue.size();
    }

    public float getCost(BlockPos pos) {
        return costMap.get(pos);
    }

    public boolean isDone() {
        return done;
    }

    private boolean canMoveSidewaysInMidairAt(BlockPos pos) {
        // check feet
        Block blockFeet = BlockUtil.getState(pos);
        if (BlockUtil.getState(pos).getMaterial().isLiquid()
                || blockFeet instanceof BlockLadder
                || blockFeet instanceof BlockVine
                || blockFeet instanceof BlockWeb)
            return true;

        // check head
        Block blockHead = BlockUtil.getState(pos.up());
        return BlockUtil.getState(pos.up()).getMaterial().isLiquid()
                || blockHead instanceof BlockWeb;
    }

    protected boolean checkDone() {
        return done = goal.equals(current);
    }

    private boolean checkFailed() {
        return failed = queue.isEmpty() || iterations >= thinkSpeed * thinkTime;
    }


    public boolean isFailed() {
        return failed;
    }

    protected boolean canBeSolid(BlockPos pos) {
        Material material = BlockUtil.getState(pos).getMaterial();
        Block block = BlockUtil.getState(pos);
        return material.blocksMovement()
                && !(block instanceof BlockSign)
                || block instanceof BlockLadder;
    }

    private float getCost(BlockPos current, BlockPos next) {
        float[] costs = {0.5F, 0.5F};
        BlockPos[] positions = new BlockPos[]{current, next};

        for (int i = 0; i < positions.length; i++) {
            Material material = BlockUtil.getState(positions[i]).getMaterial();

            // liquids
            if (material == Material.water)
                costs[i] *= 1.3164437838225804F;

            // soul sand
            if (!canFlyAt(positions[i]) && BlockUtil.getState
                    (positions[i].down()) instanceof BlockSoulSand)
                costs[i] *= 2.5F;
        }

        float cost = costs[0] + costs[1];

        // diagonal movement
        if (current.getX() != next.getX() && current.getZ() != next.getZ())
            cost *= 1.4142135623730951F;

        return cost;
    }

    private boolean canFallBelow(PathPos pos) {
        // check if player can keep falling
        BlockPos down2 = pos.down(2);
        if (fallingAllowed && canGoThrough(down2))
            return true;

        // check if player can stand below
        if (!canSafelyStandOn(down2))
            return false;

        // check if fall damage is off
        if (immuneToFallDamage && fallingAllowed)
            return true;

        // check if fall ends with slime block
        if (BlockUtil.getState(down2) instanceof BlockSlime && fallingAllowed)
            return true;

        // check fall damage
        BlockPos prevPos = pos;
        for (int i = 0; i <= (fallingAllowed ? 3 : 1); i++) {
            // check if prevPos does not exist, meaning that the pathfinding
            // started during the fall and fall damage should be ignored because
            // it cannot be prevented
            if (prevPos == null)
                return true;

            // check if point is not part of this fall, meaning that the fall is
            // too short to cause any damage
            if (!pos.up(i).equals(prevPos))
                return true;

            // check if block resets fall damage
            Block prevBlock = BlockUtil.getState(prevPos);
            BlockState prevState = Minecraft.theWorld.getBlockState(prevPos).getBlock().getBlockState();
            if (prevState.getBlock().getMaterial() == Material.water
                    || prevBlock instanceof BlockLadder
                    || prevBlock instanceof BlockVine
                    || prevBlock instanceof BlockWeb)
                return true;

            prevPos = prevPosMap.get(prevPos);
        }

        return false;
    }


}
