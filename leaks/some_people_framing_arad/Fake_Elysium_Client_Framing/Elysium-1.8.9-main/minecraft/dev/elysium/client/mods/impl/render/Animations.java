package dev.elysium.client.mods.impl.render;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventMotion;
import dev.elysium.client.events.EventUpdate;

public class Animations extends Mod {
    private  int bongli = 0;
    private boolean stillswinging = false;
    public ModeSetting mode = new ModeSetting("Mode",this,"1.7","Swank","Swonk","Exhibition","Test");
    public NumberSetting slowdown = new NumberSetting("Slow Down Factor",0,5,0,0.1,this);
    public NumberSetting x = new NumberSetting("X",-5,5,0,0.05,this);
    public NumberSetting y = new NumberSetting("Y",-5,5,0,0.05,this);

    public Animations() {
        super("Animations","Modifies how the held item is rendered", Category.RENDER);
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        if(!stillswinging && slowdown.getValue() != 0) {
            float speed = (float) (5.1 - slowdown.getValue()) / 1.2F;
            if(e.isPre()) {
                mc.thePlayer.swingProgress = (float) (speed * bongli/200);
                bongli += 8;
            } else if(e.isPost()) {
                mc.thePlayer.swingProgress = (float) (speed * bongli/200);
            }

            if(speed * bongli >= 200) {
                bongli = 0;
                stillswinging = true;
            }
        }
        if(mc.thePlayer.isSwingInProgress && stillswinging) {
            stillswinging = false;
        }
    }

}
