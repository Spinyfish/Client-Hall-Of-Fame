package me.kansio.client.utils.block;

import me.kansio.client.utils.Util;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class BlockUtil extends Util {

    public static Block getBlockAt(BlockPos bpos) {
        return mc.theWorld.getBlockState(bpos).getBlock();
    }

}
