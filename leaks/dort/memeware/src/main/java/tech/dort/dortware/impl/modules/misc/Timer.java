package tech.dort.dortware.impl.modules.misc;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.UpdateEvent;

public class Timer extends Module {

    private final NumberValue timer = new NumberValue("Timer", this, 2, 0.1, 10);

    public Timer(ModuleData moduleData) {
        super(moduleData);
        register(timer);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        mc.timer.timerSpeed = timer.getCastedValue().floatValue();
    }
}
