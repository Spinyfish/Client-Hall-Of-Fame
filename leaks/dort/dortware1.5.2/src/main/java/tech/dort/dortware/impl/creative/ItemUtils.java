package tech.dort.dortware.impl.creative;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;

import java.util.Base64;
import java.util.UUID;

/**
 * @author - Aidan#1337
 * @created 4/9/2021 at 12:35 AM
 * Do not distribute this code without credit
 * or ill get final on ur ass
 */

public class ItemUtils {

    public static ItemStack getCustomSkull(String name, String url) {
        final String gameProfileData = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", url);
        final String base64Encoded = Base64.getEncoder().encodeToString(gameProfileData.getBytes());
        return getItemStack(String.format("skull 1 3 {SkullOwner:{Id:\"%s\",Name:\"%s\",Properties:{textures:[{Value:\"%s\"}]}}}", UUID.randomUUID(), name, base64Encoded));
    }

    // pasted from skillclient or some shit
    public static ItemStack getItemStack(String command) {
        try {
            command = command.replace('&', '\u00a7');
            String[] args;
            int i = 1;
            int j = 0;
            args = command.split(" ");
            final ResourceLocation resourcelocation = new ResourceLocation(args[0]);
            final Item item = (Item) Item.itemRegistry.getObject(resourcelocation);
            if (args.length >= 2 && args[1].matches("\\d+")) {
                i = Integer.parseInt(args[1]);
            }
            if (args.length >= 3 && args[2].matches("\\d+")) {
                j = Integer.parseInt(args[2]);
            }
            final ItemStack itemstack = new ItemStack(item, i, j);
            if (args.length >= 4) {
                String NBT = "";
                int nbtcount = 3;
                while (nbtcount < args.length) {
                    NBT = NBT + " " + args[nbtcount];
                    ++nbtcount;
                }
                itemstack.setTagCompound(JsonToNBT.func_180713_a(NBT));
            }
            return itemstack;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ItemStack(Blocks.barrier);
        }
    }

    public static String getCustomSkullNBT(String name, String url) {
        final String gameProfileData = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", url);
        final String base64Encoded = Base64.getEncoder().encodeToString(gameProfileData.getBytes());
        return String.format("SkullOwner:{Id:\"%s\",Name:\"%s\",Properties:{textures:[{Value:\"%s\"}]}}", UUID.randomUUID(), name, base64Encoded);
    }
}
