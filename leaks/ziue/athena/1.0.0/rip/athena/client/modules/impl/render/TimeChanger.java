package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import rip.athena.client.modules.*;

public class TimeChanger extends Module
{
    @ConfigValue.Integer(name = "Time", min = 1, max = 24000)
    public int time;
    
    public TimeChanger() {
        super("Time Changer", Category.RENDER, "Athena/gui/mods/timechanger.png");
        this.time = 16000;
    }
}
