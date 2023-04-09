package tech.dort.dortware.impl.creative;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;

import java.util.List;

/**
 * @author - Aidan#1337
 * @created 4/8/2021 at 11:18 PM
 * Do not distribute this code without credit
 * or ill get final on ur ass
 */

public class DortwareTab extends CreativeTabs {
    private final Minecraft mc = Minecraft.getMinecraft();

    public DortwareTab() {
        super(12, "");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void displayAllRelevantItems(List items) {
        // Hologram
        final ItemStack stack = new ItemStack(Items.armor_stand);
        final NBTTagCompound baseCompound = new NBTTagCompound();
        final NBTTagList posList = new NBTTagList();
        posList.appendTag(new NBTTagDouble(mc.thePlayer.posX));
        posList.appendTag(new NBTTagDouble(mc.thePlayer.posY));
        posList.appendTag(new NBTTagDouble(mc.thePlayer.posZ));
        baseCompound.setBoolean("Invisible", true);
        baseCompound.setBoolean("NoGravity", true);
        baseCompound.setBoolean("CustomNameVisible", true);
        baseCompound.setString("CustomName", "\u00a74D\u00a7fortware");
        baseCompound.setTag("Pos", posList);
        baseCompound.setTag("pose", posList);
        stack.setTagInfo("EntityTag", baseCompound);
        stack.setStackDisplayName("Hologram");
        items.add(stack);

        // Hologram (Via Version)
        final ItemStack stack1 = new ItemStack(Items.armor_stand);
        final NBTTagCompound baseCompound1 = new NBTTagCompound();
        final NBTTagList posList1 = new NBTTagList();
        posList1.appendTag(new NBTTagDouble(mc.thePlayer.posX));
        posList1.appendTag(new NBTTagDouble(mc.thePlayer.posY));
        posList1.appendTag(new NBTTagDouble(mc.thePlayer.posZ));
        baseCompound1.setBoolean("Invisible", true);
        baseCompound1.setBoolean("NoGravity", true);
        baseCompound1.setBoolean("CustomNameVisible", true);
        baseCompound1.setString("CustomName", "\"\u00a74D\u00a7fortware\"");
        baseCompound1.setTag("Pos", posList1);
        stack1.setTagInfo("EntityTag", baseCompound1);
        stack1.setStackDisplayName("Hologram (Via Version)");
        items.add(stack1);

        // Suspicious's Head
        items.add(ItemUtils.getCustomSkull("Suspicious", "https://education.minecraft.net/wp-content/uploads/sus.png"));

        // Hentai's Head
        items.add(ItemUtils.getCustomSkull("Hentai", "https://education.minecraft.net/wp-content/uploads/wtf.png"));

        // Spawn Imposter
        final ItemStack stack2 = ItemUtils.getItemStack("anvil 1 100");
        stack2.setStackDisplayName("Spawn Imposter");
        items.add(stack2);

        // Splash Potion of Instant Death
        final ItemStack stack3 = ItemUtils.getItemStack("potion 1 16385 {CustomPotionEffects:[{Id:6,Amplifier:125,Duration:1000000}]}");
        stack3.setStackDisplayName("Splash Potion of Instant Death");
        items.add(stack3);

        // Dragon Egg
        items.add(ItemUtils.getItemStack("dragon_egg"));

        // Barrier
        items.add(ItemUtils.getItemStack("barrier"));

        // Command Block
        items.add(ItemUtils.getItemStack("command_block"));

        // Command Block Minecart
        items.add(ItemUtils.getItemStack("command_block_minecart"));

        // Alpha Slab
        final ItemStack stack4 = ItemUtils.getItemStack("stone_slab 1 2");
        stack4.setStackDisplayName("Alpha Slab");
        items.add(stack4);

        // Alpha Leaves
        final ItemStack stack5 = ItemUtils.getItemStack("leaves 1 4");
        stack5.setStackDisplayName("Alpha Leaves");
        items.add(stack5);

        // Shrub
        items.add(ItemUtils.getItemStack("tallgrass 1 0"));
    }

    @Override
    public Item getTabIconItem() {
        return Items.flint_and_steel;
    }

    @Override
    public String getTranslatedTabLabel() {
        return "Dortware";
    }

}
