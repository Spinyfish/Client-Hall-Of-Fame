package tech.dort.dortware.impl.modules.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.util.DamageSource;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.inventory.InventoryUtils;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.time.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jinthium
 */

public class InventoryManager extends Module {

    private final NumberValue swordDelay = new NumberValue("Sword Delay", this, 500, 25, 1000, SliderUnit.MS, true);
    private final NumberValue delay = new NumberValue("Delay", this, 150, 25, 500, SliderUnit.MS, true);

    public InventoryManager(ModuleData moduleData) {
        super(moduleData);
        register(swordDelay, delay);
    }

    private final Stopwatch stopwatch = new Stopwatch();
    private final List<Integer> allSwords = new ArrayList<>();
    private final List[] allArmor = new List[4];
    private final List<Integer> trash = new ArrayList<>();
    private boolean cleaning;
    private int[] bestArmorSlot;
    private int bestSwordSlot;

    @Subscribe
    public void onEvent(UpdateEvent event) {
        if (mc.currentScreen == null && event.isPre()) {
            collectItems();
            collectBestArmor();
            collectTrash();
            int trashSize = trash.size();
            boolean trashPresent = trashSize > 0;
            int windowId = mc.thePlayer.inventoryContainer.windowId;
            int bestSwordSlot = this.bestSwordSlot;
            if (trashPresent) {
                if (!cleaning) {
                    cleaning = true;
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
                }

                for (Object o : trash) {
                    int slot = (Integer) o;
                    if (stopwatch.timeElapsed(delay.getValue().longValue())) {
                        mc.playerController.windowClick(windowId, slot < 9 ? slot + 36 : slot, 1, 4, mc.thePlayer);
                        stopwatch.resetTime();
                    }
                }

                if (cleaning) {
                    PacketUtil.sendPacketNoEvent(new C0DPacketCloseWindow(windowId));
                    cleaning = false;
                }
            }

            if (bestSwordSlot != -1 && stopwatch.timeElapsed(swordDelay.getValue().longValue())) {
                mc.playerController.windowClick(windowId, bestSwordSlot < 9 ? bestSwordSlot + 36 : bestSwordSlot, 0, 2, mc.thePlayer);
                stopwatch.resetTime();
            }
        }
    }

    public void collectItems() {
        bestSwordSlot = -1;
        allSwords.clear();
        float bestSwordDamage = -1.0F;

        for (int i = 0; i < 36; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemSword) {
                float damageLevel = InventoryUtils.getDamageLevel(itemStack);
                allSwords.add(i);
                if (bestSwordDamage < damageLevel) {
                    bestSwordDamage = damageLevel;
                    bestSwordSlot = i;
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
            if (itemStack != null && itemStack.getItem() != null && !(itemStack.getItem() instanceof ItemBook) && !(itemStack.getDisplayName().contains("Hype")) && !(itemStack.getDisplayName().contains("Jogadores")) && !(itemStack.getDisplayName().startsWith("\2476")) && !(itemStack.getDisplayName().contains("Compass")) && !(itemStack.getDisplayName().contains("Emerald")) && !(itemStack.getDisplayName().contains("Gadget")) && !InventoryUtils.isValidItem(itemStack)) {
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

        for (int allSwordsSize = allSwords.size(); i < allSwordsSize; ++i) {
            Integer slot = allSwords.get(i);
            if (slot != bestSwordSlot) {
                trash.add(slot);
            }
        }
    }
}
