package de.fanta.module.impl.world;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventNoClip;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.settings.CheckBox;

public class Phase extends Module {
    public Phase() {
        super("Phase",0, Type.World, Color.red);
    }

    @Override
    public void onEvent(Event event) {
    	if(event instanceof EventNoClip) {
    		((EventNoClip) event).noClip = true;
    	}
    }
}