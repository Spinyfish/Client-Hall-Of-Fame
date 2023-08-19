package cc.swift.module.impl.render;

import cc.swift.events.EventState;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.value.impl.DoubleValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;

public class ParticleTimerModule extends Module {

    public final DoubleValue speed = new DoubleValue("Speed", 0.5, 0.05, 3, 0.025);

    public ParticleTimerModule() {
        super("ParticleTimer", Category.RENDER);
        registerValues(speed);
    }

    @Override
    public void onDisable(){
        super.onDisable();
        mc.particleTimer.timerSpeed = 1;
    }

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateEventListener = e -> {
        if(e.getState() == EventState.PRE){
            mc.particleTimer.timerSpeed = speed.getValue().floatValue();
        }
    };
}
