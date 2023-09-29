package frapppyz.cutefurry.pics.modules.impl.player;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.*;

import java.util.Arrays;

public class InvManager extends Mod {

    private Mode mode = new Mode("Mode", "Normal", "Normal", "Open");
    private Mode delay = new Mode("Ticks", "1", "0", "1", "2", "3", "4", "5");
    public InvManager() {
        super("Manager", "Bye bye shit items", 0, Category.PLAYER);
        addSettings(mode, delay);
    }

    private int ticks;

    public boolean isBestWeapon(ItemStack stack){
        float damage = getDamage(stack);

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getDamage(is) > damage && (is.getItem() instanceof ItemSword))
                    return false;
            }
        }
        if((stack.getItem() instanceof ItemSword)) {
            return true;
        }else{
            return false;
        }

    }

    public void getBestWeapon(int slot){
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(isBestWeapon(is) && getDamage(is) > 0 && (is.getItem() instanceof ItemSword)) {
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, slot-36, 2, mc.thePlayer);
                    break;
                }
            }
        }
    }

    private float getDamage(ItemStack stack) {
        float damage = 0;
        Item item = stack.getItem();
        if(item instanceof ItemSword){
            ItemSword sword = (ItemSword) item;
            damage += sword.attackDamage;
        }
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f +
                EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 2f;
        if(stack.getItemDamage() * 3 > stack.getMaxItemUseDuration()) {
            damage -= 3;
        }
        return damage;
    }

    public void getBestArmor(){
        for(int type = 1; type < 5; type++){
            if(mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()){
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                if(isBestArmor(is, type)){
                    continue;
                } else{
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 4 + type, 1, 4, mc.thePlayer);
                }
            }
            for (int i = 9; i < 45; i++) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if(isBestArmor(is, type) && getProtection(is) > 0){
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                    }
                }
            }
        }
    }

    public boolean isBestArmor(ItemStack stack, int type) {
        float prot = getProtection(stack);
        String strType = "";
        if(type == 1){
            strType = "helmet";
        }else if(type == 2){
            strType = "chestplate";
        }else if(type == 3){
            strType = "legging";
        }else if(type == 4){
            strType = "boots";
        }
        if(!stack.getUnlocalizedName().contains(strType)){
            return false;
        }
        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getProtection(is) > prot && is.getUnlocalizedName().contains(strType))
                    return false;
            }
        }
        return true;
    }

    public static float getProtection(ItemStack stack){
        float prot = 0;
        if ((stack.getItem() instanceof ItemArmor)) {
            ItemArmor armor = (ItemArmor)stack.getItem();
            prot += armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075D;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack)/100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack)/100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack)/100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)/50d;
        }
        return prot;
    }

    public void onEvent(Event e){
        if(e instanceof Update){
            ticks++;
            if(mode.is("Normal")){
                    for (int i = 0; i < 45; i++) {
                        if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && ticks >= Integer.parseInt(delay.getMode())) {
                            ItemStack item = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if(((item.getItem().getUnlocalizedName().contains("stick")) ||
                                    (item.getItem().getUnlocalizedName().contains("string")) ||
                                    (item.getItem().getUnlocalizedName().contains("cake")) ||
                                    (item.getItem().getUnlocalizedName().contains("mushroom")) ||
                                    (item.getItem().getUnlocalizedName().contains("flint")) ||
                                    (item.getItem().getUnlocalizedName().contains("feather")) ||
                                    //(item.getItem().getUnlocalizedName().contains("bucket")) ||
                                    (item.getItem().getUnlocalizedName().contains("shears")) ||
                                    (item.getItem().getUnlocalizedName().contains("torch")) ||
                                    (item.getItem().getUnlocalizedName().contains("seeds")) ||
                                    (item.getItem().getUnlocalizedName().contains("reeds")) ||
                                    (item.getItem().getUnlocalizedName().contains("record")) ||
                                    (item.getItem().getUnlocalizedName().contains("snowball")) ||
                                    (item.getItem().getUnlocalizedName().contains("planks")) ||
                                    (item.getItem() instanceof ItemGlassBottle) ||
                                    (item.getItem().getUnlocalizedName().contains("piston")))){
                                ticks = 0;
                                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);
                            }else if(item.getItem() instanceof ItemSword){
                                try{
                                    if (isBestWeapon(item)) {
                                        getBestWeapon(36);
                                    } else {
                                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);

                                    }

                                }catch(Exception err){err.printStackTrace();}
                                ticks = 0;
                            }else{
                                getBestArmor();
                            }
                        }
                    }

            }else if(mc.currentScreen instanceof GuiInventory){
                    for (int i = 0; i < 45; i++) {
                        if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && ticks >= Integer.parseInt(delay.getMode())) {
                            ItemStack item = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if(((item.getItem().getUnlocalizedName().contains("stick")) ||

                                    (item.getItem().getUnlocalizedName().contains("string")) ||
                                    (item.getItem().getUnlocalizedName().contains("cake")) ||
                                    (item.getItem().getUnlocalizedName().contains("mushroom")) ||
                                    (item.getItem().getUnlocalizedName().contains("flint")) ||
                                    (item.getItem().getUnlocalizedName().contains("feather")) ||
                                    //(item.getItem().getUnlocalizedName().contains("bucket")) ||
                                    (item.getItem().getUnlocalizedName().contains("shears")) ||
                                    (item.getItem().getUnlocalizedName().contains("torch")) ||
                                    (item.getItem().getUnlocalizedName().contains("seeds")) ||
                                    (item.getItem().getUnlocalizedName().contains("reeds")) ||
                                    (item.getItem().getUnlocalizedName().contains("record")) ||
                                    (item.getItem().getUnlocalizedName().contains("snowball")) ||
                                    (item.getItem().getUnlocalizedName().contains("planks")) ||
                                    (item.getItem() instanceof ItemGlassBottle) ||
                                    (item.getItem().getUnlocalizedName().contains("piston")))){
                                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);
                                ticks = 0;
                            }else if(item.getItem() instanceof ItemSword){
                                try{
                                    if (isBestWeapon(item)) {
                                        getBestWeapon(36);
                                    } else {
                                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);

                                    }
                                    ticks = 0;
                                }catch(Exception err){err.printStackTrace();}
                            }else{
                                getBestArmor();
                            }
                    }

                }
            }
        }
    }
}
