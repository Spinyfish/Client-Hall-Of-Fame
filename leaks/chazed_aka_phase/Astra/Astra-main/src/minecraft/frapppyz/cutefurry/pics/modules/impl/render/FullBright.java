package frapppyz.cutefurry.pics.modules.impl.render;

import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;

public class FullBright extends Mod {

    public FullBright() {
        super("Fullbright", "Bright..", 0, Category.RENDER);
    }

    public void onDisable(){
        mc.gameSettings.saturation = 100;
    }

    public void onEvent(Event e){
        if(e instanceof Update){
            mc.gameSettings.saturation = 10000;
        }
    }
}
