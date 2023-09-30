package com.alan.clients.script.api.wrapper.impl.event.impl;


import com.alan.clients.newevent.impl.motion.WaterEvent;
import com.alan.clients.script.api.wrapper.impl.event.ScriptEvent;

/**
 * @author Auth
 * @since 10/07/2022
 */
public class ScriptWaterEvent extends ScriptEvent<WaterEvent> {

    public ScriptWaterEvent(final WaterEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public void setWater(final boolean water) {
        this.wrapped.setWater(water);
    }

    public boolean isWater() {
        return this.wrapped.isWater();
    }

    @Override
    public String getHandlerName() {
        return "onWater";
    }
}
