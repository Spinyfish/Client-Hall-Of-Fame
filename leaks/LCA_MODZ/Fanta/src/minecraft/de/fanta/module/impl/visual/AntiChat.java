package de.fanta.module.impl.visual;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.settings.CheckBox;

public class AntiChat extends Module {
    public AntiChat() {
        super("AntiChat",0, Type.Visual, Color.yellow);
    }

    @Override
    public void onEvent(Event event) {
    }
}
