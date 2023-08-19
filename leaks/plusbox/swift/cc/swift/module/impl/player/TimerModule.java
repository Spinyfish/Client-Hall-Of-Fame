package cc.swift.module.impl.player;

import cc.swift.events.EventState;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.value.impl.DoubleValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;

public class TimerModule extends Module {

    public final DoubleValue speed = new DoubleValue("Speed", 1.5, 0.05, 5, 0.025);

    public TimerModule() {
        super("Timer", Category.MISC);
        registerValues(speed);
    }

    @Override
    public void onDisable(){
        super.onDisable();
        mc.timer.timerSpeed = 1;
    }

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateEventListener = e -> {
        if(e.getState() == EventState.PRE){
            mc.timer.timerSpeed = speed.getValue().floatValue();
        }
    };
}
