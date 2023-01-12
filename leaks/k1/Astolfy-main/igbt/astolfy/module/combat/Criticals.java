package igbt.astolfy.module.combat;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventPacket;
import igbt.astolfy.module.ModuleBase;

public class Criticals extends ModuleBase {
    public Criticals() {
        super("Criticals", 0, Category.COMBAT);
    }

    public void onEvent(Event e) {
        if(e instanceof EventPacket) {

        }
    }
}
