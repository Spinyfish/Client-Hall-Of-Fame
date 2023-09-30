package de.fanta.module.impl.visual;


import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.settings.CheckBox;

public class NoBob extends Module {
    public NoBob() {
        super("NoBob",0, Type.Visual, Color.green);
    }

    @Override
    public void onEvent(Event event) {

    	  this.mc.gameSettings.viewBobbing = true;
          this.mc.thePlayer.distanceWalkedModified = 0.0F;
    }

}