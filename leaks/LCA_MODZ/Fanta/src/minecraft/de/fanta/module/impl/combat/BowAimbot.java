package de.fanta.module.impl.combat;



import java.awt.Color;

import de.fanta.events.Event;
import de.fanta.module.Module;

public class BowAimbot extends Module {
    public BowAimbot() {
        super("BowAimbot", 0, Type.Combat, Color.WHITE);
    }

    @Override
    public void onEvent(Event event) {

    }
}
