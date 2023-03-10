/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_12to1_11_1.data;

import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Set;

public class AchievementTranslationMapping {
    private static final Object2ObjectOpenHashMap<String, String> ACHIEVEMENTS = new Object2ObjectOpenHashMap(150, 0.99f);
    private static final Set<String> SPECIAL_ACHIEVEMENTS = new HashSet<String>(10);

    private static void add(String key, String value) {
        ACHIEVEMENTS.put(key, value);
    }

    private static void addSpecial(String key, String value) {
        AchievementTranslationMapping.add(key, value);
        SPECIAL_ACHIEVEMENTS.add(key);
    }

    public static String get(String key) {
        return ACHIEVEMENTS.get(key);
    }

    public static boolean isSpecial(String key) {
        return SPECIAL_ACHIEVEMENTS.contains(key);
    }

    static {
        AchievementTranslationMapping.add("chat.type.achievement", "%s has just earned the achievement %s");
        AchievementTranslationMapping.add("chat.type.achievement.taken", "%s has lost the achievement %s");
        AchievementTranslationMapping.add("stats.tooltip.type.achievement", "Achievement");
        AchievementTranslationMapping.add("stats.tooltip.type.statistic", "Statistic");
        AchievementTranslationMapping.add("stat.generalButton", "General");
        AchievementTranslationMapping.add("stat.blocksButton", "Blocks");
        AchievementTranslationMapping.add("stat.itemsButton", "Items");
        AchievementTranslationMapping.add("stat.mobsButton", "Mobs");
        AchievementTranslationMapping.add("stat.used", "Times Used");
        AchievementTranslationMapping.add("stat.mined", "Times Mined");
        AchievementTranslationMapping.add("stat.depleted", "Times Depleted");
        AchievementTranslationMapping.add("stat.crafted", "Times Crafted");
        AchievementTranslationMapping.add("stat.entityKills", "You killed %s %s");
        AchievementTranslationMapping.add("stat.entityKilledBy", "%s killed you %s time(s)");
        AchievementTranslationMapping.add("stat.entityKills.none", "You have never killed %s");
        AchievementTranslationMapping.add("stat.entityKilledBy.none", "You have never been killed by %s");
        AchievementTranslationMapping.add("stat.startGame", "Times Played");
        AchievementTranslationMapping.add("stat.createWorld", "Worlds Created");
        AchievementTranslationMapping.add("stat.loadWorld", "Saves Loaded");
        AchievementTranslationMapping.add("stat.joinMultiplayer", "Multiplayer Joins");
        AchievementTranslationMapping.add("stat.leaveGame", "Games Quit");
        AchievementTranslationMapping.add("stat.playOneMinute", "Minutes Played");
        AchievementTranslationMapping.add("stat.timeSinceDeath", "Since Last Death");
        AchievementTranslationMapping.add("stat.sneakTime", "Sneak Time");
        AchievementTranslationMapping.add("stat.walkOneCm", "Distance Walked");
        AchievementTranslationMapping.add("stat.crouchOneCm", "Distance Crouched");
        AchievementTranslationMapping.add("stat.sprintOneCm", "Distance Sprinted");
        AchievementTranslationMapping.add("stat.fallOneCm", "Distance Fallen");
        AchievementTranslationMapping.add("stat.swimOneCm", "Distance Swum");
        AchievementTranslationMapping.add("stat.flyOneCm", "Distance Flown");
        AchievementTranslationMapping.add("stat.climbOneCm", "Distance Climbed");
        AchievementTranslationMapping.add("stat.diveOneCm", "Distance Dove");
        AchievementTranslationMapping.add("stat.minecartOneCm", "Distance by Minecart");
        AchievementTranslationMapping.add("stat.boatOneCm", "Distance by Boat");
        AchievementTranslationMapping.add("stat.pigOneCm", "Distance by Pig");
        AchievementTranslationMapping.add("stat.horseOneCm", "Distance by Horse");
        AchievementTranslationMapping.add("stat.aviateOneCm", "Distance by Elytra");
        AchievementTranslationMapping.add("stat.jump", "Jumps");
        AchievementTranslationMapping.add("stat.drop", "Items Dropped");
        AchievementTranslationMapping.add("stat.dropped", "Dropped");
        AchievementTranslationMapping.add("stat.pickup", "Picked Up");
        AchievementTranslationMapping.add("stat.damageDealt", "Damage Dealt");
        AchievementTranslationMapping.add("stat.damageTaken", "Damage Taken");
        AchievementTranslationMapping.add("stat.deaths", "Number of Deaths");
        AchievementTranslationMapping.add("stat.mobKills", "Mob Kills");
        AchievementTranslationMapping.add("stat.animalsBred", "Animals Bred");
        AchievementTranslationMapping.add("stat.playerKills", "Player Kills");
        AchievementTranslationMapping.add("stat.fishCaught", "Fish Caught");
        AchievementTranslationMapping.add("stat.treasureFished", "Treasure Fished");
        AchievementTranslationMapping.add("stat.junkFished", "Junk Fished");
        AchievementTranslationMapping.add("stat.talkedToVillager", "Talked to Villagers");
        AchievementTranslationMapping.add("stat.tradedWithVillager", "Traded with Villagers");
        AchievementTranslationMapping.add("stat.cakeSlicesEaten", "Cake Slices Eaten");
        AchievementTranslationMapping.add("stat.cauldronFilled", "Cauldrons Filled");
        AchievementTranslationMapping.add("stat.cauldronUsed", "Water Taken from Cauldron");
        AchievementTranslationMapping.add("stat.armorCleaned", "Armor Pieces Cleaned");
        AchievementTranslationMapping.add("stat.bannerCleaned", "Banners Cleaned");
        AchievementTranslationMapping.add("stat.brewingstandInteraction", "Interactions with Brewing Stand");
        AchievementTranslationMapping.add("stat.beaconInteraction", "Interactions with Beacon");
        AchievementTranslationMapping.add("stat.dropperInspected", "Droppers Searched");
        AchievementTranslationMapping.add("stat.hopperInspected", "Hoppers Searched");
        AchievementTranslationMapping.add("stat.dispenserInspected", "Dispensers Searched");
        AchievementTranslationMapping.add("stat.noteblockPlayed", "Note Blocks Played");
        AchievementTranslationMapping.add("stat.noteblockTuned", "Note Blocks Tuned");
        AchievementTranslationMapping.add("stat.flowerPotted", "Plants Potted");
        AchievementTranslationMapping.add("stat.trappedChestTriggered", "Trapped Chests Triggered");
        AchievementTranslationMapping.add("stat.enderchestOpened", "Ender Chests Opened");
        AchievementTranslationMapping.add("stat.itemEnchanted", "Items Enchanted");
        AchievementTranslationMapping.add("stat.recordPlayed", "Records Played");
        AchievementTranslationMapping.add("stat.furnaceInteraction", "Interactions with Furnace");
        AchievementTranslationMapping.add("stat.workbenchInteraction", "Interactions with Crafting Table");
        AchievementTranslationMapping.add("stat.chestOpened", "Chests Opened");
        AchievementTranslationMapping.add("stat.shulkerBoxOpened", "Shulker Boxes Opened");
        AchievementTranslationMapping.add("stat.sleepInBed", "Times Slept in a Bed");
        AchievementTranslationMapping.add("stat.mineBlock", "%1$s Mined");
        AchievementTranslationMapping.add("stat.craftItem", "%1$s Crafted");
        AchievementTranslationMapping.add("stat.useItem", "%1$s Used");
        AchievementTranslationMapping.add("stat.breakItem", "%1$s Depleted");
        AchievementTranslationMapping.add("achievement.get", "Achievement get!");
        AchievementTranslationMapping.add("achievement.taken", "Taken!");
        AchievementTranslationMapping.add("achievement.unknown", "???");
        AchievementTranslationMapping.add("achievement.requires", "Requires '%1$s'");
        AchievementTranslationMapping.add("achievement.openInventory", "Taking Inventory");
        AchievementTranslationMapping.add("achievement.openInventory.desc", "Press 'E' to open your inventory");
        AchievementTranslationMapping.add("achievement.mineWood", "Getting Wood");
        AchievementTranslationMapping.add("achievement.mineWood.desc", "Attack a tree until a block of wood pops out");
        AchievementTranslationMapping.add("achievement.buildWorkBench", "Benchmarking");
        AchievementTranslationMapping.add("achievement.buildWorkBench.desc", "Craft a workbench with four blocks of planks");
        AchievementTranslationMapping.add("achievement.buildPickaxe", "Time to Mine!");
        AchievementTranslationMapping.add("achievement.buildPickaxe.desc", "Use planks and sticks to make a pickaxe");
        AchievementTranslationMapping.add("achievement.buildFurnace", "Hot Topic");
        AchievementTranslationMapping.add("achievement.buildFurnace.desc", "Construct a furnace out of eight cobblestone blocks");
        AchievementTranslationMapping.add("achievement.acquireIron", "Acquire Hardware");
        AchievementTranslationMapping.add("achievement.acquireIron.desc", "Smelt an iron ingot");
        AchievementTranslationMapping.add("achievement.buildHoe", "Time to Farm!");
        AchievementTranslationMapping.add("achievement.buildHoe.desc", "Use planks and sticks to make a hoe");
        AchievementTranslationMapping.add("achievement.makeBread", "Bake Bread");
        AchievementTranslationMapping.add("achievement.makeBread.desc", "Turn wheat into bread");
        AchievementTranslationMapping.add("achievement.bakeCake", "The Lie");
        AchievementTranslationMapping.add("achievement.bakeCake.desc", "Wheat, sugar, milk and eggs!");
        AchievementTranslationMapping.add("achievement.buildBetterPickaxe", "Getting an Upgrade");
        AchievementTranslationMapping.add("achievement.buildBetterPickaxe.desc", "Construct a better pickaxe");
        AchievementTranslationMapping.addSpecial("achievement.overpowered", "Overpowered");
        AchievementTranslationMapping.add("achievement.overpowered.desc", "Eat a Notch apple");
        AchievementTranslationMapping.add("achievement.cookFish", "Delicious Fish");
        AchievementTranslationMapping.add("achievement.cookFish.desc", "Catch and cook fish!");
        AchievementTranslationMapping.addSpecial("achievement.onARail", "On A Rail");
        AchievementTranslationMapping.add("achievement.onARail.desc", "Travel by minecart at least 1 km from where you started");
        AchievementTranslationMapping.add("achievement.buildSword", "Time to Strike!");
        AchievementTranslationMapping.add("achievement.buildSword.desc", "Use planks and sticks to make a sword");
        AchievementTranslationMapping.add("achievement.killEnemy", "Monster Hunter");
        AchievementTranslationMapping.add("achievement.killEnemy.desc", "Attack and destroy a monster");
        AchievementTranslationMapping.add("achievement.killCow", "Cow Tipper");
        AchievementTranslationMapping.add("achievement.killCow.desc", "Harvest some leather");
        AchievementTranslationMapping.add("achievement.breedCow", "Repopulation");
        AchievementTranslationMapping.add("achievement.breedCow.desc", "Breed two cows with wheat");
        AchievementTranslationMapping.addSpecial("achievement.flyPig", "When Pigs Fly");
        AchievementTranslationMapping.add("achievement.flyPig.desc", "Fly a pig off a cliff");
        AchievementTranslationMapping.addSpecial("achievement.snipeSkeleton", "Sniper Duel");
        AchievementTranslationMapping.add("achievement.snipeSkeleton.desc", "Kill a skeleton with an arrow from more than 50 meters");
        AchievementTranslationMapping.add("achievement.diamonds", "DIAMONDS!");
        AchievementTranslationMapping.add("achievement.diamonds.desc", "Acquire diamonds with your iron tools");
        AchievementTranslationMapping.add("achievement.diamondsToYou", "Diamonds to you!");
        AchievementTranslationMapping.add("achievement.diamondsToYou.desc", "Throw diamonds at another player");
        AchievementTranslationMapping.add("achievement.portal", "We Need to Go Deeper");
        AchievementTranslationMapping.add("achievement.portal.desc", "Build a portal to the Nether");
        AchievementTranslationMapping.addSpecial("achievement.ghast", "Return to Sender");
        AchievementTranslationMapping.add("achievement.ghast.desc", "Destroy a Ghast with a fireball");
        AchievementTranslationMapping.add("achievement.blazeRod", "Into Fire");
        AchievementTranslationMapping.add("achievement.blazeRod.desc", "Relieve a Blaze of its rod");
        AchievementTranslationMapping.add("achievement.potion", "Local Brewery");
        AchievementTranslationMapping.add("achievement.potion.desc", "Brew a potion");
        AchievementTranslationMapping.addSpecial("achievement.theEnd", "The End?");
        AchievementTranslationMapping.add("achievement.theEnd.desc", "Locate the End");
        AchievementTranslationMapping.addSpecial("achievement.theEnd2", "The End.");
        AchievementTranslationMapping.add("achievement.theEnd2.desc", "Defeat the Ender Dragon");
        AchievementTranslationMapping.add("achievement.spawnWither", "The Beginning?");
        AchievementTranslationMapping.add("achievement.spawnWither.desc", "Spawn the Wither");
        AchievementTranslationMapping.add("achievement.killWither", "The Beginning.");
        AchievementTranslationMapping.add("achievement.killWither.desc", "Kill the Wither");
        AchievementTranslationMapping.addSpecial("achievement.fullBeacon", "Beaconator");
        AchievementTranslationMapping.add("achievement.fullBeacon.desc", "Create a full beacon");
        AchievementTranslationMapping.addSpecial("achievement.exploreAllBiomes", "Adventuring Time");
        AchievementTranslationMapping.add("achievement.exploreAllBiomes.desc", "Discover all biomes");
        AchievementTranslationMapping.add("achievement.enchantments", "Enchanter");
        AchievementTranslationMapping.add("achievement.enchantments.desc", "Use a book, obsidian and diamonds to construct an enchantment table");
        AchievementTranslationMapping.addSpecial("achievement.overkill", "Overkill");
        AchievementTranslationMapping.add("achievement.overkill.desc", "Deal nine hearts of damage in a single hit");
        AchievementTranslationMapping.add("achievement.bookcase", "Librarian");
        AchievementTranslationMapping.add("achievement.bookcase.desc", "Build some bookshelves to improve your enchantment table");
    }
}

