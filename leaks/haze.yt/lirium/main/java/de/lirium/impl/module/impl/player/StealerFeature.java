package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.GuiHandleEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.inventory.InventoryUtil;
import de.lirium.util.random.RandomUtil;
import god.buddy.aot.BCompiler;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ModuleFeature.Info(name = "Stealer", description = "Loot automatically chests", category = ModuleFeature.Category.PLAYER)
public class StealerFeature extends ModuleFeature {

    @Value(name = "Instant")
    final CheckBox instant = new CheckBox(false);

    @Value(name = "Start Delay")
    final SliderSetting<Long> startDelay = new SliderSetting<>(150L, 0L, 500L, new Dependency<>(instant, false));

    @Value(name = "Grab Delay")
    final SliderSetting<Long> grabDelay = new SliderSetting<>(150L, 0L, 500L, new Dependency<>(instant, false));

    @Value(name = "Stack Items")
    final CheckBox stackItems = new CheckBox(true);

    @Value(name = "Random Pick")
    final CheckBox randomPick = new CheckBox(false);

    @Value(name = "Auto Close")
    final CheckBox autoClose = new CheckBox(true);

    @Value(name = "Name Check")
    final CheckBox nameCheck = new CheckBox(true);

    final TimeHelper startTimer = new TimeHelper(), grabTimer = new TimeHelper();
    final List<Integer> itemsToSteal = new ArrayList<>();

    @EventHandler
    public final Listener<GuiHandleEvent> guiHandleEvent = e -> handleGui();

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void handleGui() {
        if (mc.currentScreen == null) {
            startTimer.reset();
            grabTimer.reset();
            return;
        }

        if(mc.currentScreen instanceof GuiChest) {
            if(!instant.getValue() && !startTimer.hasReached(startDelay.getValue() + Math.abs((long) RandomUtil.getGaussian(20)), false))
                return;
            itemsToSteal.clear();
            final ContainerChest chest = (ContainerChest) getPlayer().openContainer;
            final IInventory inventory = chest.getLowerChestInventory();
            boolean isEmpty = true;
            if (inventory.hasCustomName() && !inventory.getName().equalsIgnoreCase("Chest") && nameCheck.getValue()) return;
            for(int i = 0; i < inventory.getSizeInventory(); i++) {
                final ItemStack stack = inventory.getStackInSlot(i);
                if(!stack.getItem().isAir()) {
                    itemsToSteal.add(i);
                }
            }

            if(randomPick.getValue())
                Collections.shuffle(itemsToSteal);

            for(int i : itemsToSteal) {
                final ItemStack stack = inventory.getStackInSlot(i);
                if(!stack.getItem().isAir()) {
                    if(!instant.getValue() && !grabTimer.hasReached(grabDelay.getValue() + Math.abs((long) RandomUtil.getGaussian(20)), false))
                        return;

                    final int size = InventoryUtil.getItemSize(stack.getItem(), inventory);
                    if(stackItems.getValue() && stack.stackSize != 64 && stack.getMaxStackSize() != 1 && size != 0 && size != 1) {
                        mc.playerController.windowClick(chest.windowId, i, 0, ClickType.PICKUP, getPlayer());
                        mc.playerController.windowClick(chest.windowId, i, 0, ClickType.PICKUP_ALL, getPlayer());
                        mc.playerController.windowClick(chest.windowId, i, 0, ClickType.PICKUP, getPlayer());
                        mc.playerController.windowClick(chest.windowId, i, 0, ClickType.QUICK_MOVE, getPlayer());
                    } else {
                        mc.playerController.windowClick(chest.windowId, i, 0, ClickType.QUICK_MOVE, getPlayer());
                    }
                    grabTimer.reset();
                    isEmpty = false;
                }
            }

            if(isEmpty && autoClose.getValue())
                getPlayer().closeScreen();
        }
    }
}
