package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RegionFileCache {
    private static final Map<File, RegionFile> regionsByFilename = Maps.newHashMap();

    public static synchronized RegionFile createOrLoadRegionFile(final File worldDir, final int chunkX, final int chunkZ) {
        final File file1 = new File(worldDir, "region");
        final File file2 = new File(file1, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");
        final RegionFile regionfile = regionsByFilename.get(file2);

        if (regionfile != null) {
            return regionfile;
        } else {
            if (!file1.exists()) {
                file1.mkdirs();
            }

            if (regionsByFilename.size() >= 256) {
                clearRegionFileReferences();
            }

            final RegionFile regionfile1 = new RegionFile(file2);
            regionsByFilename.put(file2, regionfile1);
            return regionfile1;
        }
    }

    /**
     * clears region file references
     */
    public static synchronized void clearRegionFileReferences() {
        for (final RegionFile regionfile : regionsByFilename.values()) {
            try {
                if (regionfile != null) {
                    regionfile.close();
                }
            } catch (final IOException ioexception) {
                ioexception.printStackTrace();
            }
        }

        regionsByFilename.clear();
    }

    /**
     * Returns an input stream for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static DataInputStream getChunkInputStream(final File worldDir, final int chunkX, final int chunkZ) {
        final RegionFile regionfile = createOrLoadRegionFile(worldDir, chunkX, chunkZ);
        return regionfile.getChunkDataInputStream(chunkX & 31, chunkZ & 31);
    }

    /**
     * Returns an output stream for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static DataOutputStream getChunkOutputStream(final File worldDir, final int chunkX, final int chunkZ) {
        final RegionFile regionfile = createOrLoadRegionFile(worldDir, chunkX, chunkZ);
        return regionfile.getChunkDataOutputStream(chunkX & 31, chunkZ & 31);
    }
}
