package me.uncodable.srt.impl.utils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class BlockUtils {
   private static final Minecraft MC = Minecraft.getMinecraft();
   private static final BlockPos pos = new BlockPos(MC.thePlayer.posX, MC.thePlayer.getEntityBoundingBox().minY - 0.5, MC.thePlayer.posZ);

   public static Block getBlockUnderneath() {
      return MC.theWorld.getBlockState(pos).getBlock();
   }
}
