package net.optifine.render;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

public class ChunkVisibility {
    public static final int MASK_FACINGS = 63;
    public static final EnumFacing[][] enumFacingArrays = makeEnumFacingArrays(false);
    public static final EnumFacing[][] enumFacingOppositeArrays = makeEnumFacingArrays(true);
    private static int counter = 0;
    private static int iMaxStatic = -1;
    private static int iMaxStaticFinal = 16;
    private static World worldLast = null;
    private static int pcxLast = Integer.MIN_VALUE;
    private static int pczLast = Integer.MIN_VALUE;

    public static int getMaxChunkY(final World world, final Entity viewEntity, final int renderDistanceChunks) {
        final int i = MathHelper.floor_double(viewEntity.posX) >> 4;
        final int j = MathHelper.floor_double(viewEntity.posY) >> 4;
        final int k = MathHelper.floor_double(viewEntity.posZ) >> 4;
        final Chunk chunk = world.getChunkFromChunkCoords(i, k);
        int l = i - renderDistanceChunks;
        int i1 = i + renderDistanceChunks;
        int j1 = k - renderDistanceChunks;
        int k1 = k + renderDistanceChunks;

        if (world != worldLast || i != pcxLast || k != pczLast) {
            counter = 0;
            iMaxStaticFinal = 16;
            worldLast = world;
            pcxLast = i;
            pczLast = k;
        }

        if (counter == 0) {
            iMaxStatic = -1;
        }

        int l1 = iMaxStatic;

        switch (counter) {
            case 0:
                i1 = i;
                k1 = k;
                break;

            case 1:
                l = i;
                k1 = k;
                break;

            case 2:
                i1 = i;
                j1 = k;
                break;

            case 3:
                l = i;
                j1 = k;
        }

        for (int i2 = l; i2 < i1; ++i2) {
            for (int j2 = j1; j2 < k1; ++j2) {
                final Chunk chunk1 = world.getChunkFromChunkCoords(i2, j2);

                if (!chunk1.isEmpty()) {
                    final ExtendedBlockStorage[] aextendedblockstorage = chunk1.getBlockStorageArray();

                    for (int k2 = aextendedblockstorage.length - 1; k2 > l1; --k2) {
                        final ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[k2];

                        if (extendedblockstorage != null && !extendedblockstorage.isEmpty()) {
                            if (k2 > l1) {
                                l1 = k2;
                            }

                            break;
                        }
                    }

                    try {
                        final Map<BlockPos, TileEntity> map = chunk1.getTileEntityMap();

                        if (!map.isEmpty()) {
                            for (final BlockPos blockpos : map.keySet()) {
                                final int l2 = blockpos.getY() >> 4;

                                if (l2 > l1) {
                                    l1 = l2;
                                }
                            }
                        }
                    } catch (final ConcurrentModificationException var21) {
                    }

                    final ClassInheritanceMultiMap<Entity>[] classinheritancemultimap = chunk1.getEntityLists();

                    for (int i3 = classinheritancemultimap.length - 1; i3 > l1; --i3) {
                        final ClassInheritanceMultiMap<Entity> classinheritancemultimap1 = classinheritancemultimap[i3];

                        if (!classinheritancemultimap1.isEmpty() && (chunk1 != chunk || i3 != j || classinheritancemultimap1.size() != 1)) {
                            if (i3 > l1) {
                                l1 = i3;
                            }

                            break;
                        }
                    }
                }
            }
        }

        if (counter < 3) {
            iMaxStatic = l1;
            l1 = iMaxStaticFinal;
        } else {
            iMaxStaticFinal = l1;
            iMaxStatic = -1;
        }

        counter = (counter + 1) % 4;
        return l1 << 4;
    }

    public static boolean isFinished() {
        return counter == 0;
    }

    private static EnumFacing[][] makeEnumFacingArrays(final boolean opposite) {
        final int i = 64;
        final EnumFacing[][] aenumfacing = new EnumFacing[i][];

        for (int j = 0; j < i; ++j) {
            final List<EnumFacing> list = new ArrayList();

            for (int k = 0; k < EnumFacing.VALUES.length; ++k) {
                final EnumFacing enumfacing = EnumFacing.VALUES[k];
                final EnumFacing enumfacing1 = opposite ? enumfacing.getOpposite() : enumfacing;
                final int l = 1 << enumfacing1.ordinal();

                if ((j & l) != 0) {
                    list.add(enumfacing);
                }
            }

            final EnumFacing[] aenumfacing1 = list.toArray(new EnumFacing[list.size()]);
            aenumfacing[j] = aenumfacing1;
        }

        return aenumfacing;
    }

    public static EnumFacing[] getFacingsNotOpposite(final int setDisabled) {
        final int i = ~setDisabled & 63;
        return enumFacingOppositeArrays[i];
    }

    public static void reset() {
        worldLast = null;
    }
}
