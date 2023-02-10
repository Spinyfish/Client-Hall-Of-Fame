/**
 * @project Myth
 * @author CodeMan
 * @at 11.08.22, 17:51
 */
package dev.myth.features.player;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.inventory.InventoryUtils;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.NumberSetting;
import javafx.util.Pair;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.*;

import java.util.ArrayList;

@Feature.Info(
        name = "InvManager",
        description = "Manages your inventory",
        category = Feature.Category.PLAYER
)
public class InvManagerFeature extends Feature {


    public final BooleanSetting onlyInInv = new BooleanSetting("OnlyInInv", false);
    public final BooleanSetting sortHotbar = new BooleanSetting("Sort Hotbar", false);
    public final BooleanSetting autoArmor = new BooleanSetting("AutoArmor", false);
    public final NumberSetting startDelay = new NumberSetting("Start Delay", 300, 0, 1000, 10).setSuffix("ms");
    public final NumberSetting delay = new NumberSetting("Delay", 100, 0, 1000, 10).setSuffix("ms");

    private long lastClicked = 0;
    private long startd = 0;

    private Pair<ItemStack, Integer> bestSword = null,
            bestPickaxe = null,
            bestAxe = null;
    private ArrayList<Pair<ItemStack, Integer>> bestArmor = new ArrayList<>();

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {

        if(MC.currentScreen instanceof GuiContainer && !(MC.currentScreen instanceof GuiInventory)) return;

        if(onlyInInv.getValue() && !(MC.currentScreen instanceof GuiInventory)) {
            lastClicked = System.currentTimeMillis();
            startd = System.currentTimeMillis();
            return;
        }

        if (System.currentTimeMillis() - lastClicked <= delay.getValue() || System.currentTimeMillis() - startd <= startDelay.getValue() || event.getState() != EventState.PRE) return;

        reset();
        for (int i = 5; i < 45; i++) {
            ItemStack stack = getPlayer().inventoryContainer.getSlot(i).getStack();
            if (stack != null) {

//                doLog("Stack: " + stack.getDisplayName() + " - " + stack.getUnlocalizedName() + " - " + I18n.format(stack.getUnlocalizedName()));
//                if(stack.getDisplayName().equals(I18n.format(stack.getUnlocalizedName()))) continue;

                if(stack.getItem() instanceof ItemSword) {
                    if(bestSword == null || InventoryUtils.getAttackDamage(stack) > InventoryUtils.getAttackDamage(bestSword.getKey())) {
                        if(bestSword != null) {
                            dropStack(bestSword.getValue());
                            return;
                        }
                        bestSword = new Pair<>(stack, i);
                    } else {
                        dropStack(i);
                        return;
                    }
                } else if(stack.getItem() instanceof ItemPickaxe) {
                    if(bestPickaxe == null || InventoryUtils.getToolEfficiency(stack) > InventoryUtils.getToolEfficiency(bestPickaxe.getKey())) {
                        if(bestPickaxe != null) {
                            dropStack(bestPickaxe.getValue());
                            return;
                        }
                        bestPickaxe = new Pair<>(stack, i);
                    } else {
                        dropStack(i);
                        return;
                    }
                } else if(stack.getItem() instanceof ItemAxe) {
                    if(bestAxe == null || InventoryUtils.getToolEfficiency(stack) > InventoryUtils.getToolEfficiency(bestAxe.getKey())) {
                        if(bestAxe != null) {
                            dropStack(bestAxe.getValue());
                            return;
                        }
                        bestAxe = new Pair<>(stack, i);
                    } else {
                        dropStack(i);
                        return;
                    }
                } else if(stack.getItem() instanceof ItemArmor) {
                    Pair<ItemStack, Integer> armor = bestArmor.get(((ItemArmor) stack.getItem()).armorType);

                    if(armor == null || InventoryUtils.getDamageReduction(stack) > InventoryUtils.getDamageReduction(armor.getKey())) {
                        if(armor != null) {
                            dropStack(armor.getValue());
                            return;
                        }
                        armor = new Pair<>(stack, i);
                        bestArmor.set(((ItemArmor) stack.getItem()).armorType, armor);
                    } else {
                        dropStack(i);
                        return;
                    }
                } else {
                    if(!(stack.getItem() instanceof ItemFood)
                            && !(stack.getItem() instanceof ItemPotion)
                            && !(stack.getItem() instanceof ItemEnderPearl)
                            && !(stack.getItem() instanceof ItemBlock)
                            && stack.getItem() != Items.fishing_rod
                            && stack.getItem() != Items.bow && stack.getItem() != Items.arrow) {
                        dropStack(i);
                        return;
                    }
                }
            }
        }

        if(autoArmor.getValue()){
            for (int i = 0; i < 4; i++) {
                Pair<ItemStack, Integer> armor = bestArmor.get(i);
                if (armor != null) {
                    int targetSlot = 5 + i;
                    if (armor.getValue() != targetSlot) {
                        if (getPlayer().inventoryContainer.getSlot(targetSlot).getStack() == null) {
                            InventoryUtils.windowClick(armor.getValue(), 0, InventoryUtils.ClickType.SHIFT_CLICK);
                            lastClicked = System.currentTimeMillis();
                        } else {
                            dropStack(targetSlot);
                        }
                        return;
                    }
                }
            }
        }

        if(!sortHotbar.getValue()) return;

        if(bestSword != null) {
            if(bestSword.getValue() != 36) {
                InventoryUtils.windowClick(bestSword.getValue(), 0, InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                lastClicked = System.currentTimeMillis();
                return;
            }
        }
        if(bestPickaxe != null) {
            if(bestPickaxe.getValue() != 37) {
                InventoryUtils.windowClick(bestPickaxe.getValue(), 1, InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                lastClicked = System.currentTimeMillis();
                return;
            }
        }
        if(bestAxe != null) {
            if(bestAxe.getValue() != 38) {
                InventoryUtils.windowClick(bestAxe.getValue(), 2, InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                lastClicked = System.currentTimeMillis();
                return;
            }
        }

        for(int i = 36; i < 45; i++) {
            ItemStack stack = getPlayer().inventoryContainer.getSlot(i).getStack();
            if(stack != null) {
                if(stack.getItem() == Items.arrow) {
                    InventoryUtils.windowClick(i, 0, InventoryUtils.ClickType.SHIFT_CLICK);
                    lastClicked = System.currentTimeMillis();
                    return;
                }
            }
        }
    };

    @Override
    public String getSuffix() {
        return delay.getValueDisplayString();
    }

    public void dropStack(int index) {
        InventoryUtils.windowClick(index, 1, InventoryUtils.ClickType.DROP_ITEM);
        lastClicked = System.currentTimeMillis();
    }

    public void reset() {
        bestSword = null;
        bestPickaxe = null;
        bestAxe = null;
        bestArmor = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            bestArmor.add(null);
        }
    }


}
