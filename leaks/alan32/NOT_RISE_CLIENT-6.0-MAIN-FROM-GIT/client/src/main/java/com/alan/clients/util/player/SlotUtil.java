package com.alan.clients.util.player;

import com.alan.clients.util.interfaces.InstanceAccess;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

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
                if ((block.isFullBlock() || block instanceof BlockGlass || block instanceof BlockStainedGlass || block instanceof BlockTNT) && !blacklist.contains(block)) {
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

    public int findTool(final BlockPos blockPos) {
        float bestSpeed = 1;
        int bestSlot = -1;

        final IBlockState blockState = mc.theWorld.getBlockState(blockPos);

        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack == null) {
                continue;
            }

            final float speed = itemStack.getStrVsBlock(blockState.getBlock());

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    public ItemStack getCurrentItemInSlot(final int slot) {
        return slot < 9 && slot >= 0 ? mc.thePlayer.inventory.mainInventory[slot] : null;
    }

    public float getStrVsBlock(final Block blockIn, final int slot) {
        float f = 1.0F;

        if (mc.thePlayer.inventory.mainInventory[slot] != null) {
            f *= mc.thePlayer.inventory.mainInventory[slot].getStrVsBlock(blockIn);
        }
        return f;
    }

    public float getPlayerRelativeBlockHardness(final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final int slot) {
        final Block block = mc.theWorld.getBlockState(pos).getBlock();
        final float f = block.getBlockHardness(worldIn, pos);
        return f < 0.0F ? 0.0F : (!canHeldItemHarvest(block, slot) ? getToolDigEfficiency(block, slot) / f / 100.0F : getToolDigEfficiency(block, slot) / f / 30.0F);
    }

    public boolean canHeldItemHarvest(final Block blockIn, final int slot) {
        if (blockIn.getMaterial().isToolNotRequired()) {
            return true;
        } else {
            final ItemStack itemstack = mc.thePlayer.inventory.getStackInSlot(slot);
            return itemstack != null && itemstack.canHarvestBlock(blockIn);
        }
    }

    public float getToolDigEfficiency(final Block blockIn, final int slot) {
        float f = getStrVsBlock(blockIn, slot);

        if (f > 1.0F) {
            final int i = EnchantmentHelper.getEfficiencyModifier(mc.thePlayer);
            final ItemStack itemstack = getCurrentItemInSlot(slot);

            if (i > 0 && itemstack != null) {
                f += (float) (i * i + 1);
            }
        }

        if (mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            f *= 1.0F + (float) (mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }

        if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            final float f1;

            switch (mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;

                case 1:
                    f1 = 0.09F;
                    break;

                case 2:
                    f1 = 0.0027F;
                    break;

                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (mc.thePlayer.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(mc.thePlayer)) {
            f /= 5.0F;
        }

        if (!mc.thePlayer.onGround) {
            f /= 5.0F;
        }

        return f;
    }
}