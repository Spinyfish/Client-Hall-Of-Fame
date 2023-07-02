package me.eldodebug.soar.management.cosmetics;

import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;

public class CosmeticManager {

	private ArrayList<Cosmetic> cosmetics = new ArrayList<Cosmetic>();
	
	private String currentCpae;
	
	public CosmeticManager() {
		currentCpae = "None";
		cosmetics.add(new Cosmetic("None", this.loadResource("None-sample"), this.loadResource("None-sample")));
		cosmetics.add(new Cosmetic("Minecon 2011", this.loadResource("2011-sample"), this.loadResource("2011")));
		cosmetics.add(new Cosmetic("Minecon 2012", this.loadResource("2012-sample"), this.loadResource("2012")));
		cosmetics.add(new Cosmetic("Minecon 2013", this.loadResource("2013-sample"), this.loadResource("2013")));
		cosmetics.add(new Cosmetic("Minecon 2015", this.loadResource("2015-sample"), this.loadResource("2015")));
		cosmetics.add(new Cosmetic("Minecon 2016", this.loadResource("2016-sample"), this.loadResource("2016")));
		cosmetics.add(new Cosmetic("Unused2011-1", this.loadResource("Unused2011-1-sample"), this.loadResource("Unused2011-1")));
		cosmetics.add(new Cosmetic("Unused2011-2", this.loadResource("Unused2011-2-sample"), this.loadResource("Unused2011-2")));
		cosmetics.add(new Cosmetic("Unused2011-3", this.loadResource("Unused2011-3-sample"), this.loadResource("Unused2011-3")));
		cosmetics.add(new Cosmetic("VanillaCape", this.loadResource("VanillaCape-sample"), this.loadResource("VanillaCape")));
		cosmetics.add(new Cosmetic("ValentineCape", this.loadResource("ValentineCape-sample"), this.loadResource("ValentineCape")));
		cosmetics.add(new Cosmetic("Gr8_Escape", this.loadResource("Gr8_Escape-sample"), this.loadResource("Gr8_Escape")));
		cosmetics.add(new Cosmetic("Cubed", this.loadResource("Cubed-sample"), this.loadResource("Cubed")));
		cosmetics.add(new Cosmetic("MSCape", this.loadResource("MSCape-sample"), this.loadResource("MSCape")));
		cosmetics.add(new Cosmetic("Pancape", this.loadResource("Pancape-sample"), this.loadResource("Pancape")));
		cosmetics.add(new Cosmetic("MinecraftMarketPlace", this.loadResource("MinecraftMarketPlace-sample"), this.loadResource("MinecraftMarketPlace")));
		cosmetics.add(new Cosmetic("Turtle", this.loadResource("Turtle-sample"), this.loadResource("Turtle")));
		cosmetics.add(new Cosmetic("Prismarine", this.loadResource("Prismarine-sample"), this.loadResource("Prismarine")));
		cosmetics.add(new Cosmetic("MrMessiah", this.loadResource("MrMessiah-sample"), this.loadResource("MrMessiah")));
		cosmetics.add(new Cosmetic("Cobalt", this.loadResource("Cobalt-sample"), this.loadResource("Cobalt")));
		cosmetics.add(new Cosmetic("Realms", this.loadResource("Realms-sample"), this.loadResource("Realms")));
		cosmetics.add(new Cosmetic("BugTracker", this.loadResource("BugTracker-sample"), this.loadResource("BugTracker")));
		cosmetics.add(new Cosmetic("Scrolls", this.loadResource("Scrolls-sample"), this.loadResource("Scrolls")));
		cosmetics.add(new Cosmetic("CrowdinTranslator", this.loadResource("CrowdinTranslator-sample"), this.loadResource("CrowdinTranslator")));
		cosmetics.add(new Cosmetic("JulianClark", this.loadResource("JulianClark-sample"), this.loadResource("JulianClark")));
		cosmetics.add(new Cosmetic("dannyBstyle", this.loadResource("dannyBstyle-sample"), this.loadResource("dannyBstyle")));
		cosmetics.add(new Cosmetic("1Mill", this.loadResource("1Mill-sample"), this.loadResource("1Mill")));
		cosmetics.add(new Cosmetic("Bacon", this.loadResource("Bacon-sample"), this.loadResource("Bacon")));
		cosmetics.add(new Cosmetic("XboxBirthday", this.loadResource("XboxBirthday-sample"), this.loadResource("XboxBirthday")));
		cosmetics.add(new Cosmetic("NewYears", this.loadResource("NewYears-sample"), this.loadResource("NewYears")));
		cosmetics.add(new Cosmetic("Xmas", this.loadResource("Xmas-sample"), this.loadResource("Xmas")));
		cosmetics.add(new Cosmetic("MojangStudios", this.loadResource("MojangStudios-sample"), this.loadResource("MojangStudios")));
		cosmetics.add(new Cosmetic("NewMojang", this.loadResource("NewMojang-sample"), this.loadResource("NewMojang")));
		cosmetics.add(new Cosmetic("OldMojang", this.loadResource("OldMojang-sample"), this.loadResource("OldMojang")));
		cosmetics.add(new Cosmetic("Grey Migration Cape", this.loadResource("Grey Migration Cape-sample"), this.loadResource("Grey Migration Cape")));
		cosmetics.add(new Cosmetic("Orange Migration V2", this.loadResource("Orange Migration V2-sample"), this.loadResource("Orange Migration V2")));
		cosmetics.add(new Cosmetic("Rainbow Creeper", this.loadResource("Rainbow Creeper-sample"), this.loadResource("Rainbow Creeper")));
		cosmetics.add(new Cosmetic("Lazuli Glass Cape", this.loadResource("Lazuli Glass Cape-sample"), this.loadResource("Lazuli Glass Cape")));
		cosmetics.add(new Cosmetic("Forest", this.loadResource("Forest-sample"), this.loadResource("Forest")));
		cosmetics.add(new Cosmetic("Blue Migration V2", this.loadResource("Blue Migration V2-sample"), this.loadResource("Blue Migration V2")));
		cosmetics.add(new Cosmetic("Netherite Cape", this.loadResource("Netherite Cape-sample"), this.loadResource("Netherite Cape")));
		cosmetics.add(new Cosmetic("ByJoes Minecon Cape", this.loadResource("ByJoes Minecon Cape-sample"), this.loadResource("ByJoes Minecon Cape")));
	}
	
	public Cosmetic getCosmeticByName(String name) {
		return cosmetics.stream().filter(cosmetics -> cosmetics.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	private ResourceLocation loadResource(String path) {
		return new ResourceLocation("soar/cosmetic/" + path + ".png");
	}

	public String getCurrentCpae() {
		return currentCpae;
	}

	public void setCurrentCpae(String currentCpae) {
		this.currentCpae = currentCpae;
	}

	public ArrayList<Cosmetic> getCosmetics() {
		return cosmetics;
	}
}