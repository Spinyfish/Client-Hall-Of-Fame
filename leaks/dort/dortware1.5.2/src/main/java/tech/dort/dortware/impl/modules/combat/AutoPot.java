package tech.dort.dortware.impl.modules.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockGlass;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.modules.movement.Speed;
import tech.dort.dortware.impl.utils.movement.MotionUtils;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.time.Stopwatch;

import java.util.List;

public class AutoPot extends Module {

    private static boolean isPotting;
    private final Object[] badPotionArray = {false};
    private final Stopwatch timer = new Stopwatch();

    private final EnumValue<AutoPot.Mode> enumValue = new EnumValue<>("Mode", this, AutoPot.Mode.values());
    private final BooleanValue skywars = new BooleanValue("Skywars", this, false);
    private final NumberValue minHealth = new NumberValue("Min Health", this, 12, 5, 20, true);
    private final NumberValue delay = new NumberValue("Delay", this, 150, 50, 1000, SliderUnit.MS, true);

    public AutoPot(ModuleData moduleData) {
        super(moduleData);
        register(enumValue, minHealth, delay, skywars);
    }

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

        if (skywars.getValue() && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).offsetDown()).getBlock() instanceof BlockGlass) {
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
            case DOWN:
                event.setRotationPitch(90F);
                break;

            case UP:
                event.setRotationPitch(-90F);
                break;

            case JUMP:
                if (!Client.INSTANCE.getModuleManager().get(Speed.class).isToggled() && mc.thePlayer.onGround && !mc.thePlayer.isMoving()) {
                    mc.thePlayer.motionY = MotionUtils.getMotion(0.42F);
                }

                event.setRotationPitch(-90F);
                break;

            case INSTANT_JUMP:
                if (!Client.INSTANCE.getModuleManager().get(Speed.class).isToggled() && mc.thePlayer.onGround && !mc.thePlayer.isMoving()) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + MotionUtils.getMotion(0.42F), mc.thePlayer.posZ);
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
        return " \2477" + enumValue.getValue().getDisplayName();
//        return " \2477" + delay.getValue().intValue();
    }

    public enum Mode implements INameable {
        DOWN("Down"), UP("Up"), JUMP("Jump"), INSTANT_JUMP("Instant Jump");
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
