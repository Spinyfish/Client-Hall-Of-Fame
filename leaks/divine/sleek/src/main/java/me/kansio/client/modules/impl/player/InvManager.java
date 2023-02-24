package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.gui.clickgui.frame.ClickGUI;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.utils.math.Stopwatch;
import me.kansio.client.utils.network.PacketUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ModuleData(
        name = "Inventory Manager",
        category = ModuleCategory.PLAYER,
        description = "Automatically manages your inventory"
)
public class InvManager extends Module {

    private BooleanValue autoSword = new BooleanValue("AutoSword", this, false);
    private BooleanValue aSwordInInv = new BooleanValue("Sword Only in Inv", this, false, autoSword);
    private NumberValue<Double> aSwordDelay = new NumberValue<>("AutoSword Delay", this, 25.0, 0.0, 1000.0, 1.0, autoSword);

    private BooleanValue invCleaner = new BooleanValue("Inv Cleaner", this, false);
    private BooleanValue invCleanerInInv = new BooleanValue("Clean Only in Inv", this, false, invCleaner);
    private NumberValue<Double> invCleanerDelay = new NumberValue<>("InvCleaner Delay", this, 25.0, 0.0, 1000.0, 1.0, invCleaner);

    private BooleanValue autoArmor = new BooleanValue("Auto Armor", this, false);
    private BooleanValue autoArmorInInv = new BooleanValue("Armor Only in Inv", this, false, autoArmor);
    private NumberValue<Double> autoArmorDelay = new NumberValue<>("AutoArmor Delay", this, 25.0, 0.0, 1000.0, 1.0, autoArmor);

    private BooleanValue disableIfSlimeBall = new BooleanValue("Disable if compass", this, true);

    private Stopwatch armorStop = new Stopwatch();
    private Stopwatch invStop = new Stopwatch();
    private Stopwatch swordStop = new Stopwatch();

    private final Stopwatch stopwatch = new Stopwatch();
    private final List<Integer> allSwords = new ArrayList<>();
    private final List[] allArmor = new List[4];
    private final List<Integer> trash = new ArrayList<>();
    private boolean cleaning;
    private int[] bestArmorSlot;
    private int bestSwordSlot;

    public void onEnable() {

    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (!event.isPre()) return;
        if (mc.currentScreen instanceof GuiChest) return;

        if (mc.thePlayer.isMoving() || !mc.thePlayer.onGround) {
            return;
        }

        if (disableIfSlimeBall.getValue() && (mc.thePlayer.inventory.hasItem(Items.compass) || mc.thePlayer.inventory.hasItem(Items.slime_ball))) {
            return;
        }

        collectItems();
        collectBestArmor();
        collectTrash();
        int trashSize = trash.size();
        boolean trashPresent = trashSize > 0;
        int windowId = mc.thePlayer.inventoryContainer.windowId;
        int bestSwordSlot = this.bestSwordSlot;
        if (autoArmor.getValue()) {
            if (!autoArmorInInv.getValue()) {
                equipArmor(armorStop, autoArmorDelay.getValue().intValue());
            } else if (mc.currentScreen instanceof GuiInventory) {
                equipArmor(armorStop, autoArmorDelay.getValue().intValue());
            }
        }

        if (autoSword.getValue()) {
            if (!aSwordInInv.getValue()) {
                autoSwordThing(bestSwordSlot, swordStop, windowId);
            } else if (mc.currentScreen instanceof GuiInventory) {
                autoSwordThing(bestSwordSlot, swordStop, windowId);
            }
        }

        if (invCleaner.getValue()) {
            if (!invCleanerInInv.getValue()) {
                invCleanerThing(invStop, invCleanerDelay.getValue().intValue(), trash, windowId);
            } else if (mc.currentScreen instanceof GuiInventory) {
                invCleanerThing(invStop, invCleanerDelay.getValue().intValue(), trash, windowId);
            }
        }

    }


    private void equipArmor(Stopwatch stopwatch, int delay) {

        for (int i = 9; i < 45; i++) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
                continue;

            ItemStack stackInSlot = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (!(stackInSlot.getItem() instanceof ItemArmor))
                continue;

            if (getArmorItemsEquipSlot(stackInSlot, false) == -1)
                continue;

            if (mc.thePlayer.getEquipmentInSlot(getArmorItemsEquipSlot(stackInSlot, true)) == null) {
                if (stopwatch.timeElapsed(delay)) {
                    mc.thePlayer.sendQueue.addToSendQueue(
                            new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));

                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0,
                            mc.thePlayer);
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
                            getArmorItemsEquipSlot(stackInSlot, false), 0, 0, mc.thePlayer);

                    stopwatch.resetTime();
                    return;
                }
            } else {
                ItemStack stackInEquipmentSlot = mc.thePlayer
                        .getEquipmentInSlot(getArmorItemsEquipSlot(stackInSlot, true));
                if (compareProtection(stackInSlot, stackInEquipmentSlot) == stackInSlot) {
                    System.out.println("Stack in slot : " + stackInSlot.getUnlocalizedName());
                    if (stopwatch.timeElapsed(delay)) {
                        int finalI1 = i;
                        mc.thePlayer.sendQueue.addToSendQueue(
                                new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));

                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, finalI1, 0, 0,
                                mc.thePlayer);
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
                                getArmorItemsEquipSlot(stackInSlot, false), 0, 0, mc.thePlayer);
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, finalI1, 0, 0,
                                mc.thePlayer);

                        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                        stopwatch.resetTime();
                        return;
                    }
                }
            }
        }
    }

    private void invCleanerThing(Stopwatch stopwatch, int delay, List<Integer> trash, int windowId) {
        if (!(mc.currentScreen instanceof GuiInventory) || !(mc.currentScreen instanceof ClickGUI)) {
            PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
        }

        for (Integer slot : trash) {
            if (stopwatch.timeElapsed(delay)) {
                mc.playerController.windowClick(windowId, slot < 9 ? slot + 36 : slot, 1, 4, mc.thePlayer);
                stopwatch.resetTime();
            }
        }
        if (!(mc.currentScreen instanceof GuiInventory)) {
            PacketUtil.sendPacketNoEvent(new C0DPacketCloseWindow(windowId));
        }
    }

    private void autoSwordThing(int bestSwordSlot, Stopwatch stopwatch, int windowId) {
        if (bestSwordSlot != -1 && stopwatch.timeElapsed(aSwordDelay.getValue().intValue())) {
            mc.playerController.windowClick(windowId, bestSwordSlot < 9 ? bestSwordSlot + 36 : bestSwordSlot, 0, 2, mc.thePlayer);
            stopwatch.resetTime();
        }
    }

    private boolean isTrash(ItemStack item) {
        return ((item.getItem().getUnlocalizedName().contains("tnt")) || item.getDisplayName().contains("frog") ||
                (item.getItem().getUnlocalizedName().contains("stick")) ||
                (item.getItem().getUnlocalizedName().contains("string")) || (item.getItem().getUnlocalizedName().contains("flint")) ||
                (item.getItem().getUnlocalizedName().contains("feather")) || (item.getItem().getUnlocalizedName().contains("bucket")) ||
                (item.getItem().getUnlocalizedName().contains("snow")) || (item.getItem().getUnlocalizedName().contains("enchant")) ||
                (item.getItem().getUnlocalizedName().contains("exp")) || (item.getItem().getUnlocalizedName().contains("shears")) ||
                (item.getItem().getUnlocalizedName().contains("arrow")) || (item.getItem().getUnlocalizedName().contains("anvil")) ||
                (item.getItem().getUnlocalizedName().contains("torch")) || (item.getItem().getUnlocalizedName().contains("seeds")) ||
                (item.getItem().getUnlocalizedName().contains("leather")) || (item.getItem().getUnlocalizedName().contains("boat")) ||
                (item.getItem().getUnlocalizedName().contains("fishing")) || (item.getItem().getUnlocalizedName().contains("wheat")) ||
                (item.getItem().getUnlocalizedName().contains("flower")) || (item.getItem().getUnlocalizedName().contains("record")) ||
                (item.getItem().getUnlocalizedName().contains("note")) || (item.getItem().getUnlocalizedName().contains("sugar")) ||
                (item.getItem().getUnlocalizedName().contains("wire")) || (item.getItem().getUnlocalizedName().contains("trip")) ||
                (item.getItem().getUnlocalizedName().contains("slime")) || (item.getItem().getUnlocalizedName().contains("web")) ||
                ((item.getItem() instanceof ItemGlassBottle)) || (item.getItem().getUnlocalizedName().contains("piston")) ||
                (item.getItem().getUnlocalizedName().contains("potion") && (isBadPotion(item))) ||
                //   ((item.getItem() instanceof ItemArmor) && isBestArmor(item)) ||
                (item.getItem() instanceof ItemEgg || (item.getItem().getUnlocalizedName().contains("bow")) && !item.getDisplayName().contains("Kit")) ||
                //   ((item.getItem() instanceof ItemSword) && !isBestSword(item)) ||
                (item.getItem().getUnlocalizedName().contains("Raw")));
    }

    private int getArmorItemsEquipSlot(ItemStack stack, boolean equipmentSlot) {
        if (stack.getUnlocalizedName().contains("helmet"))
            return equipmentSlot ? 4 : 5;
        if (stack.getUnlocalizedName().contains("chestplate"))
            return equipmentSlot ? 3 : 6;
        if (stack.getUnlocalizedName().contains("leggings"))
            return equipmentSlot ? 2 : 7;
        if (stack.getUnlocalizedName().contains("boots"))
            return equipmentSlot ? 1 : 8;
        return -1;
    }

    private ItemStack compareProtection(ItemStack item1, ItemStack item2) {
        if (!(item1.getItem() instanceof ItemArmor) && !(item2.getItem() instanceof ItemArmor))
            return null;

        if (!(item1.getItem() instanceof ItemArmor))
            return item2;

        if (!(item2.getItem() instanceof ItemArmor))
            return item1;

        if (getArmorProtection(item1) > getArmorProtection(item2)) {
            return item1;
        } else if (getArmorProtection(item2) > getArmorProtection(item1)) {
            return item2;
        }

        return null;
    }

    private double getArmorProtection(ItemStack armorStack) {
        if (!(armorStack.getItem() instanceof ItemArmor))
            return 0.0;

        final ItemArmor armorItem = (ItemArmor) armorStack.getItem();
        final double protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId,
                armorStack);

        return armorItem.damageReduceAmount + ((6 + protectionLevel * protectionLevel) * 0.75 / 3);

    }

    public void collectItems() {
        bestSwordSlot = -1;
        allSwords.clear();
        float bestSwordDamage = -1.0F;

        for (int i = 0; i < 36; ++i) {

            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() != null) {
                if (itemStack.getItem() instanceof ItemSword) {

                    float damageLevel = getDamageLevel(itemStack);
                    allSwords.add(i);
                    if (bestSwordDamage < damageLevel) {
                        bestSwordDamage = damageLevel;
                        bestSwordSlot = i;
                    }
                }
            }
        }
    }

    private void collectBestArmor() {
        int[] bestArmorDamageReduction = new int[4];
        bestArmorSlot = new int[4];
        Arrays.fill(bestArmorDamageReduction, -1);
        Arrays.fill(bestArmorSlot, -1);

        int i;
        ItemStack itemStack;
        ItemArmor armor;
        int armorType;
        for (i = 0; i < bestArmorSlot.length; ++i) {
            itemStack = mc.thePlayer.inventory.armorItemInSlot(i);
            allArmor[i] = new ArrayList<>();
            if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemArmor) {
                armor = (ItemArmor) itemStack.getItem();
                armorType = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{itemStack}, DamageSource.generic);
                bestArmorDamageReduction[i] = armorType;
            }
        }

        for (i = 0; i < 36; ++i) {
            itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemArmor) {
                armor = (ItemArmor) itemStack.getItem();
                armorType = 3 - armor.armorType;
                allArmor[armorType].add(i);
                int slotProtectionLevel = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{itemStack}, DamageSource.generic);
                if (bestArmorDamageReduction[armorType] < slotProtectionLevel) {
                    bestArmorDamageReduction[armorType] = slotProtectionLevel;
                    bestArmorSlot[armorType] = i;
                }
            }
        }
    }

    private void collectTrash() {
        trash.clear();

        int i;
        for (i = 0; i < 36; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null)
                if (itemStack.getItem() != null && !(itemStack.getItem() instanceof ItemBook) && !(itemStack.getDisplayName().contains("Hype")) && !(itemStack.getDisplayName().contains("Jogadores")) && !(itemStack.getDisplayName().startsWith("\2476")) && !(itemStack.getDisplayName().contains("Emerald")) && !(itemStack.getDisplayName().contains("Gadget")) && !isValidItem(itemStack)) {
                    trash.add(i);
                }
        }

        for (i = 0; i < allArmor.length; ++i) {
            List armorItem = allArmor[i];
            if (armorItem != null) {
                int i1 = 0;

                for (int armorItemSize = armorItem.size(); i1 < armorItemSize; ++i1) {
                    Integer slot = (Integer) armorItem.get(i1);
                    if (slot != bestArmorSlot[i]) {
                        trash.add(slot);
                    }
                }
            }
        }


        for (i = 0; i < allSwords.size(); ++i) {

            Integer slot = allSwords.get(i);
            if (slot != bestSwordSlot) {
                trash.add(slot);
            }
        }
    }

    private boolean isValidItem(ItemStack itemStack) {
        if (itemStack.getDisplayName().startsWith("\247a")) {
            return true;
        } else {
            return itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemEnderPearl || itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemTool || itemStack.getItem() instanceof ItemFood || itemStack.getItem() instanceof ItemPotion && !isBadPotion(itemStack) || itemStack.getItem() instanceof ItemBlock || itemStack.getDisplayName().contains("Play") || itemStack.getDisplayName().contains("Game") || itemStack.getDisplayName().contains("Right Click");
        }
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {

                for (Object o : potion.getEffects(stack)) {
                    PotionEffect effect = (PotionEffect) o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private float getDamageLevel(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            ItemSword sword = (ItemSword) stack.getItem();
            float sharpness = (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
            float fireAspect = (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1.5F;
            return sword.getDamageVsEntity() + sharpness + fireAspect;
        } else {
            return 0.0F;
        }
    }
}
