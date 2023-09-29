package com.alan.clients.script.api.wrapper.impl.event.impl;


import com.alan.clients.newevent.impl.other.TickEvent;
import com.alan.clients.script.api.wrapper.impl.event.ScriptEvent;

/**
 * @author Strikeless
 * @since 23.06.2022
 */
public class ScriptTickEvent extends ScriptEvent<TickEvent> {

    public ScriptTickEvent(final TickEvent wrappedEvent) {
        super(wrappedEvent);
    }

    @Override
    public String getHandlerName() {
        return "onTick";
    }
}
