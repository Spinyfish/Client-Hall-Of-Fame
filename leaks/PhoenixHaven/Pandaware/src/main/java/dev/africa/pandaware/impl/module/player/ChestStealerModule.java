package dev.africa.pandaware.impl.module.player;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@Getter
@ModuleInfo(name = "Chest Stealer", category = Category.PLAYER)
public class ChestStealerModule extends Module {
    private final TimeHelper timer = new TimeHelper();
    private final NumberSetting delay = new NumberSetting("Delay", 1000, 0, 80);
    private final NumberSetting randomMax = new NumberSetting("Random Max", 1000, 0, 50);
    private final NumberSetting randomMin = new NumberSetting("Random Min", 1000, 0, 0);
    private final BooleanSetting random = new BooleanSetting("Randomization", false);
    private final BooleanSetting chestCheck = new BooleanSetting("ChestCheck", true);
    private final BooleanSetting silent = new BooleanSetting("Silent", false);
    private final BooleanSetting aim = new BooleanSetting("Aim", true);

    public ChestStealerModule() {
        this.registerSettings(this.delay, this.randomMax, this.randomMin,
                this.chestCheck, this.random, this.silent, this.aim);
    }

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        if (mc.currentScreen instanceof GuiChest) {
            GuiChest chest = (GuiChest) mc.currentScreen;

            if (chestCheck.getValue() && !isInvalidChest(chest)) {
                return;
            }

            boolean full = true;
            ItemStack[] arrayOfItemStack;
            int j = (arrayOfItemStack = mc.thePlayer.inventory.mainInventory).length;
            for (int i = 0; i < j; i++) {
                ItemStack item = arrayOfItemStack[i];
                if (item == null) {
                    full = false;
                    break;
                }
            }


            boolean containsItems = false;
            if (!full) {
                for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); index++) {
                    ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
                    if (stack != null && !isBad(stack)) {
                        containsItems = true;
                        break;
                    }
                }
                if (containsItems) {
                    for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); index++) {
                        ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
                        if (stack != null && timer.reach(delay.getValue().intValue() +
                                (random.getValue() ? RandomUtils.nextInt(randomMin.getValue().intValue(), randomMax.getValue().intValue()) : 0)) && !isBad(stack)) {
                            mc.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, mc.thePlayer);
                            timer.reset();
                        }
                    }
                } else {
                    mc.thePlayer.closeScreen();

                    if (silent.getValue()) {
                        Client.getInstance().getNotificationManager().addNotification(Notification.Type.SUCCESS, "Finished stealing chest.", 1);
                    }
                }
            }
        }
    };

    private boolean isBad(ItemStack item) {
        return item != null &&
                ((item.getItem().getUnlocalizedName().contains("tnt")) ||
                        (item.getItem().getUnlocalizedName().contains("stick")) ||
                        (item.getItem().getUnlocalizedName().contains("egg") && !item.getItem().getUnlocalizedName().contains("leg")) ||
                        (item.getItem().getUnlocalizedName().contains("string")) ||
                        (item.getItem().getUnlocalizedName().contains("flint")) ||
                        (item.getItem().getUnlocalizedName().contains("compass")) ||
                        (item.getItem().getUnlocalizedName().contains("feather")) ||
                        (item.getItem().getUnlocalizedName().contains("snow")) ||
                        (item.getItem().getUnlocalizedName().contains("fish")) ||
                        (item.getItem().getUnlocalizedName().contains("enchant")) ||
                        (item.getItem().getUnlocalizedName().contains("exp")) ||
                        (item.getItem().getUnlocalizedName().contains("shears")) ||
                        (item.getItem().getUnlocalizedName().contains("anvil")) ||
                        (item.getItem().getUnlocalizedName().contains("torch")) ||
                        (item.getItem().getUnlocalizedName().contains("seeds")) ||
                        (item.getItem().getUnlocalizedName().contains("leather")) ||
                        ((item.getItem() instanceof ItemGlassBottle)) ||
                        (item.getItem().getUnlocalizedName().contains("piston")) ||
                        ((item.getItem().getUnlocalizedName().contains("potion"))
                                && (isBadPotion(item))));
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (final Object o : potion.getEffects(stack)) {
                    final PotionEffect effect = (PotionEffect) o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInvalidChest(GuiScreen screen) {
        GuiChest chest = (GuiChest) screen;
        return chest.lowerChestInventory.getName().contains(I18n.format("container.chest"))/*Pattern.compile(Pattern.quote(chest.lowerChestInventory.getName()), Pattern.CASE_INSENSITIVE).matcher("chest").find()*/;
    }
}