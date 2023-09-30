package de.verschwiegener.atero.util.inventory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.DamageSource;

public class Group implements Wrapper{
    String name;
    private int algorithm;
    private int preferedCell;
    private int maxCellAmount;
    private ArrayList<Integer> itemIDs;
    private ArrayList<Integer> inventoryIDs = new ArrayList<>();

    public Group(String name, int algorithm, int preferedCell, int maxCellAmount, Integer[] ids) {
        this.name = name;
        this.algorithm = algorithm;
        this.preferedCell = preferedCell;
        this.maxCellAmount = maxCellAmount;
        this.itemIDs = new ArrayList<>();
        itemIDs.addAll(Arrays.asList(ids));
    }
    public ArrayList<Integer> getItemIDs() {
        return itemIDs;
    }
    public void reset() {
        inventoryIDs.clear();
    }

    public ArrayList<Integer> getBest() {
        ArrayList<Integer> trashIds = new ArrayList<>();
        if (!inventoryIDs.isEmpty()) {
            if (inventoryIDs.size() > 0) {
                switch (algorithm) {
                    case 0:
                        Collections.sort(inventoryIDs, new Comparator<Integer>() {
                            @Override
                            public int compare(Integer o1, Integer o2) {
                                ItemStack itemStack = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o1)
                                        .getStack();
                                ItemStack beststack = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(o2)
                                        .getStack();
                                if (itemStack.stackSize > beststack.stackSize) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            }
                        });
                        break;
                    case 1:
                        Collections.sort(inventoryIDs, new Comparator<Integer>() {

                            @Override
                            public int compare(Integer o1, Integer o2) {
                                if (compareDamage(o1, o2)) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            }
                        });
                        break;
                    case 2:
                        Collections.sort(inventoryIDs, new Comparator<Integer>() {

                            @Override
                            public int compare(Integer o1, Integer o2) {
                                if (compareArmor(o1, o2)) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            }
                        });
                        break;
                }
            }

            if (inventoryIDs.size() > maxCellAmount && maxCellAmount != 0) {
                for (int i = maxCellAmount; i < inventoryIDs.size(); i++) {
                    trashIds.add(inventoryIDs.get(i));
                }
            }
        }
        return trashIds;
    }

    public void putArmor(boolean hotbar) {
        String[] armorType = new String[] { "item.helmet", "item.chestplate", "item.leggings", "item.boots" };
        if (algorithm == 2) {
            if (!inventoryIDs.isEmpty()) {
                ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(inventoryIDs.get(0)).getStack();
                String name = itemStack.getItem().getUnlocalizedName();
                int offset = 0;
                for (String str : armorType) {
                    if (name.startsWith(str)) {
                        offset = Arrays.asList(armorType).indexOf(str);
                    }
                }

                ItemStack armorStack = Minecraft.thePlayer.inventoryContainer.getSlot(offset + 5).getStack();

                if (armorStack == null) {
                    this.mc.playerController.windowClick(0, offset + 5, 1, 4, Minecraft.thePlayer);
                    this.mc.playerController.windowClick(0, inventoryIDs.get(0), 0, 1, Minecraft.thePlayer);
                } else {
                    if (compareArmor(itemStack, armorStack)) {
                        if(!hotbar) {
                            this.mc.playerController.windowClick(0, offset + 5, 1, 4, Minecraft.thePlayer);
                            this.mc.playerController.windowClick(0, inventoryIDs.get(0), 0, 1, Minecraft.thePlayer);
                        }
                    }else if(itemStack != armorStack){
                        mc.playerController.windowClick(0, inventoryIDs.get(0), 1, 4, mc.thePlayer);
                    }
                }
            }
        }
    }

    public void putInBestSlot(boolean hotbar) {
        if(hotbar) {
            if(preferedCell > 1 || preferedCell < 9)
                return;
        }
        if(algorithm == 2 || inventoryIDs.isEmpty())
            return;
        this.mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, inventoryIDs.get(0), preferedCell - 1, 2, Minecraft.thePlayer);
    }

    private boolean compareDamage(int id, int id2) {
        ItemStack item1 = mc.thePlayer.inventoryContainer.getSlot(id).getStack();
        ItemStack item2 = mc.thePlayer.inventoryContainer.getSlot(id2).getStack();

        float damageWeapon1 = item1.getItem() instanceof ItemSword ? getDamageEnchant(item1) : ((ItemTool) item1.getItem()).getDamageVsEntity();
        float damageWeapon2 = item2.getItem() instanceof ItemSword ? getDamageEnchant(item2) : ((ItemTool) item2.getItem()).getDamageVsEntity();

        return damageWeapon1 > damageWeapon2 || (damageWeapon1 == damageWeapon2 && item1.getItemDamage() < item2.getItemDamage());
    }

    private boolean compareArmor(int id, int id2) {
        ItemStack item1 = mc.thePlayer.inventoryContainer.getSlot(id).getStack();
        ItemStack item2 = mc.thePlayer.inventoryContainer.getSlot(id2).getStack();
        return compareArmor(item1, item2);
    }

    private boolean compareArmor(ItemStack item1, ItemStack item2) {
        if (item2 != null) {
            ItemArmor armor = (ItemArmor) item1.getItem();
            ItemArmor armor2 = (ItemArmor) item2.getItem();
            float damageReduceAmount = armor.damageReduceAmount
                    + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { item1 }, DamageSource.generic);
            float damageReduceAmount2 = armor2.damageReduceAmount
                    + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { item2 }, DamageSource.generic);
            if (damageReduceAmount > damageReduceAmount2
                    || (damageReduceAmount == damageReduceAmount2 && item1.getItemDamage() < item2.getItemDamage())) {
                return true;
            }
            return false;
        }
        return true;
    }

    private float getDamageEnchant(ItemStack stack) {
        ItemSword sword = (ItemSword) stack.getItem();
        float sharpness = (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
        float fireAspect = (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1.5F;
        return sword.getDamageVsEntity() + sharpness + fireAspect;
    }
    public ArrayList<Integer> getInventoryIDs() {
        return inventoryIDs;
    }

}
