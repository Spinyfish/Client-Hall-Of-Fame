package dev.echo.module.impl.player;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.misc.MathUtils;

@SuppressWarnings("unused")
public final class Timer extends Module {

    private final NumberSetting amount = new NumberSetting("Max Amount", 1, 10, 0.1, 0.1);
    private final NumberSetting amount2 = new NumberSetting("Min Amount", 1, 10, 0.1, 0.1);

    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        mc.timer.timerSpeed = MathUtils.getRandomFloat(amount.getValue().floatValue(), amount2.getValue().floatValue());
    };

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }

    public Timer() {
        super("Timer", Category.PLAYER, "changes game speed");
        this.addSettings(amount, amount2);
    }
}
