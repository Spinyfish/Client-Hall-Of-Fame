package com.alan.clients.util.player;

import com.alan.clients.util.interfaces.InstanceAccess;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.Arrays;
import java.util.List;

/**
 * This is a slot util which can be used to do various things related to held item slots
 *
 * @author Auth
 * @since 3/02/2022
 */
@UtilityClass
public class SlotUtil implements InstanceAccess {

    public final List<Block> blacklist = Arrays.asList(Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest,
            Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch,
            Blocks.crafting_table, Blocks.furnace, Blocks.waterlily, Blocks.dispenser,
            Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.noteblock,
            Blocks.dropper, Blocks.tnt, Blocks.standing_banner, Blocks.wall_banner, Blocks.redstone_torch);

    public final List<Block> interactList = Arrays.asList(Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest,
            Blocks.trapped_chest, Blocks.anvil, Blocks.crafting_table, Blocks.furnace, Blocks.dispenser,
            Blocks.iron_door, Blocks.oak_door, Blocks.noteblock, Blocks.dropper);

    /**
     * Gets and returns a slot of a valid block
     *
     * @return slot
     */
    public int findBlock() {
        for (int i = 36; i < 45; i++) {
            final ItemStack item = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (item != null && item.getItem() instanceof ItemBlock && item.stackSize > 0) {
                final Block block = ((ItemBlock) item.getItem()).getBlock();
                if ((block.isFullBlock() || block instanceof BlockGlass || block instanceof BlockStainedGlass) && !blacklist.contains(block)) {
                    return i - 36;
                }
            }
        }

        return -1;
    }

    /**
     * Gets and returns a slot of the best sword
     *
     * @return slot
     */
    public int findSword() {
        int bestDurability = -1;
        float bestDamage = -1;
        int bestSlot = -1;

        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack == null) {
                continue;
            }

            if (itemStack.getItem() instanceof ItemSword) {
                final ItemSword sword = (ItemSword) itemStack.getItem();

                final int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
                final float damage = sword.getDamageVsEntity() + sharpnessLevel * 1.25F;
                final int durability = sword.getMaxDamage();

                if (bestDamage < damage) {
                    bestDamage = damage;
                    bestDurability = durability;
                    bestSlot = i;
                }

                if (damage == bestDamage && durability > bestDurability) {
                    bestDurability = durability;
                    bestSlot = i;
                }
            }
        }

        return bestSlot;
    }

    /**
     * Gets and returns the slot of the specified item if you have the item
     *
     * @return slot
     */
    public int findItem(final Item item) {
        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack == null) {
                if (item == null) {
                    return i;
                }
                continue;
            }

            if (itemStack.getItem() == item) {
                return i;
            }
        }

        return -1;
    }

    public int findBlock(final Block block) {
        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack == null) {
                if (block == null) {
                    return i;
                }
                continue;
            }

            if (itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlock() == block) {
                return i;
            }
        }

        return -1;
    }
}