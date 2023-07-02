package me.eldodebug.soar.management.colors;

import java.awt.Color;
import java.util.ArrayList;

public class ColorManager {

	private ArrayList<AccentColor> colors = new ArrayList<AccentColor>();
	
	private AccentColor clientColor;
	
	public ColorManager() {
		colors.add(new AccentColor("Blue", new Color(85, 184, 221), new Color(2, 94, 186)));
		colors.add(new AccentColor("Fruit", new Color(241, 178, 246), new Color(254, 240, 45)));
		colors.add(new AccentColor("Speamint", new Color(96, 194, 165), new Color(66, 129, 108)));
		colors.add(new AccentColor("Green Spiirit", new Color(5, 135, 65), new Color(158, 227, 191)));
		colors.add(new AccentColor("Rosy Pink", new Color(255, 102, 202), new Color(191, 78, 152)));
		colors.add(new AccentColor("Magenta", new Color(216, 63, 123), new Color(191, 77, 151)));
		colors.add(new AccentColor("Amethyst", new Color(144, 99, 205), new Color(99, 67, 140)));
		colors.add(new AccentColor("Sunset Pink", new Color(253, 145, 21), new Color(245, 106, 230)));
		colors.add(new AccentColor("Blaze Orange", new Color(254, 169, 76), new Color(253, 130, 0)));
		colors.add(new AccentColor("Lemon", new Color(252, 248, 184), new Color(255, 243, 109)));
		colors.add(new AccentColor("Pink Blood", new Color(226, 0, 70), new Color(255, 166, 200)));
		colors.add(new AccentColor("Neon Red", new Color(210, 39, 48), new Color(184, 27, 45)));
		colors.add(new AccentColor("Deep Ocean", new Color(61, 79, 143), new Color(1, 19, 63)));
		colors.add(new AccentColor("Chambray Blue", new Color(34, 45, 174), new Color(58, 79, 137)));
		colors.add(new AccentColor("Mint Blue", new Color(63, 149, 150), new Color(38, 90, 88)));
		colors.add(new AccentColor("Pacific Blue", new Color(7, 154, 186), new Color(0, 106, 122)));
		colors.add(new AccentColor("Tropical Ice", new Color(90, 227, 186), new Color(6, 133, 227)));
		colors.add(new AccentColor("Purple&Blue", new Color(33, 212, 253), new Color(183, 33, 255)));
		colors.add(new AccentColor("Melon", new Color(173, 247, 115), new Color(128, 243, 147)));
		colors.add(new AccentColor("Orange", new Color(251, 109, 32), new Color(190, 53, 38)));
		colors.add(new AccentColor("Pink", new Color(234, 107, 149), new Color(238, 164, 123)));
		colors.add(new AccentColor("MintYellow", new Color(100, 234, 190), new Color(254, 250, 163)));
		colors.add(new AccentColor("March 7th", new Color(237, 133, 211), new Color(28, 166, 222)));
		colors.add(new AccentColor("LightOrange", new Color(250, 217, 97), new Color(247, 107, 28)));
		colors.add(new AccentColor("ClearMint", new Color(116, 235, 213), new Color(159, 172, 230)));
		colors.add(new AccentColor("Purple Love", new Color(204, 43, 94), new Color(117, 58, 136)));
		colors.add(new AccentColor("Gray", new Color(189, 195, 199), new Color(44, 62, 80)));
		colors.add(new AccentColor("Candy", new Color(211, 149, 155), new Color(191, 230, 186)));
		colors.add(new AccentColor("Amin", new Color(142, 45, 226), new Color(74, 0, 224)));
		colors.add(new AccentColor("Metapolis", new Color(101, 153, 153), new Color(244, 121, 31)));
		colors.add(new AccentColor("Kye Meh", new Color(131, 96, 195), new Color(46, 191, 145)));
		colors.add(new AccentColor("Magic", new Color(89, 193, 115), new Color(93, 38, 193)));
		colors.add(new AccentColor("Sublime", new Color(252, 70, 107), new Color(63, 94, 251)));
		colors.add(new AccentColor("Relaxing red", new Color(255, 251, 213), new Color(178, 10, 44)));
		colors.add(new AccentColor("Sulphur", new Color(202, 197, 49), new Color(243, 249, 167)));
		colors.add(new AccentColor("Rainbow Blue", new Color(0, 242, 96), new Color(5, 117, 230)));
		colors.add(new AccentColor("Cinnamint", new Color(74, 194, 154), new Color(189, 255, 243)));
		
		clientColor = getAccentColorByName("MintYellow");
	}

	public AccentColor getClientColor() {
		return clientColor;
	}

	public void setClientColor(AccentColor clientColor) {
		this.clientColor = clientColor;
	}

	public ArrayList<AccentColor> getColors() {
		return colors;
	}
	
	public AccentColor getAccentColorByName(String name) {
		return colors.stream().filter(color -> color.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public AccentColor getAccentColorByClass(Class<AccentColor> modClass) {
		return colors.stream().filter(color -> color.getClass().equals(modClass)).findFirst().orElse(null);
	}
}
