package net.optifine.util;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class RenderChunkUtils {
   public static int getCountBlocks(RenderChunk renderChunk) {
      ExtendedBlockStorage[] aextendedblockstorage = renderChunk.getChunk().getBlockStorageArray();
      if (aextendedblockstorage == null) {
         return 0;
      } else {
         int i = renderChunk.getPosition().getY() >> 4;
         ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[i];
         return extendedblockstorage == null ? 0 : extendedblockstorage.getBlockRefCount();
      }
   }

   public static double getRelativeBufferSize(RenderChunk renderChunk) {
      int i = getCountBlocks(renderChunk);
      return getRelativeBufferSize(i);
   }

   public static double getRelativeBufferSize(int blockCount) {
      double d0 = (double)blockCount / 4096.0;
      d0 *= 0.995;
      double d1 = d0 * 2.0 - 1.0;
      d1 = MathHelper.clamp_double(d1, -1.0, 1.0);
      return (double)MathHelper.sqrt_double(1.0 - d1 * d1);
   }
}
