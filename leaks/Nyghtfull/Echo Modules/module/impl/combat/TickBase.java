package dev.echo.module.impl.combat;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.AttackEvent;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.Setting;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.player.ChatUtil;

public class TickBase extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Timer", "Timer");
    private final NumberSetting zoomticks = new NumberSetting("Zoom Ticks", 5, 20, 0, 1);
    private final NumberSetting timer1 = new NumberSetting("First Timer", 4.6, 10, 0, 0.1);
    private final NumberSetting timer2 = new NumberSetting("Second Timer", 0.4, 10, 0, 0.1);
    double tick = 0;

    public TickBase() {
        super("Tick Base", Category.COMBAT, "Makes you zoom to targets");
        Setting.addParent(mode, m -> m.is("Timer"), zoomticks, timer1, timer2);
        addSettings(mode, zoomticks, timer1, timer2);
    }

    @Override
    public void onEnable() {
        tick = 0;
        super.onEnable();
    }

    @Link
    public final Listener<AttackEvent> eventAttackListener = event -> {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        switch (mode.getMode()) {
            case "Timer": {
                if (event.getTargetEntity() != null && tick == 0) {
                    tick = zoomticks.getValue();
                }
                break;
            }
        }
    };
    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        ChatUtil.print(tick + "  " + mc.timer.timerSpeed);
        if (mode.getMode().equals("Timer")) {
            if (!mc.thePlayer.onGround) {
                mc.getTimer().timerSpeed = 1.0F;
                return;
            }
            if (tick == zoomticks.getValue()) {
                if (mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = timer1.getValue().floatValue();
                    tick--;
                }
            } else if (tick > 1) {
                if (mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = timer2.getValue().floatValue();
                    tick--;
                }
            } else if (tick == 1) {
                mc.timer.timerSpeed = 1;
                tick--;
            }
        }
    };
}
