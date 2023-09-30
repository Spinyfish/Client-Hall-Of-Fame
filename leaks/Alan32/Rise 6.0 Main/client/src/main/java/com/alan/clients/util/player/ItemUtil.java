package com.alan.clients.util.player;

import lombok.experimental.UtilityClass;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * @author Auth
 * @since 09/07/2022
 */
@UtilityClass
public class ItemUtil {

    private final List<Item> WHITELISTED_ITEMS = Arrays.asList(Items.fishing_rod, Items.water_bucket, Items.bucket, Items.arrow, Items.bow, Items.snowball, Items.egg, Items.ender_pearl);

    public boolean useful(final ItemStack stack) {
        final Item item = stack.getItem();

        if (item instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) item;
            return ItemPotion.isSplash(stack.getMetadata()) && PlayerUtil.goodPotion(potion.getEffects(stack).get(0).getPotionID());
        }

        if (item instanceof ItemBlock) {
            final Block block = ((ItemBlock) item).getBlock();
            if (block instanceof BlockGlass || block instanceof BlockStainedGlass || (block.isFullBlock() && !(block instanceof BlockTNT || block instanceof BlockSlime || block instanceof BlockFalling))) {
                return true;
            }
        }

        return item instanceof ItemSword ||
                item instanceof ItemTool ||
                item instanceof ItemArmor ||
                item instanceof ItemFood ||
                WHITELISTED_ITEMS.contains(item);
    }

    public ItemStack getCustomSkull(final String name, final String url) {
        final String gameProfileData = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", url);
        final String base64Encoded = Base64.getEncoder().encodeToString(gameProfileData.getBytes());
        return getItemStack(String.format("skull 1 3 {SkullOwner:{Id:\"%s\",Name:\"%s\",Properties:{textures:[{Value:\"%s\"}]}}}", UUID.randomUUID(), name, base64Encoded));
    }

    public ItemStack getItemStack(String command) {
        try {
            command = command.replace('&', '\u00a7');
            final String[] args;
            int i = 1;
            int j = 0;
            args = command.split(" ");
            final ResourceLocation resourcelocation = new ResourceLocation(args[0]);
            final Item item = Item.itemRegistry.getObject(resourcelocation);

            if (args.length >= 2 && args[1].matches("\\d+")) {
                i = Integer.parseInt(args[1]);
            }

            if (args.length >= 3 && args[2].matches("\\d+")) {
                j = Integer.parseInt(args[2]);
            }

            final ItemStack itemstack = new ItemStack(item, i, j);
            if (args.length >= 4) {
                final StringBuilder NBT = new StringBuilder();

                int nbtCount = 3;
                while (nbtCount < args.length) {
                    NBT.append(" ").append(args[nbtCount]);
                    nbtCount++;
                }

                itemstack.setTagCompound(JsonToNBT.getTagFromJson(NBT.toString()));
            }
            return itemstack;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return new ItemStack(Blocks.barrier);
        }
    }

    public String getCustomSkullNBT(final String name, final String url) {
        final String gameProfileData = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", url);
        final String base64Encoded = Base64.getEncoder().encodeToString(gameProfileData.getBytes());
        return String.format("SkullOwner:{Id:\"%s\",Name:\"%s\",Properties:{textures:[{Value:\"%s\"}]}}", UUID.randomUUID(), name, base64Encoded);
    }
}
