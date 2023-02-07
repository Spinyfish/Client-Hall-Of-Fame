package org.crystalpvp.anticheat.api.util;

import org.crystalpvp.anticheat.CrystalAC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtil {

	private static final Map<String, ItemData> NAME_MAP = new HashMap<>();

	public static ItemData[] repeat(Material material, int times) {
		return repeat(material, (byte) 0, times);
	}

	public static ItemData[] repeat(Material material, byte data, int times) {
		ItemData[] itemData = new ItemData[times];

		for (int i = 0; i < times; i++) {
			itemData[i] = new ItemData(material, data);
		}

		return itemData;

	}

	public static ItemData[] armorOf(ArmorPart part) {
		List<ItemData> data = new ArrayList<>();

		for (ArmorType at : ArmorType.values()) {
			data.add(new ItemData(Material.valueOf(at.name() + "_" + part.name()), (short) 0));
		}

		return data.toArray(new ItemData[data.size()]);
	}

	public static ItemData[] swords() {
		List<ItemData> data = new ArrayList<>();

		for (SwordType at : SwordType.values()) {
			data.add(new ItemData(Material.valueOf(at.name() + "_SWORD"), (short) 0));
		}

		return data.toArray(new ItemData[data.size()]);
	}

	public static void load() {
		NAME_MAP.clear();

		List<String> lines = readLines();

		for (String line : lines) {
			String[] parts = line.split(",");

			NAME_MAP.put(
					parts[0],
					new ItemData(Material.getMaterial(Integer.parseInt(parts[1])), Short.parseShort(parts[2]))
			);
		}
	}

	public static ItemStack get(String input, int amount) {
		ItemStack item = get(input);

		if (item != null) {
			item.setAmount(amount);
		}

		return item;
	}

	public static ItemStack get(String input) {
		if (NumberUtil.isInteger(input)) {
			return new ItemStack(Material.getMaterial(Integer.parseInt(input)));
		}

		if (input.contains(":")) {
			if (NumberUtil.isShort(input.split(":")[1])) {
				if (NumberUtil.isInteger(input.split(":")[0])) {
					return new ItemStack(
							Material.getMaterial(Integer.parseInt(input.split(":")[0])), 1,
							Short.parseShort(input.split(":")[1])
					);
				} else {
					if (!NAME_MAP.containsKey(input.split(":")[0].toLowerCase())) {
						return null;
					}

					ItemData data = NAME_MAP.get(input.split(":")[0].toLowerCase());
					return new ItemStack(data.getMaterial(), 1, Short.parseShort(input.split(":")[1]));
				}
			} else {
				return null;
			}
		}

		if (!NAME_MAP.containsKey(input)) {
			return null;
		}

		return NAME_MAP.get(input).toItemStack();
	}

	public static String getName(ItemStack item) {
		if (item.getDurability() != 0) {
			String reflectedName = BukkitReflection.getItemStackName(item);

			if (reflectedName != null) {
				if (reflectedName.contains(".")) {
					reflectedName = WordUtils.capitalize(item.getType().toString().toLowerCase().replace("_", " "));
				}

				return reflectedName;
			}
		}

		String string = item.getType().toString().replace("_", " ");
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;

		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
				found = false;
			}
		}

		return String.valueOf(chars);
	}

	private static List<String> readLines() {
		try {
			return IOUtils.readLines(CrystalAC.class.getClassLoader().getResourceAsStream("items.csv"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public enum ArmorPart {
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS
	}

	public enum ArmorType {
		DIAMOND,
		IRON,
		GOLD,
		LEATHER
	}

	public enum SwordType {
		DIAMOND,
		IRON,
		GOLD,
		STONE
	}

	@Getter
	@AllArgsConstructor
	public static class ItemData {

		private final Material material;
		private final short data;

		public String getName() {
			return ItemUtil.getName(toItemStack());
		}

		public boolean matches(ItemStack item) {
			return item != null && item.getType() == material && item.getDurability() == data;
		}

		public ItemStack toItemStack() {
			return new ItemStack(material, 1, data);
		}

	}



	public static ItemStack guiItem(Material material, String displayName, int amount,
									  byte data, String... lore) {
		ItemStack itemStack = new ItemStack(material, amount, data);
		ItemMeta itemMeta = itemStack.getItemMeta();

		if (!displayName.equals("")) {
			itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
					displayName));
		}

		List<String> loreList = new ArrayList<>();

		for (String s : lore) {
			loreList.add(CC.translate(s));
		}

		itemMeta.setLore(loreList);
		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

	public static ItemStack guiItem(Material material, String displayName, String... lore) {
		return guiItem(material, displayName, 1, (byte)0, lore);
	}

	public static ItemStack guiItem(Material material) {
		return guiItem(material, "", 1, (byte) 0);
	}

	public static ItemStack guiItem(Material material, String displayName) {
		return guiItem(material, displayName, 1, (byte) 0);
	}

	public static ItemStack guiItem(Material material, String displayName, byte data) {
		return guiItem(material, displayName, 1, data);
	}

	public static ItemStack guiItem(Material material, String displayName, int amount) {
		return guiItem(material, displayName, amount, (byte) 0);
	}

	public static ItemStack guiItem(Material material, int amount) {
		return guiItem(material, "", amount, (byte) 0);
	}

	public static ItemStack guiItem(Material material, byte data) {
		return guiItem(material, "", 1, data);
	}

	public static ItemStack guiItem(Material material, int amount, String displayName) {
		return guiItem(material, displayName, amount, (byte) 0);
	}

}
