package de.verschwiegener.atero.util.inventory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtil {

    //private static Integer[][] inventory = new Integer[2][36];
    static Minecraft mc = Minecraft.getMinecraft();
    //public static ArrayList<Integer> trashInventory = new ArrayList<>();
    private static ArrayList<Group> groups = new ArrayList<>();
    public static Queue<Move> queue = new LinkedList<Move>();
    public static int count;
    public static int count2;

    public static void addGroups() {
        groups.add(new Group("Blocks", 0, 3, 0, new Integer[] {1, 2, 3, 4, 5, 24, 35, 41, 42, 45, 48, 46, 57, 79, 80, 95}));
        groups.add(new Group("Weapon", 1, 1, 1, new Integer[] {267, 268, 272, 276, 283}));
        groups.add(new Group("Helmet", 2, 0, 1, new Integer[] {298,302,306,310,314}));
        groups.add(new Group("Leggings", 2, 0, 1, new Integer[] {304, 308, 312, 316}));
        groups.add(new Group("Chestplate", 2, 0, 1, new Integer[] {303, 307, 311, 315}));
        groups.add(new Group("Boots", 2, 0, 1, new Integer[] {301, 305,309,313,317}));
        groups.add(new Group("GApple", 0, 9, 0, new Integer[] {322, 317}));//345
        groups.add(new Group("Compass", 0, 8, 0, new Integer[] {345}));//345


    }

    public static void updateInventory() {
        queue.clear();
        groups.forEach(group -> group.reset());

        for (int i = 0; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); i++) {
            int id = 0;
            ItemStack item = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (item != null) {
                id = Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem());
                boolean trash = true;
                for (Group g : groups) {
                    if (g.getItemIDs().contains(id)) {
                        g.getInventoryIDs().add(i);
                        trash = false;
                    }
                }
                if (trash) {
                    queue.add(new Move(i, 1));
                }
            }
        }
    }
    public static void pollQueue() {
        if(!queue.isEmpty()) {
            Move move = queue.poll();
            System.out.println("Move: " + move.getCurrentInvID() + " target: " + move.getTargetInvID());
            mc.playerController.windowClick(0, move.getCurrentInvID(), 1, 4, mc.thePlayer);
            if(move.getTargetInvID() != 0) {
                mc.playerController.windowClick(0, move.getTargetInvID(), 0, 1, mc.thePlayer);
            }
        }
    }
    public static void getBestofGroup() {
        groups.forEach(group -> group.getBest());
    }

    public static ArrayList<Integer> getGarbageItems(boolean onlyhotbar) {
        groups.forEach(group -> group.reset());
        ArrayList<Integer> garbage = new ArrayList<>();
        int startvalue = (onlyhotbar ? 36 : 0);
        int maxValue = (onlyhotbar ? 45 : mc.thePlayer.inventoryContainer.inventorySlots.size());
        for (int i = startvalue; i < maxValue; i++) {
            int id = 0;
            ItemStack item = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (item != null) {
                //System.out.println("Item: " + item.getItem().getUnlocalizedName());
                id = Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem());
                boolean trash = true;
                for (Group g : groups) {
                    if (g.getItemIDs().contains(id)) {
                        g.getInventoryIDs().add(i);
                        trash = false;
                    }
                }
                if (trash) {
                    garbage.add(i);
                }
            }
        }
        for (Group g : groups) {
            garbage.addAll(g.getBest());
        }
        return garbage;
    }
    public static void putArmor(boolean hotbar) {
        count++;
        if(count + 1 > groups.size()) count = 0;

        groups.get(count).putArmor(hotbar);
    }
    public static void putInBestSlot(boolean hotbar) {
        count2++;
        if(count2 + 1 > groups.size()) count2 = 0;

        groups.get(count2).putInBestSlot(hotbar);
    }

    public static boolean compareItem(ItemStack itemchest) {
        groups.forEach(group -> group.reset());
        int startvalue = 0;
        int maxValue = mc.thePlayer.inventoryContainer.inventorySlots.size();
        for (int i = startvalue; i < maxValue; i++) {
            int id = 0;
            ItemStack item = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (item != null) {
                for (Group g : groups) {
                    if (g.getItemIDs().contains(Item.getIdFromItem(item.getItem()))) {
                        g.getInventoryIDs().add(i);
                    }
                }
            }
        }
        boolean needItem = false;
        for(Group g : groups) {
            if(g.getItemIDs().contains(Item.getIdFromItem(itemchest.getItem()))) {
               // if(g.compareBestItems(itemchest)) {
                    needItem = true;
              //  }
            }
        }
        return needItem;
    }
}
