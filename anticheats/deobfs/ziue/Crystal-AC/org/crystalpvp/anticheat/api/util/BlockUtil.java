package org.crystalpvp.anticheat.api.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockUtil {

	public static List<Material> blocked = new ArrayList<>();
	private static Set<Byte> blockSolidPassSet;
	private static Set<Byte> blockStairsSet;
	private static Set<Byte> blockLiquidsSet;
	private static Set<Byte> blockWebsSet;
	private static Set<Byte> blockIceSet;
	private static Set<Byte> blockPistonSet;
	private static Set<Byte> blockCarpetSet;

	static {
		BlockUtil.blockSolidPassSet = new HashSet<>();
		BlockUtil.blockStairsSet = new HashSet<>();
		BlockUtil.blockLiquidsSet = new HashSet<>();
		BlockUtil.blockWebsSet = new HashSet<>();
		BlockUtil.blockIceSet = new HashSet<>();
		BlockUtil.blockPistonSet = new HashSet<>();
		BlockUtil.blockCarpetSet = new HashSet<>();
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
		BlockUtil.blockStairsSet.add((byte) (-76));
		BlockUtil.blockStairsSet.add((byte) 126);
		BlockUtil.blockStairsSet.add((byte) (-74));
		BlockUtil.blockStairsSet.add((byte) 44);
		BlockUtil.blockStairsSet.add((byte) 78);
		BlockUtil.blockStairsSet.add((byte) 99);
		BlockUtil.blockStairsSet.add((byte) (-112));
		BlockUtil.blockStairsSet.add((byte) (-115));
		BlockUtil.blockStairsSet.add((byte) (-116));
		BlockUtil.blockStairsSet.add((byte) (-105));
		BlockUtil.blockStairsSet.add((byte) (-108));
		BlockUtil.blockStairsSet.add((byte) 100);
		BlockUtil.blockLiquidsSet.add((byte) 8);
		BlockUtil.blockLiquidsSet.add((byte) 9);
		BlockUtil.blockLiquidsSet.add((byte) 10);
		BlockUtil.blockLiquidsSet.add((byte) 11);
		BlockUtil.blockWebsSet.add((byte) 30);
		BlockUtil.blockIceSet.add((byte) 79);
		BlockUtil.blockIceSet.add((byte) (-82));
		BlockUtil.blockPistonSet.add((byte) 29);
		BlockUtil.blockPistonSet.add((byte) 33);
		BlockUtil.blockCarpetSet.add((byte) (-85));

		blocked.add(Material.ACTIVATOR_RAIL);
		blocked.add(Material.ANVIL);
		blocked.add(Material.BED_BLOCK);
		blocked.add(Material.POTATO);
		blocked.add(Material.POTATO_ITEM);
		blocked.add(Material.CARROT);
		blocked.add(Material.CARROT_ITEM);
		blocked.add(Material.BIRCH_WOOD_STAIRS);
		blocked.add(Material.BREWING_STAND);
		blocked.add(Material.BOAT);
		blocked.add(Material.BRICK_STAIRS);
		blocked.add(Material.BROWN_MUSHROOM);
		blocked.add(Material.CAKE_BLOCK);
		blocked.add(Material.CARPET);
		blocked.add(Material.CAULDRON);
		blocked.add(Material.COBBLESTONE_STAIRS);
		blocked.add(Material.COBBLE_WALL);
		blocked.add(Material.DARK_OAK_STAIRS);
		blocked.add(Material.DIODE);
		blocked.add(Material.DIODE_BLOCK_ON);
		blocked.add(Material.DIODE_BLOCK_OFF);
		blocked.add(Material.DEAD_BUSH);
		blocked.add(Material.DETECTOR_RAIL);
		blocked.add(Material.DOUBLE_PLANT);
		blocked.add(Material.DOUBLE_STEP);
		blocked.add(Material.DRAGON_EGG);
		blocked.add(Material.PAINTING);
		blocked.add(Material.FLOWER_POT);
		blocked.add(Material.GOLD_PLATE);
		blocked.add(Material.HOPPER);
		blocked.add(Material.STONE_PLATE);
		blocked.add(Material.IRON_PLATE);
		blocked.add(Material.HUGE_MUSHROOM_1);
		blocked.add(Material.HUGE_MUSHROOM_2);
		blocked.add(Material.IRON_DOOR_BLOCK);
		blocked.add(Material.IRON_DOOR);
		blocked.add(Material.FENCE);
		blocked.add(Material.IRON_FENCE);
		blocked.add(Material.IRON_PLATE);
		blocked.add(Material.ITEM_FRAME);
		blocked.add(Material.JUKEBOX);
		blocked.add(Material.JUNGLE_WOOD_STAIRS);
		blocked.add(Material.LADDER);
		blocked.add(Material.LEVER);
		blocked.add(Material.LONG_GRASS);
		blocked.add(Material.NETHER_FENCE);
		blocked.add(Material.NETHER_STALK);
		blocked.add(Material.NETHER_WARTS);
		blocked.add(Material.MELON_STEM);
		blocked.add(Material.PUMPKIN_STEM);
		blocked.add(Material.QUARTZ_STAIRS);
		blocked.add(Material.RAILS);
		blocked.add(Material.RED_MUSHROOM);
		blocked.add(Material.RED_ROSE);
		blocked.add(Material.SAPLING);
		blocked.add(Material.SEEDS);
		blocked.add(Material.SIGN);
		blocked.add(Material.SIGN_POST);
		blocked.add(Material.SKULL);
		blocked.add(Material.SMOOTH_STAIRS);
		blocked.add(Material.NETHER_BRICK_STAIRS);
		blocked.add(Material.SPRUCE_WOOD_STAIRS);
		blocked.add(Material.STAINED_GLASS_PANE);
		blocked.add(Material.REDSTONE_COMPARATOR);
		blocked.add(Material.REDSTONE_COMPARATOR_OFF);
		blocked.add(Material.REDSTONE_COMPARATOR_ON);
		blocked.add(Material.REDSTONE_LAMP_OFF);
		blocked.add(Material.REDSTONE_LAMP_ON);
		blocked.add(Material.REDSTONE_TORCH_OFF);
		blocked.add(Material.REDSTONE_TORCH_ON);
		blocked.add(Material.REDSTONE_WIRE);
		blocked.add(Material.SANDSTONE_STAIRS);
		blocked.add(Material.STEP);
		blocked.add(Material.ACACIA_STAIRS);
		blocked.add(Material.SUGAR_CANE);
		blocked.add(Material.SUGAR_CANE_BLOCK);
		blocked.add(Material.ENCHANTMENT_TABLE);
		blocked.add(Material.SOUL_SAND);
		blocked.add(Material.TORCH);
		blocked.add(Material.TRAP_DOOR);
		blocked.add(Material.TRIPWIRE);
		blocked.add(Material.TRIPWIRE_HOOK);
		blocked.add(Material.WALL_SIGN);
		blocked.add(Material.VINE);
		blocked.add(Material.WATER_LILY);
		blocked.add(Material.WEB);
		blocked.add(Material.WOOD_DOOR);
		blocked.add(Material.WOOD_DOUBLE_STEP);
		blocked.add(Material.WOOD_PLATE);
		blocked.add(Material.WOOD_STAIRS);
		blocked.add(Material.WOOD_STEP);
		blocked.add(Material.HOPPER);
		blocked.add(Material.WOODEN_DOOR);
		blocked.add(Material.YELLOW_FLOWER);
		blocked.add(Material.LAVA);
		blocked.add(Material.WATER);
		blocked.add(Material.STATIONARY_WATER);
		blocked.add(Material.STATIONARY_LAVA);
		blocked.add(Material.CACTUS);
		blocked.add(Material.CHEST);
		blocked.add(Material.PISTON_BASE);
		blocked.add(Material.PISTON_MOVING_PIECE);
		blocked.add(Material.PISTON_EXTENSION);
		blocked.add(Material.PISTON_STICKY_BASE);
		blocked.add(Material.TRAPPED_CHEST);
		blocked.add(Material.SNOW);
		blocked.add(Material.ENDER_CHEST);
		blocked.add(Material.THIN_GLASS);
		blocked.add(Material.ENDER_PORTAL_FRAME);
	}

	public static boolean isOnStairs(final Location location, final int down) {
		return isUnderBlock(location, BlockUtil.blockStairsSet, down);
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

	public static boolean isOnPiston(final Location location, final int down) {
		return isUnderBlock(location, BlockUtil.blockPistonSet, down);
	}

	public static boolean isOnCarpet(final Location location, final int down) {
		return isUnderBlock(location, BlockUtil.blockCarpetSet, down);
	}

	public static boolean isSlab(final Player player) {
		return blocked.contains(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType());
	}

	public static boolean isBlockFaceAir(final Player player) {
		final Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		return block.getType().equals(Material.AIR) && block.getRelative(BlockFace.WEST).getType().equals(Material
				.AIR) && block.getRelative(BlockFace.NORTH).getType().equals(Material.AIR) && block.getRelative
				(BlockFace.EAST).getType().equals(Material.AIR) && block.getRelative(BlockFace.SOUTH).getType()
		                                                                .equals(Material.AIR);
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

		if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
			return true;
		}

		if (fracX < 0.3) {
			if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())) {
				return true;
			}

			if (fracZ < 0.3) {
				if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
					return true;
				}

				if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
					return true;
				}

				if (itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
					return true;
				}
			} else if (fracZ > 0.7) {
				if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
					return true;
				}

				if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
					return true;
				}

				if (itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
					return true;
				}
			}
		} else if (fracX > 0.7) {
			if (itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())) {
				return true;
			}

			if (fracZ < 0.3) {
				if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
					return true;
				}

				if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
					return true;
				}

				if (itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
					return true;
				}
			} else if (fracZ > 0.7) {
				if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
					return true;
				}

				if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
					return true;
				}

				if (itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
					return true;
				}
			}
		} else if (fracZ < 0.3) {
			if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
				return true;
			}
		} else if (fracZ > 0.7 && itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
			return true;
		}

		return false;
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

		if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
			return true;
		}

		if (fracX < 0.3) {
			if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId()
			)) {
				return true;
			}

			if (fracZ < 0.3) {
				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1)
				                                                      .getTypeId())) {
					return true;
				}

				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1)
				                                                      .getTypeId())) {
					return true;
				}

				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1)
				                                                      .getTypeId())) {
					return true;
				}
			} else if (fracZ > 0.7) {
				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1)
				                                                      .getTypeId())) {
					return true;
				}

				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1)
				                                                      .getTypeId())) {
					return true;
				}

				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1)
				                                                      .getTypeId())) {
					return true;
				}
			}
		} else if (fracX > 0.7) {
			if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId()
			)) {
				return true;
			}

			if (fracZ < 0.3) {
				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1)
				                                                      .getTypeId())) {
					return true;
				}

				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1)
				                                                      .getTypeId())) {
					return true;
				}

				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1)
				                                                      .getTypeId())) {
					return true;
				}
			} else if (fracZ > 0.7) {
				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1)
				                                                      .getTypeId())) {
					return true;
				}

				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1)
				                                                      .getTypeId())) {
					return true;
				}

				if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1)
				                                                      .getTypeId())) {
					return true;
				}
			}
		} else if (fracZ < 0.3) {
			if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId()
			)) {
				return true;
			}
		} else if (fracZ > 0.7 && !BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY,
				blockZ + 1
		).getTypeId())) {
			return true;
		}

		return false;
	}


	public static ArrayList<Block> getBlocksAround(Location loc, int radius){
		ArrayList<Block> blocks = new ArrayList<Block>();
		for(double x = loc.getX() - radius; x <= loc.getX() + radius; x++){
			for(double y = loc.getY() - radius; y <= loc.getY() + radius; y++){
				for(double z = loc.getZ() - radius; z <= loc.getZ() + radius; z++){
					Location block = new Location(loc.getWorld(), x, y, z);
					blocks.add(block.getBlock());
				}
			}
		}
		return blocks;
	}


	public static boolean isBlockingVelocityH(Location loc) {
		for(double x = loc.getX() - 1; x <= loc.getX() + 1; x++){
			for(double y = loc.getY(); y <= loc.getY() + 2; y++){
				for(double z = loc.getZ() - 1; z <= loc.getZ() + 1; z++){
					Location block = new Location(loc.getWorld(), x, y, z);
					if (!String.format("%s", block.getBlock()).contains("AIR")) {
						return true;
					}
				}
			}
		}
		return false;
	}


	public static boolean isOnPlatform(Location loc, String type){
		for(double x = loc.getX() - 1; x <= loc.getX() + 1; x++){
			for(double y = loc.getY() - 1; y <= loc.getY() - 1; y++){
				for(double z = loc.getZ() - 1; z <= loc.getZ() + 1; z++){
					Location block = new Location(loc.getWorld(), x, y, z);
					if (!String.format("%s", block.getBlock()).contains(type)) {
						return false;
					}
				}
			}
		}
		return true;
	}



	public static boolean blockAt(Location loc, String type) {
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();

		Location block =  new Location(loc.getWorld(), x, y, z);
		return String.format("%s", block.getBlock()).contains(type);
	}


}
