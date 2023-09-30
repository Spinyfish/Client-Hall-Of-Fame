package dev.tenacity.module.impl.combat;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.movement.Scaffold;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.server.PacketUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPosition;

import java.util.concurrent.ThreadLocalRandom;

public class AutoPotion extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", 750, 2000, 0, 50);
    private final NumberSetting minHealHP = new NumberSetting("Heal HP", 12, 20, 1, 0.5);
    private final BooleanSetting splashFrogPots = new BooleanSetting("Frog potions", false);
    private final TimerUtil timerUtil = new TimerUtil();
    public static boolean isPotting;
    private float prevPitch;

    public AutoPotion() {
        super("AutoPotion", Category.COMBAT, "Automatically splashes potions.");
        this.addSettings(delay, minHealHP, splashFrogPots);
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        if (Tenacity.INSTANCE.isEnabled(Scaffold.class) || mc.currentScreen instanceof GuiChest || Tenacity.INSTANCE.isEnabled(KillAura.class))
            return;

        int prevSlot = mc.thePlayer.inventory.currentItem;
        if (e.isPre()) {
            if (mc.thePlayer.onGround
                    && !(mc.theWorld.getBlockState(new BlockPosition(e.getX(), e.getY() - 1, e.getZ())).getBlock() instanceof BlockGlass)
                    && (!mc.thePlayer.isPotionActive(Potion.moveSpeed)
                    || mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getDuration() < 30)
                    && timerUtil.hasTimeElapsed(delay.getValue().longValue()) && !mc.thePlayer.isUsingItem()) {
                if (isSpeedPotsInHotbar()) {
                    for (int i = 36; i < 45; i++) {
                        if (isSpeedPot(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                            isPotting = true;
                            prevPitch = mc.thePlayer.rotationPitch;
                            throwPot(prevSlot, i);
                            e.setPitch(-90 + ThreadLocalRandom.current().nextFloat());
                            break;
                        }
                    }
                    timerUtil.reset();
                    isPotting = false;
                } else {
                    moveSpeedPots();
                }
            }

            if (!mc.thePlayer.isPotionActive(Potion.regeneration) && mc.thePlayer.getHealth() <= minHealHP.getValue()
                    && timerUtil.hasTimeElapsed(delay.getValue().longValue())) {
                if (isRegenPotsInHotbar()) {
                    for (int i = 36; i < 45; i++) {
                        if (isRegenPot(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                            isPotting = true;
                            prevPitch = mc.thePlayer.rotationPitch;
                            throwPot(prevSlot, i);
                            e.setPitch(-90 + ThreadLocalRandom.current().nextFloat());
                            break;
                        }
                    }
                    timerUtil.reset();
                    isPotting = false;
                } else {
                    moveRegenPots();
                }
            }

            if (mc.thePlayer.getHealth() <= minHealHP.getValue() && timerUtil.hasTimeElapsed(delay.getValue().longValue())) {
                if (isHealthPotsInHotbar()) {
                    for (int i = 36; i < 45; i++) {
                        if (isHealthPot(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                            isPotting = true;
                            prevPitch = mc.thePlayer.rotationPitch;
                            throwPot(prevSlot, i);
                            e.setPitch(-90 + ThreadLocalRandom.current().nextFloat());
                            break;
                        }
                    }
                    timerUtil.reset();
                    isPotting = false;
                } else {
                    moveHealthPots();
                }
            }
        } else if (e.isPost()) {
            isPotting = false;
        }
    }

    private void throwPot(int prevSlot, int index) {
        double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;
        float yaw = mc.thePlayer.rotationYaw;
        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                x, y, z, yaw, 88.8F + ThreadLocalRandom.current().nextFloat(), mc.thePlayer.onGround));
        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(index - 36));
        PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(
                BlockPosition.NEGATIVE, 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
        PacketUtils.sendPacket(new C09PacketHeldItemChange(prevSlot));
        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                x, y, z, yaw, prevPitch, mc.thePlayer.onGround));
    }

    private boolean isSpeedPotsInHotbar() {
        for (int index = 36; index < 45; index++) {
            if (isSpeedPot(mc.thePlayer.inventoryContainer.getSlot(index).getStack())) return true;
        }
        return false;
    }

    private boolean isHealthPotsInHotbar() {
        for (int index = 36; index < 45; index++) {
            if (isHealthPot(mc.thePlayer.inventoryContainer.getSlot(index).getStack())) return true;
        }
        return false;
    }

    private boolean isRegenPotsInHotbar() {
        for (int index = 36; index < 45; index++) {
            if (isRegenPot(mc.thePlayer.inventoryContainer.getSlot(index).getStack())) return true;
        }
        return false;
    }

    private int getPotionCount() {
        int count = 0;
        for (int index = 0; index < 45; index++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (isHealthPot(stack) || isHealthPot(stack) || isRegenPot(stack))
                count++;
        }
        return count;
    }

    private void moveSpeedPots() {
        if (mc.currentScreen instanceof GuiChest) return;
        for (int index = 9; index < 36; index++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack == null) continue;
            if (!splashFrogPots.isEnabled() && stack.getDisplayName().contains("Frog")) continue;
            if (isSpeedPot(stack)) {
                mc.playerController.windowClick(0, index, 6, 2, mc.thePlayer);
                break;
            }
        }
    }

    private void moveHealthPots() {
        if (mc.currentScreen instanceof GuiChest) return;
        for (int index = 9; index < 36; index++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (isHealthPot(stack)) {
                mc.playerController.windowClick(0, index, 6, 2, mc.thePlayer);
                break;
            }
        }
    }

    private void moveRegenPots() {
        if (mc.currentScreen instanceof GuiChest) return;
        for (int index = 9; index < 36; index++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (isRegenPot(stack)) {
                mc.playerController.windowClick(0, index, 6, 2, mc.thePlayer);
                break;
            }
        }
    }

    private boolean isSpeedPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            if (!splashFrogPots.isEnabled() && stack.getDisplayName().contains("Frog")) return false;
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect e : ((ItemPotion) stack.getItem()).getEffects(stack)) {
                    if (e.getPotionID() == Potion.moveSpeed.id && e.getPotionID() != Potion.jump.id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isHealthPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect e : ((ItemPotion) stack.getItem()).getEffects(stack)) {
                    if (e.getPotionID() == Potion.heal.id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isRegenPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect e : ((ItemPotion) stack.getItem()).getEffects(stack)) {
                    if (e.getPotionID() == Potion.regeneration.id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
