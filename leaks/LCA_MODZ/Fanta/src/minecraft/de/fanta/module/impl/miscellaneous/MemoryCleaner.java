package de.fanta.module.impl.miscellaneous;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.module.Module;
import de.fanta.utils.TimeUtil;


public class MemoryCleaner extends Module {
    public MemoryCleaner() {
        super("RamClean",0, Type.Misc, Color.WHITE);
    }
    TimeUtil time = new TimeUtil();
    @Override
    public void onEvent(Event event) {

    	  if(time.hasReached(300000)) {
            //  System.gc();
              time.reset();
            }
        
    }
}
