package me.jinthium.straight.impl.modules.player;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.RandomUtils;

public class AutoArmor extends Module {

    private final int[] boots = new int[]{313, 309, 317, 305, 301};
    private final int[] chestplate = new int[]{311, 307, 315, 303, 299};
    private final int[] helmet = new int[]{310, 306, 314, 302, 298};
    private final int[] leggings = new int[]{312, 308, 316, 304, 300};
    private final TimerUtil timer = new TimerUtil();
    public final ModeSetting equipMode = new ModeSetting("Mode", "Normal", "Normal", "Open", "Silent");
    private final NumberSetting delay = new NumberSetting("Delay", 75, 0, 1000, 1);
    private final NumberSetting randomMax = new NumberSetting("Random Max", 50, 0, 1000, 1);
    private final NumberSetting randomMin = new NumberSetting("Random Min", 0, 0, 1000, 1);
    private final BooleanSetting random = new BooleanSetting("Randomization", false);
    private final BooleanSetting stopWhenCleaning = new BooleanSetting("Stop when cleaning", false);
    protected long delayVal;
    private int num = 5;

    public long lastCycle;

    public AutoArmor() {
        super("AutoArmor", 0, Category.PLAYER);
        this.addSettings(equipMode, delay, randomMin, randomMax, random, stopWhenCleaning);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        this.setSuffix(delay.getValue().toString());
        if ((equipMode.is("Open") && !(mc.currentScreen instanceof GuiInventory) ||
                this.stopWhenCleaning.isEnabled() && (mc.thePlayer.isMoving() || mc.thePlayer.moveForward > 0 ||
                        mc.thePlayer.moveStrafing > 0 || mc.gameSettings.keyBindJump.pressed)))
            return;

        if (equipMode.is("Fake") && (mc.thePlayer.isMoving() || mc.gameSettings.keyBindJump.pressed || !mc.thePlayer.isUsingItem()))
            return;

        delayVal = delay.getValue().intValue() +
                (random.isEnabled() ? RandomUtils.nextInt(randomMin.getValue().intValue(), randomMax.getValue().intValue()) : 0);
        if (!mc.thePlayer.capabilities.isCreativeMode) {
            if (timer.hasTimeElapsed(delayVal)) {
                for (int i = 9; i < 45; ++i) {
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null && this.canEquip(mc.thePlayer.inventoryContainer.getSlot(i).getStack()) != -1 && this.canEquip(mc.thePlayer.inventoryContainer.getSlot(i).getStack()) == this.num) {
                        lastCycle = System.currentTimeMillis();
                        getBestArmor();
                        if (shouldDrop(is, i)) drop(i);
                    }
                }

                this.num = this.num == 8 ? 5 : ++this.num;
                this.timer.reset();
            }
        }
    };

    private int canEquip(ItemStack stack) {
        int[] var5;
        int var4 = (var5 = this.boots).length;

        int id4;
        int var3;
        for (var3 = 0; var3 < var4; ++var3) {
            id4 = var5[var3];
            if (Item.getIdFromItem(stack.getItem()) == id4) {
                return 8;
            }
        }

        var4 = (var5 = this.leggings).length;

        for (var3 = 0; var3 < var4; ++var3) {
            id4 = var5[var3];
            if (Item.getIdFromItem(stack.getItem()) == id4) {
                return 7;
            }
        }

        var4 = (var5 = this.chestplate).length;

        for (var3 = 0; var3 < var4; ++var3) {
            id4 = var5[var3];
            if (Item.getIdFromItem(stack.getItem()) == id4) {
                return 6;
            }
        }

        var4 = (var5 = this.helmet).length;

        for (var3 = 0; var3 < var4; ++var3) {
            id4 = var5[var3];
            if (Item.getIdFromItem(stack.getItem()) == id4) {
                return 5;
            }
        }
        return -1;
    }

    public boolean shouldDrop(ItemStack stack, int slot) {
        return timer.hasTimeElapsed(this.delayVal + 10) && stack.getItem() instanceof ItemArmor;
    }

    public void drop(int slot) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
            lastCycle = System.currentTimeMillis();
        }
    }

    private float getProtValue(ItemStack stack) {
        float prot = 0;
        if ((stack.getItem() instanceof ItemArmor armor)) {
            prot += armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075D;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack) / 100d;
        }
        return prot;
    }

    private boolean isBestArmor(ItemStack stack, int type) {
        float prot = getProtValue(stack);
        String strType = "";
        if (type == 1) {
            strType = "helmet";
        } else if (type == 2) {
            strType = "chestplate";
        } else if (type == 3) {
            strType = "leggings";
        } else if (type == 4) {
            strType = "boots";
        }
        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }
        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getProtValue(is) > prot && is.getUnlocalizedName().contains(strType))
                    return false;
            }
        }
        return true;
    }

    public void shiftClick(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
        lastCycle = System.currentTimeMillis();
    }

    private void getBestArmor() {
        for (int type = 1; type < 5; type++) {
            if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                if (isBestArmor(is, type)) {
                    continue;
                } else {
                    drop(4 + type);
                }
            }
            for (int i = 9; i < 45; i++) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (isBestArmor(is, type) && getProtValue(is) > 0) {
                        shiftClick(i);
                        timer.reset();
                        if (delay.getValue().longValue() > 0)
                            return;
                    }
                }
            }
        }
    }
}