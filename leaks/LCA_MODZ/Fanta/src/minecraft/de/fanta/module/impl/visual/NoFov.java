package de.fanta.module.impl.visual;

import java.awt.Color;

import de.fanta.events.Event;
import de.fanta.module.Module;

public class NoFov extends Module {
    public NoFov() {
        super("Nofov",0, Type.Visual, Color.yellow);
    }

    @Override
    public void onEvent(Event event) {

     
    }
}
