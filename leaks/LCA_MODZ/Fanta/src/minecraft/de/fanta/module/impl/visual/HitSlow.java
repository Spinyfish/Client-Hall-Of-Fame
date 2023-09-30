package de.fanta.module.impl.visual;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.Slider;

public class HitSlow extends Module {
    public HitSlow() {
        super("HitSlow",0, Type.Visual, new Color(108,2,139));
        this.settings.add(new Setting("SlowDown", new Slider(0, 1000, 1, 50)));
    }
    public static double hitSlowdown;
    @Override
    public void onEvent(Event event) {
    	hitSlowdown = ((Slider) this.getSetting("SlowDown").getSetting()).curValue;
        
    }
}