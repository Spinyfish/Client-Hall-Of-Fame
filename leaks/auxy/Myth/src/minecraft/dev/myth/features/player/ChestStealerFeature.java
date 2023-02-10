/**
 * @project Myth
 * @author CodeMan
 * @at 08.08.22, 17:52
 */
package dev.myth.features.player;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.inventory.InventoryUtils;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;

import java.util.ArrayList;
import java.util.Collections;

@Feature.Info(
        name = "ChestStealer",
        description = "Takes all items from chests",
        category = Feature.Category.PLAYER
)
public class ChestStealerFeature extends Feature {

    public final NumberSetting delay = new NumberSetting("Delay", 100, 0, 1000, 10).setSuffix("ms");
    public final BooleanSetting checkName = new BooleanSetting("Check Name", true);
    public final BooleanSetting autoClose = new BooleanSetting("Auto Close", true);

    private long lastClicked;
    private ArrayList<Integer> toTake;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.getState() != EventState.PRE)
            return;

        if(!(MC.thePlayer.openContainer instanceof ContainerChest)) {
            lastClicked = System.currentTimeMillis();
            return;
        }

        long timeSinceLastClicked = System.currentTimeMillis() - lastClicked;

        if(timeSinceLastClicked < delay.getValue().longValue())
            return;

        ContainerChest containerChest = (ContainerChest) MC.thePlayer.openContainer;
        String chestName = containerChest.getLowerChestInventory().getDisplayName().getUnformattedText();

        if (checkName.getValue()) {
            if(!chestName.equals(I18n.format("container.chest")) && !chestName.equals(I18n.format("container.chestDouble")) && !chestName.contains("Chest")) return;
        }

        ItemStack bestSword = null, bestPickaxe = null, bestAxe = null;
        ItemStack[] bestArmor = new ItemStack[4];

        for(int i = 5; i < 45; i++) {
            ItemStack stack = getPlayer().inventoryContainer.getSlot(i).getStack();
            if (stack != null) {
                if(stack.getItem() instanceof ItemSword) {
                    if(bestSword == null || InventoryUtils.getAttackDamage(stack) > InventoryUtils.getAttackDamage(bestSword)) {
                        bestSword = stack;
                    }
                } else if(stack.getItem() instanceof ItemPickaxe) {
                    if(bestPickaxe == null || InventoryUtils.getToolEfficiency(stack) > InventoryUtils.getToolEfficiency(bestPickaxe)) {
                        bestPickaxe = stack;
                    }
                } else if(stack.getItem() instanceof ItemAxe) {
                    if(bestAxe == null || InventoryUtils.getToolEfficiency(stack) > InventoryUtils.getToolEfficiency(bestAxe)) {
                        bestAxe = stack;
                    }
                } else if(stack.getItem() instanceof ItemArmor) {
                    int type = ((ItemArmor) stack.getItem()).armorType;
                    ItemStack armor = bestArmor[type];

                    if(armor == null || InventoryUtils.getDamageReduction(stack) > InventoryUtils.getDamageReduction(armor)) {
                        bestArmor[type] = armor;
                    }
                }
            }
        }
        toTake.clear();
        int slots = containerChest.getLowerChestInventory().getSizeInventory();
        for (int i = 0; i < slots; i++) {
            ItemStack stack = containerChest.getLowerChestInventory().getStackInSlot(i);
            if (stack != null) {

                if(stack.getItem() instanceof ItemSword) {
                    if(bestSword == null || InventoryUtils.getAttackDamage(stack) > InventoryUtils.getAttackDamage(bestSword)) {
                        toTake.add(i);
                    }
                } else if(stack.getItem() instanceof ItemPickaxe) {
                    if(bestPickaxe == null || InventoryUtils.getToolEfficiency(stack) > InventoryUtils.getToolEfficiency(bestPickaxe)) {
                        toTake.add(i);
                    }
                } else if(stack.getItem() instanceof ItemAxe) {
                    if(bestAxe == null || InventoryUtils.getToolEfficiency(stack) > InventoryUtils.getToolEfficiency(bestAxe)) {
                        toTake.add(i);
                    }
                } else if(stack.getItem() instanceof ItemArmor) {
                    int type = ((ItemArmor) stack.getItem()).armorType;
                    ItemStack armor = bestArmor[type];

                    if(armor == null || InventoryUtils.getDamageReduction(stack) > InventoryUtils.getDamageReduction(armor)) {
                        toTake.add(i);
                    }
                } else {
                    if(stack.getItem() instanceof ItemFood
                            || stack.getItem() instanceof ItemPotion
                            || stack.getItem() instanceof ItemEnderPearl
                            || stack.getItem() instanceof ItemBlock
                            || stack.getItem() == Items.bow || stack.getItem() == Items.arrow) {
                        toTake.add(i);
                    }
                }
            }
        }

        Collections.shuffle(toTake);

        if(!toTake.isEmpty()){
                MC.playerController.windowClick(MC.thePlayer.openContainer.windowId, toTake.get(0), 0, 1, MC.thePlayer);
                lastClicked = System.currentTimeMillis();
                return;
        }

        if(autoClose.getValue() && timeSinceLastClicked > 100) MC.thePlayer.closeScreen();
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if(event.getState() != EventState.RECEIVING) return;
    };

    @Override
    public void onEnable() {
        super.onEnable();
        toTake = new ArrayList<>();
        lastClicked = System.currentTimeMillis();
    }

    @Override
    public String getSuffix() {
        return delay.getValueDisplayString();
    }
}
