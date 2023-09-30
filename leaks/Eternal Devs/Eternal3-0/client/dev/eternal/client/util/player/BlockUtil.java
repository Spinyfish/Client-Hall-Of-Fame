package dev.eternal.client.util.player;

import dev.eternal.client.util.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

@UtilityClass
public class BlockUtil implements IMinecraft {

  public Block getBlockAtPos(BlockPos blockPosition) {
    IBlockState s = mc.theWorld.getBlockState(blockPosition);
    return s.getBlock();
  }

}
