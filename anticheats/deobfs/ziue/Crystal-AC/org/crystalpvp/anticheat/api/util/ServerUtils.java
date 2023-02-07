package org.crystalpvp.anticheat.api.util;

import org.crystalpvp.anticheat.CrystalAC;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

public final class ServerUtils {

    private ServerUtils() {
    }

    //Will return null if chunk in location is not in memory. Do not modify blocks asynchronously!
    public static Block getBlockAsync(Location loc) {
        if (loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4))
            return loc.getBlock();
        return null;
    }

    //Will return null if chunk in location is not in memory. Do not modify chunks asynchronously!
    public static Chunk getChunkAsync(Location loc) {
        if (loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4))
            return loc.getChunk();
        return null;
    }

    public static void printToConsole(String string) {
        CrystalAC.getInstance().consoleSender.sendMessage(CC.translate(string));
    }


}
