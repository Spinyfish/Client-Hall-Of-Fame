package tech.dort.dortware.impl.utils.pathfinding;

import net.minecraft.block.Block;

public class PathFinderBlockInfo {

    private final double height;
    private final Block block;

    public PathFinderBlockInfo(double height, Block block) {
        this.height = height;
        this.block = block;
    }

    public double getHeight() {
        return height;
    }

    public Block getBlock() {
        return block;
    }
}
