package dev.echo.module.impl.combat;



import dev.echo.Echo;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.player.MovementUtils;

import java.awt.*;
import java.util.Random;


public class Reach extends Module {
    public static final NumberSetting minreach = new NumberSetting("Min Reach", 3, 6, 3, 0.1);
    public static final NumberSetting chance = new NumberSetting("reach chance", 60, 100, 1, 1);
    public static final NumberSetting maxreach = new NumberSetting("Max Reach", 3, 6, 3f, 0.1);
    public static final BooleanSetting move = new BooleanSetting("Only Move", false);
    public static final BooleanSetting render = new BooleanSetting("Render", false);
    public Reach() {
        super("Reach", Category.COMBAT, "mhh i wonder");
        addSettings(maxreach,minreach,move,chance,render);
    }

    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        this.setSuffix(minreach.getValue().floatValue() + " - " + maxreach.getValue().floatValue());
    };

    public static float getReachAmount() {
        Random random = new Random();
        if (random.nextInt(100) >= (Integer)chance.getValue().intValue()) return 3.0f;
        if (!Echo.INSTANCE.isEnabled(Reach.class)) return 3.0f;
        if (Reach.mc.thePlayer == null) return 3.0f;
        if (!MovementUtils.isMoving()) {
            if (move.isEnabled() != false) return 3.0f;
        }
        float actualreach = maxreach.getValue().floatValue() - minreach.getValue().floatValue();
        actualreach = (float)((double)actualreach * Math.abs(random.nextGaussian()));
        if(mc.thePlayer.isSwingInProgress) {
            if (mc.thePlayer.ticksExisted % 10 == 0) {
               // ChatUtil.print(actualreach + minreach.getValue().floatValue());
            }
        }
        return actualreach + minreach.getValue().floatValue();
    }


}