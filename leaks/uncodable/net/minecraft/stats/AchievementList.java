package net.minecraft.stats;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonSerializableSet;

public class AchievementList {
   public static int minDisplayColumn;
   public static int minDisplayRow;
   public static int maxDisplayColumn;
   public static int maxDisplayRow;
   public static final List<Achievement> achievementList = Lists.newArrayList();
   public static final Achievement openInventory = new Achievement("achievement.openInventory", "openInventory", 0, 0, Items.book, null)
      .initIndependentStat()
      .registerStat();
   public static final Achievement mineWood = new Achievement("achievement.mineWood", "mineWood", 2, 1, Blocks.log, openInventory).registerStat();
   public static final Achievement buildWorkBench = new Achievement("achievement.buildWorkBench", "buildWorkBench", 4, -1, Blocks.crafting_table, mineWood)
      .registerStat();
   public static final Achievement buildPickaxe = new Achievement("achievement.buildPickaxe", "buildPickaxe", 4, 2, Items.wooden_pickaxe, buildWorkBench)
      .registerStat();
   public static final Achievement buildFurnace = new Achievement("achievement.buildFurnace", "buildFurnace", 3, 4, Blocks.furnace, buildPickaxe)
      .registerStat();
   public static final Achievement acquireIron = new Achievement("achievement.acquireIron", "acquireIron", 1, 4, Items.iron_ingot, buildFurnace)
      .registerStat();
   public static final Achievement buildHoe = new Achievement("achievement.buildHoe", "buildHoe", 2, -3, Items.wooden_hoe, buildWorkBench).registerStat();
   public static final Achievement makeBread = new Achievement("achievement.makeBread", "makeBread", -1, -3, Items.bread, buildHoe).registerStat();
   public static final Achievement bakeCake = new Achievement("achievement.bakeCake", "bakeCake", 0, -5, Items.cake, buildHoe).registerStat();
   public static final Achievement buildBetterPickaxe = new Achievement(
         "achievement.buildBetterPickaxe", "buildBetterPickaxe", 6, 2, Items.stone_pickaxe, buildPickaxe
      )
      .registerStat();
   public static final Achievement cookFish = new Achievement("achievement.cookFish", "cookFish", 2, 6, Items.cooked_fish, buildFurnace).registerStat();
   public static final Achievement onARail = new Achievement("achievement.onARail", "onARail", 2, 3, Blocks.rail, acquireIron).setSpecial().registerStat();
   public static final Achievement buildSword = new Achievement("achievement.buildSword", "buildSword", 6, -1, Items.wooden_sword, buildWorkBench)
      .registerStat();
   public static final Achievement killEnemy = new Achievement("achievement.killEnemy", "killEnemy", 8, -1, Items.bone, buildSword).registerStat();
   public static final Achievement killCow = new Achievement("achievement.killCow", "killCow", 7, -3, Items.leather, buildSword).registerStat();
   public static final Achievement flyPig = new Achievement("achievement.flyPig", "flyPig", 9, -3, Items.saddle, killCow).setSpecial().registerStat();
   public static final Achievement snipeSkeleton = new Achievement("achievement.snipeSkeleton", "snipeSkeleton", 7, 0, Items.bow, killEnemy)
      .setSpecial()
      .registerStat();
   public static final Achievement diamonds = new Achievement("achievement.diamonds", "diamonds", -1, 5, Blocks.diamond_ore, acquireIron).registerStat();
   public static final Achievement diamondsToYou = new Achievement("achievement.diamondsToYou", "diamondsToYou", -1, 2, Items.diamond, diamonds)
      .registerStat();
   public static final Achievement portal = new Achievement("achievement.portal", "portal", -1, 7, Blocks.obsidian, diamonds).registerStat();
   public static final Achievement ghast = new Achievement("achievement.ghast", "ghast", -4, 8, Items.ghast_tear, portal).setSpecial().registerStat();
   public static final Achievement blazeRod = new Achievement("achievement.blazeRod", "blazeRod", 0, 9, Items.blaze_rod, portal).registerStat();
   public static final Achievement potion = new Achievement("achievement.potion", "potion", 2, 8, Items.potionitem, blazeRod).registerStat();
   public static final Achievement theEnd = new Achievement("achievement.theEnd", "theEnd", 3, 10, Items.ender_eye, blazeRod).setSpecial().registerStat();
   public static final Achievement theEnd2 = new Achievement("achievement.theEnd2", "theEnd2", 4, 13, Blocks.dragon_egg, theEnd).setSpecial().registerStat();
   public static final Achievement enchantments = new Achievement("achievement.enchantments", "enchantments", -4, 4, Blocks.enchanting_table, diamonds)
      .registerStat();
   public static final Achievement overkill = new Achievement("achievement.overkill", "overkill", -4, 1, Items.diamond_sword, enchantments)
      .setSpecial()
      .registerStat();
   public static final Achievement bookcase = new Achievement("achievement.bookcase", "bookcase", -3, 6, Blocks.bookshelf, enchantments).registerStat();
   public static final Achievement breedCow = new Achievement("achievement.breedCow", "breedCow", 7, -5, Items.wheat, killCow).registerStat();
   public static final Achievement spawnWither = new Achievement("achievement.spawnWither", "spawnWither", 7, 12, new ItemStack(Items.skull, 1, 1), theEnd2)
      .registerStat();
   public static final Achievement killWither = new Achievement("achievement.killWither", "killWither", 7, 10, Items.nether_star, spawnWither).registerStat();
   public static final Achievement fullBeacon = new Achievement("achievement.fullBeacon", "fullBeacon", 7, 8, Blocks.beacon, killWither)
      .setSpecial()
      .registerStat();
   public static final Achievement exploreAllBiomes = new Achievement("achievement.exploreAllBiomes", "exploreAllBiomes", 4, 8, Items.diamond_boots, theEnd)
      .func_150953_b(JsonSerializableSet.class)
      .setSpecial()
      .registerStat();
   public static final Achievement overpowered = new Achievement(
         "achievement.overpowered", "overpowered", 6, 4, new ItemStack(Items.golden_apple, 1, 1), buildBetterPickaxe
      )
      .setSpecial()
      .registerStat();

   public static void init() {
   }
}
