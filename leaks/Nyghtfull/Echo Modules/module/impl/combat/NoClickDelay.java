package dev.echo.module.impl.combat;


import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;

public class NoClickDelay extends Module {


    public NoClickDelay() {
        super("No Click Delay", Category.COMBAT, "Remove the click delay from 1.8");
    }

    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        if (mc.thePlayer != null && mc.theWorld != null) {
            mc.leftClickCounter = 0;
        }

    };


}
