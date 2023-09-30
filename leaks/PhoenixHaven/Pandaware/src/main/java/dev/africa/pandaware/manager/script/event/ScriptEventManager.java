package dev.africa.pandaware.manager.script.event;

import dev.africa.pandaware.impl.container.MapContainer;
import jdk.nashorn.api.scripting.JSObject;

public class ScriptEventManager extends MapContainer<String, JSObject> {

    public void invoke(String name, JSObject event) {
        this.getMap().put(name, event);
    }
}
