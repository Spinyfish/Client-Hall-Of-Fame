package dev.africa.pandaware.impl.module.combat;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberRangeSetting;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import lombok.AllArgsConstructor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.security.SecureRandom;

@ModuleInfo(name = "AutoClicker", description = "Enables the gaming mouse", category = Category.COMBAT)
public class AutoClickerModule extends Module {

    private final NumberRangeSetting cpsRange =
            new NumberRangeSetting("CPS Range", 20, 0.5, 8, 14, 0.5);
    private final EnumSetting<ClickMode> clickMode
            = new EnumSetting<>("Click Mode", ClickMode.FULL_RANDOM);
    private final BooleanSetting weaponsOnly = new BooleanSetting("Weapons Only", false);

    public AutoClickerModule() {
        this.registerSettings(this.cpsRange, this.clickMode, this.weaponsOnly);
    }

    private Robot robot;
    private final TimeHelper timer = new TimeHelper();
    private final TimeHelper clickTimer = new TimeHelper();
    private double increaseClicks;
    private int nextClickTime;

    @EventHandler
    EventCallback<MoveEvent> onEvent = event -> {
        if (!Mouse.isButtonDown(0) || mc.thePlayer == null || mc.currentScreen != null) return;

        if (this.weaponsOnly.getValue()) {

            ItemStack equippedItem = mc.thePlayer.getCurrentEquippedItem();

            if (equippedItem == null || !(equippedItem.getItem() instanceof ItemAxe
                    || equippedItem.getItem() instanceof ItemSword)) {

                return;
            }
        }

        if (this.robot == null) {
            try {
                this.robot = new Robot();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (shouldClickMouse()) {
            try {
                this.robot.mouseRelease(16);
                this.robot.mousePress(16);
            } catch (Exception ignored) {
            }

            mc.thePlayer.resetCooldown();
            mc.leftClickCounter = 0;
            this.timer.reset();
        }
    };

    private boolean shouldClickMouse() {
        switch (this.clickMode.getValue()) {
            case RANDOM: {
                double time = RandomUtils.nextDouble(this.cpsRange.getFirstValue().doubleValue(),
                        this.cpsRange.getSecondValue().doubleValue());

                return this.timer.reach((float) (1000L / time));
            }

            case SECURE_RANDOM: {
                double min = this.cpsRange.getFirstValue().doubleValue();
                double max = this.cpsRange.getSecondValue().doubleValue();

                double time = MathHelper.clamp_double(
                        min + ((max - min) * new SecureRandom().nextDouble()), min, max);

                return this.timer.reach((float) (1000L / time));
            }

            case FULL_RANDOM: {
                double min = this.cpsRange.getFirstValue().doubleValue() * RandomUtils.nextDouble(0, 1);
                double max = this.cpsRange.getSecondValue().doubleValue() * RandomUtils.nextDouble(0, 1);

                double time = (max / min) * (RandomUtils.nextDouble(min, max));

                return this.timer.reach((float) (1000L / time));
            }

            case INCREASE: {
                double min = this.cpsRange.getFirstValue().doubleValue();
                double max = this.cpsRange.getSecondValue().doubleValue();

                if (this.increaseClicks > min) {
                    this.increaseClicks -= RandomUtils.nextDouble(0.2, 0.45);
                }
                if (this.clickTimer.reach(this.nextClickTime)) {
                    this.nextClickTime = RandomUtils.nextInt(30, 50);

                    this.increaseClicks += RandomUtils.nextDouble(0.2, 0.45);
                    this.clickTimer.reset();
                }
                this.increaseClicks = MathHelper.clamp_double(this.increaseClicks, 0, max);

                return this.timer.reach((float) (1000L / this.increaseClicks));
            }

            case DROP: {
                double randomTime = 0;
                if (this.clickTimer.reach(this.nextClickTime)) {
                    this.nextClickTime = RandomUtils.nextInt(450, 900);

                    randomTime -= RandomUtils.nextDouble(3, 5);
                    this.clickTimer.reset();
                }

                double min = this.cpsRange.getFirstValue().doubleValue();
                double max = this.cpsRange.getSecondValue().doubleValue();

                double time = (min + ((max - min) * new SecureRandom().nextDouble())) + randomTime;

                return this.timer.reach((float) (1000L / time));
            }

            case SPIKE: {
                double randomTime = 0;
                if (this.clickTimer.reach(this.nextClickTime)) {
                    this.nextClickTime = RandomUtils.nextInt(450, 900);

                    randomTime += RandomUtils.nextDouble(3, 5);
                    this.clickTimer.reset();
                }

                double min = this.cpsRange.getFirstValue().doubleValue();
                double max = this.cpsRange.getSecondValue().doubleValue();

                double time = (min + ((max - min) * new SecureRandom().nextDouble())) + randomTime;

                return this.timer.reach((float) (1000L / time));
            }

            case DROP_INCREASE: {
                double min = this.cpsRange.getFirstValue().doubleValue();
                double max = this.cpsRange.getSecondValue().doubleValue();

                if (this.increaseClicks > min) {
                    this.increaseClicks -= RandomUtils.nextDouble(0.2, 0.45);
                }
                if (this.clickTimer.reach(this.nextClickTime)) {
                    this.nextClickTime = RandomUtils.nextInt(30, 50);

                    this.increaseClicks += RandomUtils.nextDouble(0.2, 0.45);
                    this.clickTimer.reset();
                }
                if (RandomUtils.nextInt(0, 10) == RandomUtils.nextInt(0, 10)) {
                    this.increaseClicks -= RandomUtils.nextDouble(1.2, 1.7);
                }

                this.increaseClicks = MathHelper.clamp_double(this.increaseClicks, 0, max);

                return this.timer.reach((float) (1000L / this.increaseClicks));
            }

            case ONE_DOT_NINE_PLUS: {
                float delay = mc.thePlayer.getCooledAttackStrength(0.0f);

                return delay > 0.9f;
            }
        }

        return false;
    }

    @AllArgsConstructor
    private enum ClickMode {
        RANDOM("Random"),
        SECURE_RANDOM("Secure Random"),
        FULL_RANDOM("Full Random"),
        INCREASE("Increase"),
        DROP("Drop"),
        SPIKE("Spike"),
        DROP_INCREASE("Drop Increase"),
        ONE_DOT_NINE_PLUS("1.9+");

        private String label;

        @Override
        public String toString() {
            return label;
        }
    }
}