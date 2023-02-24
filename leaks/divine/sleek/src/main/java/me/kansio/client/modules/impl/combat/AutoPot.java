package me.kansio.client.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.Client;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.modules.impl.movement.Speed;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.utils.math.Stopwatch;
import me.kansio.client.utils.network.PacketUtil;
import me.kansio.client.utils.player.PlayerUtil;
import net.minecraft.block.BlockGlass;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;

import java.util.List;

@ModuleData(
        name = "AutoPot",
        category = ModuleCategory.COMBAT,
        description = "Pots for you"
)
public class AutoPot extends Module {
    private static boolean isPotting;
    private final Object[] badPotionArray = {false};
    private final Stopwatch timer = new Stopwatch();

    private final ModeValue enumValue = new ModeValue("Mode", this, "Down", "Up", "Jump", "Instant Jump");
    private final BooleanValue skywars = new BooleanValue("Skywars", this, false);
    private final NumberValue<Integer> minHealth = new NumberValue<>("Min Health", this, 12, 5, 20, 1);
    private final NumberValue<Integer> delay = new NumberValue<>("Delay", this, 150, 50, 1000, 25);



    public static boolean isPotting() {
        return isPotting;
    }

    public static void setPotting(boolean isPotting) {
        AutoPot.isPotting = isPotting;
    }

    private boolean invCheck() {
        for (int i = 0; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                    || getPotionData(mc.thePlayer.inventoryContainer.getSlot(i).getStack()) == badPotionArray)
                continue;
            Object[] dataArray = getPotionData(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
            int data = (int) dataArray[1];
            if (data == Potion.regeneration.id || data == Potion.heal.id) {
                return true;
            }
        }
        return false;
    }

    private void swap(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 2, mc.thePlayer);
    }

    private boolean invCheckMisc() {
        for (int i = 0; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                    || getPotionDataMisc(mc.thePlayer.inventoryContainer.getSlot(i).getStack()) == badPotionArray)
                continue;
            Object[] dataArray = getPotionDataMisc(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
            int data = (int) dataArray[1];
            if (data == 1) {
                return true;
            }
        }
        return false;
    }

    private Object[] getPotionDataMisc(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemPotion) || !ItemPotion.isSplash(stack.getMetadata())) {
            return badPotionArray;
        }
        ItemPotion itemPotion = (ItemPotion) stack.getItem();
        List<PotionEffect> potionEffectList = itemPotion.getEffects(stack);
        for (PotionEffect potionEffect : potionEffectList) {
            if (potionEffect.getPotionID() == Potion.moveSpeed.id) {
                return new Object[]{true, potionEffect.getPotionID(), stack};
            }
        }
        return badPotionArray;
    }

    private Object[] getPotionData(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemPotion) || !ItemPotion.isSplash(stack.getMetadata())) {
            return badPotionArray;
        }
        ItemPotion itemPotion = (ItemPotion) stack.getItem();
        List<PotionEffect> potionEffectList = itemPotion.getEffects(stack);
        for (PotionEffect potionEffect : potionEffectList) {
            if (potionEffect.getPotionID() == Potion.heal.id || potionEffect.getPotionID() == Potion.regeneration.id) {
                return new Object[]{true, potionEffect.getPotionID(), stack};
            }
        }
        return badPotionArray;
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (!mc.thePlayer.onGround)
            return;

        if (skywars.getValue() && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).down()).getBlock() instanceof BlockGlass) {
            return;
        }

        if (!event.isPre()) {
            if (isPotting()) {
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(1));
                PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                setPotting(false);
            }
            return;
        }

        if (mc.thePlayer.getHealth() <= minHealth.getValue().floatValue() && invCheck()) {
            if (timer.timeElapsed(1000L + delay.getValue().longValue())) {
                useMode(event);
                refillPotions();
                setPotting(true);
                timer.resetTime();
            }
        } else {
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed) || !invCheckMisc()) {
                return;
            }

            if (timer.timeElapsed(1000L + delay.getValue().longValue())) {
                useMode(event);
                refillPotionsMisc();
                setPotting(true);
                timer.resetTime();
            }
        }
    }

    private void useMode(UpdateEvent event) {
        switch (enumValue.getValue()) {
            case "Down":
                event.setRotationPitch(90F);
                break;

            case "Up":
                event.setRotationPitch(-90F);
                break;

            case "Jump":
                PlayerUtil MotionUtils;
                if (!Client.getInstance().getModuleManager().getModuleByClass(Speed.class).isToggled() && mc.thePlayer.onGround && !mc.thePlayer.isMoving()) {
                    mc.thePlayer.motionY = PlayerUtil.getMotion(0.42F);
                }

                event.setRotationPitch(-90F);
                break;

            case "Instant Jump":
                if (!Client.getInstance().getModuleManager().getModuleByClass(Speed.class).isToggled() && mc.thePlayer.onGround && !mc.thePlayer.isMoving()) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + PlayerUtil.getMotion(0.42F), mc.thePlayer.posZ);
                }

                event.setRotationPitch(-90F);
                break;
        }
    }

    private void refillPotions() {
        if (invCheck()) {
            for (int i = 0; i < 45; ++i) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (!slot.getHasStack())
                    continue;
                if (i == 37)
                    continue;
                if (getPotionData(slot.getStack()) != badPotionArray) {
                    swap(i);
                    break;
                }
            }
        }
    }

    private void refillPotionsMisc() {
        if (invCheckMisc()) {
            for (int i = 0; i < 45; ++i) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (!slot.getHasStack())
                    continue;
                if (i == 37)
                    continue;
                if (getPotionDataMisc(slot.getStack()) != badPotionArray) {
                    swap(i);
                    break;
                }
            }
        }
    }

    @Override
    public String getSuffix() {
        return " \2477" + enumValue.getValue();
//        return " \2477" + delay.getValue().intValue();
    }
}