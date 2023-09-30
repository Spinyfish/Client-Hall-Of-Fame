package de.fanta.module.impl.movement;



import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.settings.CheckBox;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint",0, Type.Movement, new Color(108,2,139));
    }

    @Override
    public void onEvent(Event event) {

        if(Client.INSTANCE.moduleManager.getModule("Scaffold").isState()) {
         
       
        	   mc.gameSettings.keyBindSprint.pressed = false;
        	   
        }else {
            if(mc.gameSettings.keyBindForward.pressed  || Client.INSTANCE.moduleManager.getModule("Killaura").isState() &&!((CheckBox) Client.INSTANCE.moduleManager.getModule("Killaura").getSetting("LegitRange").getSetting()).state) {
            	 mc.gameSettings.keyBindSprint.pressed = true;
            }
        }
    }
}
