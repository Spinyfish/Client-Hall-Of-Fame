package de.lirium.base.chunk;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChunkSystem {
    public static final HashMap<Block, List<BlockPos>> blocks = new HashMap<>();
    private static final Map<Block, List<BlockPos>> toRemove = new HashMap<>();

    public static void addBlock(Block block, BlockPos pos) {
        if (!blocks.containsKey(block)) {
            blocks.put(block, new ArrayList<>());
        }
        try {
            if (block != null && pos != null) {
                if (blocks.containsKey(block) && blocks.get(block) != null)
                    blocks.get(block).add(pos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeBlock(Block block, BlockPos pos) {
        if (blocks.containsKey(block)) {
            blocks.get(block).remove(pos);
        }
    }

    /*public static void removeBlock(BlockPos pos) {


        if (pos != null && !blocks.isEmpty()) {
            blocksToRemove.clear();
            blocksToRemove.addAll(blocks.keySet());
            for (int i = 0; i < blocksToRemove.size(); i++) {
                if (blocks.containsKey(blocksToRemove.get(i))) {
                    System.out.println(blocksToRemove.size() + " " + blocks.size() + " " + blocks.get(blocksToRemove.get(i)).size() + " " + blocksToRemove.get(i) + " ");
                    blocks.get(blocksToRemove.get(i)).remove(pos);
                }
            }
        }
    }*/

    public static List<BlockPos> getPositions(Block block) {
        return blocks.getOrDefault(block, new ArrayList<>());
    }
}
