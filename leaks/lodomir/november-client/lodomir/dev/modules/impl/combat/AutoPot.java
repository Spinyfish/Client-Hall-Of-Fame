/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import java.util.concurrent.ThreadLocalRandom;
import lodomir.dev.November;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.TimeUtils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;

public class AutoPot
extends Module {
    public NumberSetting health = new NumberSetting("Health", 1.0, 20.0, 12.0, 0.5);
    public NumberSetting delay = new NumberSetting("Delay", 0.0, 1000.0, 400.0, 50.0);
    public ModeSetting rot = new ModeSetting("Rotation", "Up", "Up", "Down");
    public BooleanSetting jumpBoostPot = new BooleanSetting("Throw Frog", false);
    private final TimeUtils timer = new TimeUtils();
    public static boolean isPotting;
    private float prevPitch;

    public AutoPot() {
        super("AutoPot", 0, Category.COMBAT);
        this.addSetting(this.delay);
        this.addSetting(this.health);
        this.addSetting(this.rot);
        this.addSetting(this.jumpBoostPot);
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        int i;
        int prevSlot = AutoPot.mc.thePlayer.inventory.currentItem;
        if (AutoPot.mc.thePlayer.onGround && !November.INSTANCE.getModuleManager().getModule("Scaffold").isEnabled() && (!AutoPot.mc.thePlayer.isPotionActive(Potion.moveSpeed) || AutoPot.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getDuration() < 30) && this.timer.hasReached(this.delay.getValueInt())) {
            if (this.isSpeedPotsInHotbar()) {
                for (i = 36; i < 45; ++i) {
                    if (!this.isSpeedPot(AutoPot.mc.thePlayer.inventoryContainer.getSlot(i).getStack())) continue;
                    isPotting = true;
                    this.prevPitch = AutoPot.mc.thePlayer.rotationPitch;
                    this.throwPot(prevSlot, i);
                    if (this.rot.isMode("Up")) {
                        event.setPitch(90.0f);
                        break;
                    }
                    if (!this.rot.isMode("Down")) break;
                    event.setPitch(-90.0f);
                    break;
                }
                this.timer.reset();
                isPotting = false;
            } else {
                this.moveSpeedPots();
            }
        }
        if (!AutoPot.mc.thePlayer.isPotionActive(Potion.regeneration) && (double)AutoPot.mc.thePlayer.getHealth() <= this.health.getValue() && this.timer.hasReached((long)this.delay.getValueFloat())) {
            if (this.isRegenPotsInHotbar()) {
                for (i = 36; i < 45; ++i) {
                    if (!this.isRegenPot(AutoPot.mc.thePlayer.inventoryContainer.getSlot(i).getStack())) continue;
                    isPotting = true;
                    this.prevPitch = AutoPot.mc.thePlayer.rotationPitch;
                    this.throwPot(prevSlot, i);
                    if (this.rot.isMode("Up")) {
                        event.setPitch(90.0f);
                        break;
                    }
                    if (!this.rot.isMode("Down")) break;
                    event.setPitch(-90.0f);
                    break;
                }
                this.timer.reset();
                isPotting = false;
            } else {
                this.moveRegenPots();
            }
        }
        if ((double)AutoPot.mc.thePlayer.getHealth() <= this.health.getValue() && this.timer.hasReached((long)this.delay.getValueFloat())) {
            if (this.isHealthPotsInHotbar()) {
                for (i = 36; i < 45; ++i) {
                    if (!this.isHealthPot(AutoPot.mc.thePlayer.inventoryContainer.getSlot(i).getStack())) continue;
                    isPotting = true;
                    this.prevPitch = AutoPot.mc.thePlayer.rotationPitch;
                    this.throwPot(prevSlot, i);
                    if (this.rot.isMode("Up")) {
                        event.setPitch(90.0f);
                        break;
                    }
                    if (!this.rot.isMode("Down")) break;
                    event.setPitch(-90.0f);
                    break;
                }
                this.timer.reset();
                isPotting = false;
            } else {
                this.moveHealthPots();
            }
        }
        isPotting = false;
    }

    private void throwPot(int prevSlot, int index) {
        double x = AutoPot.mc.thePlayer.posX;
        double y = AutoPot.mc.thePlayer.posY;
        double z = AutoPot.mc.thePlayer.posZ;
        float yaw = AutoPot.mc.thePlayer.rotationYaw;
        this.sendPacketSilent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, 88.8f + ThreadLocalRandom.current().nextFloat(), AutoPot.mc.thePlayer.onGround));
        this.sendPacketSilent(new C09PacketHeldItemChange(index - 36));
        this.sendPacketSilent(new C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, AutoPot.mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
        this.sendPacket(new C09PacketHeldItemChange(prevSlot));
        this.sendPacketSilent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, this.prevPitch, AutoPot.mc.thePlayer.onGround));
    }

    private boolean isSpeedPotsInHotbar() {
        for (int index = 36; index < 45; ++index) {
            if (!this.isSpeedPot(AutoPot.mc.thePlayer.inventoryContainer.getSlot(index).getStack())) continue;
            return true;
        }
        return false;
    }

    private boolean isHealthPotsInHotbar() {
        for (int index = 36; index < 45; ++index) {
            if (!this.isHealthPot(AutoPot.mc.thePlayer.inventoryContainer.getSlot(index).getStack())) continue;
            return true;
        }
        return false;
    }

    private boolean isRegenPotsInHotbar() {
        for (int index = 36; index < 45; ++index) {
            if (!this.isRegenPot(AutoPot.mc.thePlayer.inventoryContainer.getSlot(index).getStack())) continue;
            return true;
        }
        return false;
    }

    private int getPotionCount() {
        int count = 0;
        for (int index = 0; index < 45; ++index) {
            ItemStack stack = AutoPot.mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (!this.isHealthPot(stack) && !this.isHealthPot(stack) && !this.isRegenPot(stack)) continue;
            ++count;
        }
        return count;
    }

    private void moveSpeedPots() {
        if (AutoPot.mc.currentScreen instanceof GuiChest) {
            return;
        }
        for (int index = 9; index < 36; ++index) {
            ItemStack stack = AutoPot.mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack == null || !this.jumpBoostPot.isEnabled() && stack.getDisplayName().contains("Jump") || stack.getDisplayName().contains("Leaping") || stack.getDisplayName().contains("Frog") || !this.isSpeedPot(stack)) continue;
            AutoPot.mc.playerController.windowClick(0, index, 6, 2, AutoPot.mc.thePlayer);
            break;
        }
    }

    private void moveHealthPots() {
        if (AutoPot.mc.currentScreen instanceof GuiChest) {
            return;
        }
        for (int index = 9; index < 36; ++index) {
            ItemStack stack = AutoPot.mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (!this.isHealthPot(stack)) continue;
            AutoPot.mc.playerController.windowClick(0, index, 6, 2, AutoPot.mc.thePlayer);
            break;
        }
    }

    private void moveRegenPots() {
        if (AutoPot.mc.currentScreen instanceof GuiChest) {
            return;
        }
        for (int index = 9; index < 36; ++index) {
            ItemStack stack = AutoPot.mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (!this.isRegenPot(stack)) continue;
            AutoPot.mc.playerController.windowClick(0, index, 6, 2, AutoPot.mc.thePlayer);
            break;
        }
    }

    private boolean isSpeedPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            if (!this.jumpBoostPot.isEnabled() && stack.getDisplayName().contains("Jump") || stack.getDisplayName().contains("Leaping") || stack.getDisplayName().contains("Frog")) {
                return false;
            }
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect e : ((ItemPotion)stack.getItem()).getEffects(stack)) {
                    if (e.getPotionID() != Potion.moveSpeed.id || e.getPotionID() == Potion.jump.id) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isHealthPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion && ItemPotion.isSplash(stack.getItemDamage())) {
            for (PotionEffect e : ((ItemPotion)stack.getItem()).getEffects(stack)) {
                if (e.getPotionID() != Potion.heal.id) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isRegenPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion && ItemPotion.isSplash(stack.getItemDamage())) {
            for (PotionEffect e : ((ItemPotion)stack.getItem()).getEffects(stack)) {
                if (e.getPotionID() != Potion.regeneration.id) continue;
                return true;
            }
        }
        return false;
    }
}

