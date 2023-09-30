package dev.tenacity.utils.Skid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPosition;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemSnow;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;

import java.util.ArrayList;

public class BlockUtil
{
    private final ArrayList<Item> nonValidItems;

    private static BlockUtil INSTANCE;

    public static BlockUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BlockUtil();
        }

        return INSTANCE;
    }

    public BlockUtil() {
        (this.nonValidItems = new ArrayList<Item>()).add(Item.getItemById(30));
        this.nonValidItems.add(Item.getItemById(58));
        this.nonValidItems.add(Item.getItemById(116));
        this.nonValidItems.add(Item.getItemById(158));
        this.nonValidItems.add(Item.getItemById(23));
        this.nonValidItems.add(Item.getItemById(6));
        this.nonValidItems.add(Item.getItemById(54));
        this.nonValidItems.add(Item.getItemById(146));
        this.nonValidItems.add(Item.getItemById(130));
        this.nonValidItems.add(Item.getItemById(26));
        this.nonValidItems.add(Item.getItemById(50));
        this.nonValidItems.add(Item.getItemById(76));
        this.nonValidItems.add(Item.getItemById(46));
        this.nonValidItems.add(Item.getItemById(37));
        this.nonValidItems.add(Item.getItemById(38));
    }
    
    public boolean isValidStack(final ItemStack itemStack) {
        final Item item = itemStack.getItem();
        if (item instanceof ItemSlab || item instanceof ItemLeaves || item instanceof ItemSnow || item instanceof ItemBanner || item instanceof ItemFlintAndSteel) {
            return false;
        }
        for (final Item item2 : this.nonValidItems) {
            if (item.equals(item2)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidBock(final BlockPosition blockPosition) {
        final Block block = Minecraft.getMinecraft().theWorld.getBlockState(blockPosition).getBlock();
        return !(block instanceof BlockLiquid) && !(block instanceof BlockAir) && !(block instanceof BlockChest) && !(block instanceof BlockFurnace);
    }
    
    public static boolean isAirBlock(final BlockPosition blockPosition) {
        final Block block = Minecraft.getMinecraft().theWorld.getBlockState(blockPosition).getBlock();
        return block instanceof BlockAir;
    }

}