package me.jinthium.straight.impl.modules.ghost;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import jdk.jfr.Enabled;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;

public class NoClickDelay extends Module {

    public NoClickDelay(){
        super("No Click Delay", Category.GHOST);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        if(event.isPre()){
            if(mc.thePlayer != null && mc.theWorld != null)
                mc.leftClickCounter = 0;
        }
    };
}
