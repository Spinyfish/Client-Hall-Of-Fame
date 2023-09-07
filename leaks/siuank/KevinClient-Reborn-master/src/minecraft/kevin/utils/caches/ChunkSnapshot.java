package kevin.utils.caches;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ChunkSnapshot {
    public final ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
    public ChunkSnapshot(ExtendedBlockStorage[] storages) {
        System.arraycopy(storages, 0, storageArrays, 0, 16);
    }

    public IBlockState getBlockAt(int x, int y, int z) {
        return storageArrays[y >> 4].get(x, y & 15, z);
    }
}
