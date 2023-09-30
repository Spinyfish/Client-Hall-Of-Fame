package dev.tenacity.module.impl.player;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.time.TimerUtil;

@SuppressWarnings("unused")
public final class Timer extends Module {
    private final TimerUtil dynamicTimerUtil = new TimerUtil();
    private final ModeSetting timerMode = new ModeSetting("Timer Mode", "Normal", "Normal", "Dynamic");
    private final NumberSetting amount = new NumberSetting("Amount", 1, 10, 0.1, 0.1);

    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(timerMode.getMode());
        switch (timerMode.getMode()) {
            case "Normal":
                mc.timer.timerSpeed = amount.getValue().floatValue();
                break;

            case "Dynamic":
                if (dynamicTimerUtil.hasTimeElapsed(Math.random() * 15)) {
                    if (mc.thePlayer.ticksExisted % 6 == 0) {
                        if (mc.thePlayer.ticksExisted % 40 == 0) {
                            ChatUtil.print("Randomized");
                        }
                        mc.timer.timerSpeed = amount.getValue().floatValue() * 1.2F;
                    } else if (mc.thePlayer.ticksExisted % 3 == 0) {

                        mc.timer.timerSpeed = amount.getValue().floatValue();
                    } else {
                        if (mc.thePlayer.ticksExisted % 50 == 0) {
                            ChatUtil.print("Reset");
                        }
                        mc.timer.timerSpeed = 1.0F;
                    }
                    dynamicTimerUtil.reset();
                }
            default:
                break;
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }

    public Timer() {
        super("Timer", Category.PLAYER, "changes game speed");
        this.addSettings(amount,timerMode);
    }

}
