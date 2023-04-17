package club.mineman.antigamingchair.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class BlockUtil {
    private static Set<Byte> blockSolidPassSet;
    private static Set<Byte> blockStairsSet;
    private static Set<Byte> blockLiquidsSet;
    private static Set<Byte> blockWebsSet;
    private static Set<Byte> blockIceSet;

    static {
        BlockUtil.blockSolidPassSet = new HashSet<Byte>();
        BlockUtil.blockStairsSet = new HashSet<Byte>();
        BlockUtil.blockLiquidsSet = new HashSet<Byte>();
        BlockUtil.blockWebsSet = new HashSet<Byte>();
        BlockUtil.blockIceSet = new HashSet<Byte>();
        BlockUtil.blockSolidPassSet.add((byte) 0);
        BlockUtil.blockSolidPassSet.add((byte) 6);
        BlockUtil.blockSolidPassSet.add((byte) 8);
        BlockUtil.blockSolidPassSet.add((byte) 9);
        BlockUtil.blockSolidPassSet.add((byte) 10);
        BlockUtil.blockSolidPassSet.add((byte) 11);
        BlockUtil.blockSolidPassSet.add((byte) 27);
        BlockUtil.blockSolidPassSet.add((byte) 28);
        BlockUtil.blockSolidPassSet.add((byte) 30);
        BlockUtil.blockSolidPassSet.add((byte) 31);
        BlockUtil.blockSolidPassSet.add((byte) 32);
        BlockUtil.blockSolidPassSet.add((byte) 37);
        BlockUtil.blockSolidPassSet.add((byte) 38);
        BlockUtil.blockSolidPassSet.add((byte) 39);
        BlockUtil.blockSolidPassSet.add((byte) 40);
        BlockUtil.blockSolidPassSet.add((byte) 50);
        BlockUtil.blockSolidPassSet.add((byte) 51);
        BlockUtil.blockSolidPassSet.add((byte) 55);
        BlockUtil.blockSolidPassSet.add((byte) 59);
        BlockUtil.blockSolidPassSet.add((byte) 63);
        BlockUtil.blockSolidPassSet.add((byte) 66);
        BlockUtil.blockSolidPassSet.add((byte) 68);
        BlockUtil.blockSolidPassSet.add((byte) 69);
        BlockUtil.blockSolidPassSet.add((byte) 70);
        BlockUtil.blockSolidPassSet.add((byte) 72);
        BlockUtil.blockSolidPassSet.add((byte) 75);
        BlockUtil.blockSolidPassSet.add((byte) 76);
        BlockUtil.blockSolidPassSet.add((byte) 77);
        BlockUtil.blockSolidPassSet.add((byte) 78);
        BlockUtil.blockSolidPassSet.add((byte) 83);
        BlockUtil.blockSolidPassSet.add((byte) 90);
        BlockUtil.blockSolidPassSet.add((byte) 104);
        BlockUtil.blockSolidPassSet.add((byte) 105);
        BlockUtil.blockSolidPassSet.add((byte) 115);
        BlockUtil.blockSolidPassSet.add((byte) 119);
        BlockUtil.blockSolidPassSet.add((byte) (-124));
        BlockUtil.blockSolidPassSet.add((byte) (-113));
        BlockUtil.blockSolidPassSet.add((byte) (-81));
        BlockUtil.blockStairsSet.add((byte) 53);
        BlockUtil.blockStairsSet.add((byte) 67);
        BlockUtil.blockStairsSet.add((byte) 108);
        BlockUtil.blockStairsSet.add((byte) 109);
        BlockUtil.blockStairsSet.add((byte) 114);
        BlockUtil.blockStairsSet.add((byte) (-128));
        BlockUtil.blockStairsSet.add((byte) (-122));
        BlockUtil.blockStairsSet.add((byte) (-121));
        BlockUtil.blockStairsSet.add((byte) (-120));
        BlockUtil.blockStairsSet.add((byte) (-100));
        BlockUtil.blockStairsSet.add((byte) (-93));
        BlockUtil.blockStairsSet.add((byte) (-92));
        BlockUtil.blockStairsSet.add((byte) 126);
        BlockUtil.blockStairsSet.add((byte) (-76));
        BlockUtil.blockLiquidsSet.add((byte) 8);
        BlockUtil.blockLiquidsSet.add((byte) 9);
        BlockUtil.blockLiquidsSet.add((byte) 10);
        BlockUtil.blockLiquidsSet.add((byte) 11);
        BlockUtil.blockWebsSet.add((byte) 30);
        BlockUtil.blockIceSet.add((byte) 79);
        BlockUtil.blockIceSet.add((byte) (-82));
    }

    public static boolean isOnGround(final Location location, final int down) {
        final double posX = location.getX();
        final double posZ = location.getZ();
        final double fracX = (posX % 1.0 > 0.0) ? Math.abs(posX % 1.0) : (1.0 - Math.abs(posX % 1.0));
        final double fracZ = (posZ % 1.0 > 0.0) ? Math.abs(posZ % 1.0) : (1.0 - Math.abs(posZ % 1.0));
        final int blockX = location.getBlockX();
        final int blockY = location.getBlockY() - down;
        final int blockZ = location.getBlockZ();
        final World world = location.getWorld();
        if (!BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
            return true;
        }
        if (fracX < 0.3) {
            if (!BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (!BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                return !BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId());
            } else if (fracZ > 0.7) {
                if (!BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                return !BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId());
            }
        } else if (fracX > 0.7) {
            if (!BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (!BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                return !BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId());
            } else if (fracZ > 0.7) {
                if (!BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                return !BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId());
            }
        } else if (fracZ < 0.3) {
            return !BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId());
        } else
            return fracZ > 0.7 && !BlockUtil.blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId());
        return false;
    }

    public static boolean isOnStairs(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockStairsSet, down);
    }

    private static boolean isUnderBlock(final Location location, final Set<Byte> itemIDs, final int down) {
        final double posX = location.getX();
        final double posZ = location.getZ();
        final double fracX = (posX % 1.0 > 0.0) ? Math.abs(posX % 1.0) : (1.0 - Math.abs(posX % 1.0));
        final double fracZ = (posZ % 1.0 > 0.0) ? Math.abs(posZ % 1.0) : (1.0 - Math.abs(posZ % 1.0));
        final int blockX = location.getBlockX();
        final int blockY = location.getBlockY() - down;
        final int blockZ = location.getBlockZ();
        final World world = location.getWorld();
        if (itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
            return true;
        }
        if (fracX < 0.3) {
            if (itemIDs.contains(world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains(world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                return itemIDs.contains(world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId());
            } else if (fracZ > 0.7) {
                if (itemIDs.contains(world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                return itemIDs.contains(world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId());
            }
        } else if (fracX > 0.7) {
            if (itemIDs.contains(world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains(world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                return itemIDs.contains(world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId());
            } else if (fracZ > 0.7) {
                if (itemIDs.contains(world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                return itemIDs.contains(world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId());
            }
        } else if (fracZ < 0.3) {
            return itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId());
        } else return fracZ > 0.7 && itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId());
        return false;
    }

    public static boolean isOnLiquid(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockLiquidsSet, down);
    }

    public static boolean isOnWeb(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockWebsSet, down);
    }

    public static boolean isOnIce(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockIceSet, down);
    }
}
