package de.fanta.module.impl.visual;


import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.settings.CheckBox;

public class Notification extends Module {
    public Notification() {
        super("Notification",0, Type.Visual, Color.blue);
    }

    @Override
    public void onEvent(Event event) {

     
    }
}
