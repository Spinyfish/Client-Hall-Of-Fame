/* November.lol © 2023 */
package lol.november.utility.player;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.potion.Potion;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class InventoryUtils {

  /**
   * A list of good items
   */
  private static final List<Item> goodItems = Lists.newArrayList(
    Items.golden_apple,
    Items.apple,
    Items.cooked_beef,
    Items.cooked_chicken,
    Items.cooked_fish,
    Items.cooked_mutton,
    Items.cooked_porkchop,
    Items.cooked_rabbit,
    Items.cookie,
    Items.snowball,
    Items.egg,
    Items.rabbit_stew,
    Items.mushroom_stew,
    Items.ender_pearl,
    Items.arrow,
    Items.bow,
    Items.water_bucket,
    Items.lava_bucket
  );

  /**
   * A list of useless blocks
   * i want to kms
   */
  private static final List<Block> uselessBlocks = Lists.newArrayList(
    Blocks.stone_button,
    Blocks.wooden_button,
    Blocks.stone_pressure_plate,
    Blocks.wooden_pressure_plate,
    Blocks.light_weighted_pressure_plate,
    Blocks.heavy_weighted_pressure_plate,
    Blocks.dragon_egg,
    Blocks.brewing_stand,
    Blocks.chest,
    Blocks.ender_chest,
    Blocks.enchanting_table,
    Blocks.cake,
    Blocks.flower_pot,
    Blocks.yellow_flower,
    Blocks.red_flower,
    Blocks.tallgrass,
    Blocks.anvil,
    Blocks.double_plant,
    Blocks.sand,
    Blocks.gravel,
    Blocks.tripwire,
    Blocks.tripwire_hook,
    Blocks.carpet,
    Blocks.unpowered_repeater,
    Blocks.powered_repeater,
    Blocks.redstone_wire,
    Blocks.torch,
    Blocks.redstone_torch,
    Blocks.red_mushroom,
    Blocks.brown_mushroom,
    Blocks.lever,
    Blocks.unlit_redstone_torch,
    Blocks.bed,
    Blocks.snow_layer,
    Blocks.skull,
    Blocks.stone_slab,
    Blocks.stone_slab2,
    Blocks.wooden_slab,
    Blocks.trapdoor,
    Blocks.iron_trapdoor,
    Blocks.spruce_door,
    Blocks.iron_trapdoor,
    Blocks.oak_door,
    Blocks.dark_oak_door,
    Blocks.jungle_door,
    Blocks.birch_door,
    Blocks.acacia_door,
    Blocks.golden_rail,
    Blocks.detector_rail,
    Blocks.rail,
    Blocks.activator_rail,
    Blocks.hopper,
    Blocks.unpowered_comparator,
    Blocks.powered_comparator
  );

  /**
   * Enchantments used on pieces of armor
   */
  private static final List<Enchantment> protectionEnchantments =
    Lists.newArrayList(
      Enchantment.protection,
      Enchantment.projectileProtection,
      Enchantment.blastProtection,
      Enchantment.fireProtection
    );

  /**
   * A list of good potions
   */
  public static final List<Potion> validPotions = Lists.newArrayList(
    Potion.fireResistance,
    Potion.heal,
    Potion.healthBoost,
    Potion.moveSpeed,
    Potion.digSpeed,
    Potion.invisibility,
    Potion.damageBoost,
    Potion.regeneration,
    Potion.resistance,
    Potion.waterBreathing,
    Potion.nightVision,
    Potion.jump
  );

  /**
   * Enchantments commonly on tools
   */
  private static final List<Enchantment> toolEnchantments = Lists.newArrayList(
    Enchantment.unbreaking,
    Enchantment.silkTouch,
    Enchantment.fortune,
    Enchantment.efficiency
  );

  private static final Minecraft mc = Minecraft.getMinecraft();

  /**
   * Spoofs open your inventory
   */
  public static void openInventory() {
    mc.thePlayer.sendQueue.addToSendQueue(
      new C0BPacketEntityAction(mc.thePlayer, Action.OPEN_INVENTORY)
    );
  }

  /**
   * Closes the current inventory
   */
  public static void closeCurrentInventory() {
    mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(windowId()));
  }

  /**
   * Gets the window ID of the container you're in
   *
   * @return the window id of the container you're in
   */
  public static int windowId() {
    Container container = mc.thePlayer.openContainer;
    return container == null ? 0 : container.windowId;
  }

  /**
   * "Fixes" a slot to be used in a window click packet
   *
   * @param slot the slot index
   * @return the packet slot
   */
  public static int fix(int slot) {
    return slot < 9 ? slot + 36 : slot;
  }

  /**
   * Checks if the block is useless
   *
   * @param block the block
   * @return if it is useless (lol)
   */
  public static boolean blockUseless(Block block) {
    return uselessBlocks.contains(block);
  }

  /**
   * Checks if an item is useful
   *
   * @param item the item
   * @return if the item is useful
   */
  public static boolean useful(Item item) {
    if (item instanceof ItemBlock) {
      Block block = ((ItemBlock) item).getBlock();
      return !uselessBlocks.contains(block);
    }

    return goodItems.contains(item);
  }

  /**
   * "Scores" an {@link ItemStack}
   *
   * @param itemStack the item stack
   * @return a score as a float
   */
  public static float score(ItemStack itemStack) {
    if (itemStack == null || itemStack.getItem() == null) return 0.0f;

    if (itemStack.getItem() instanceof ItemArmor itemArmor) {
      float score = itemArmor.damageReduceAmount;

      for (Enchantment enchantment : protectionEnchantments) {
        score +=
          EnchantmentHelper.getEnchantmentLevel(
            enchantment.effectId,
            itemStack
          ) *
          0.75f;
      }

      score +=
        EnchantmentHelper.getEnchantmentLevel(
          Enchantment.unbreaking.effectId,
          itemStack
        ) *
        0.5f;

      return score;
    } else if (itemStack.getItem() instanceof ItemSword sword) {
      float score = 4.0f + sword.getDamageVsEntity();

      score +=
        EnchantmentHelper.getEnchantmentLevel(
          Enchantment.unbreaking.effectId,
          itemStack
        ) *
        0.5f;
      score +=
        EnchantmentHelper.getEnchantmentLevel(
          Enchantment.fireAspect.effectId,
          itemStack
        ) *
        0.5f;
      score +=
        EnchantmentHelper.getEnchantmentLevel(
          Enchantment.sharpness.effectId,
          itemStack
        ) *
        0.5f;

      return score;
    } else if (itemStack.getItem() instanceof ItemTool tool) {
      Block block = null;
      if (tool instanceof ItemPickaxe) {
        block = Blocks.stone;
      } else if (tool instanceof ItemAxe) {
        block = Blocks.log;
      } else if (tool instanceof ItemSpade) {
        block = Blocks.grass;
      }

      float score = block == null
        ? tool.getDamageVsEntity()
        : tool.getStrVsBlock(itemStack, Blocks.stone);

      for (Enchantment enchantment : toolEnchantments) {
        score +=
          EnchantmentHelper.getEnchantmentLevel(
            enchantment.effectId,
            itemStack
          ) *
          0.75f;
      }

      return score;
    } else if (itemStack.getItem() instanceof ItemHoe) {
      float score = itemStack
        .getItem()
        .getStrVsBlock(itemStack, Blocks.hay_block);

      for (Enchantment enchantment : toolEnchantments) {
        score +=
          EnchantmentHelper.getEnchantmentLevel(
            enchantment.effectId,
            itemStack
          ) *
          0.75f;
      }

      return score;
    } else if (itemStack.getItem() instanceof ItemBow) {
      float score = 1.0f;

      score +=
        EnchantmentHelper.getEnchantmentLevel(
          Enchantment.knockback.effectId,
          itemStack
        ) *
        0.75f;
      score +=
        EnchantmentHelper.getEnchantmentLevel(
          Enchantment.flame.effectId,
          itemStack
        ) *
        0.75f;
      score +=
        EnchantmentHelper.getEnchantmentLevel(
          Enchantment.punch.effectId,
          itemStack
        ) *
        0.75f;

      return score;
    }

    return 1.0f;
  }
}
