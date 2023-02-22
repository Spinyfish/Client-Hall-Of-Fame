package com.alan.clients.newevent;

import com.alan.clients.script.api.wrapper.impl.event.ScriptEvent;

public interface Event {
    public default ScriptEvent<? extends Event> getScriptEvent() {
        return null;
    }
}
