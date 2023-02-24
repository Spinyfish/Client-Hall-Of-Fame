package me.kansio.client.modules.impl.world;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.utils.math.Stopwatch;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@ModuleData(
        name = "Chest Stealer",
        category = ModuleCategory.WORLD,
        description = "Automatically steals stuff from chests"
)
public class ChestStealer extends Module {

    private BooleanValue checkChest = new BooleanValue("Check Chest", this, true);
    private NumberValue<Integer> delay = new NumberValue<>("Delay", this, 25, 0, 1000, 1);
    private Stopwatch delayCounter = new Stopwatch();

    public void onEnable() {
        delayCounter.resetTime();
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.currentScreen instanceof GuiChest) {
            if (delayCounter.timeElapsed(delay.getValue().longValue())) {
                GuiChest chest = (GuiChest) mc.currentScreen;

                if (checkChest.getValue() && (!chest.lowerChestInventory.getDisplayName().getUnformattedText().contains("Chest") && !chest.lowerChestInventory.getDisplayName().getUnformattedText().contains("LOW") && !chest.lowerChestInventory.getDisplayName().getUnformattedText().contains("coffre"))) {
                    delayCounter.resetTime();
                    return;
                }

                if (isChestEmpty(chest) || isInventoryFull()) {
                    mc.thePlayer.closeScreen();
                }

                for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); ++index) {
                    ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);

                    if (stack != null && delayCounter.timeElapsed(delay.getValue().longValue())) {
                        if (isTrash(stack)) continue;
                        mc.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, mc.thePlayer);
                        delayCounter.resetTime();
                    }
                }

                delayCounter.resetTime();
            }
        } else if (mc.currentScreen instanceof GuiCrafting) {

            if (delayCounter.timeElapsed(delay.getValue().longValue())) {

                GuiCrafting chest = (GuiCrafting) mc.currentScreen;

                mc.playerController.windowClick(chest.inventorySlots.windowId, 0, 0, 1, mc.thePlayer);
                delayCounter.resetTime();

            }

        }

    }

    private boolean isChestEmpty(final GuiChest chest) {
        for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); ++index) {
            ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
            if (stack != null)
                if (!isTrash(stack))
                    return false;
        }
        return true;
    }


    private boolean isInventoryFull() {
        for (int index = 9; index <= 44; ++index) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack == null) {
                return false;
            }
        }
        return true;
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

    private boolean isBadPotion(final ItemStack stack) {
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

}
