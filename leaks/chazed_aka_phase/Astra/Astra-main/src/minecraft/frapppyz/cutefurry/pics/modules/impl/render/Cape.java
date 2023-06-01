package frapppyz.cutefurry.pics.modules.impl.render;

import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;

public class Cape extends Mod {

    public static Mode mode = new Mode("Style", "Hentai", "Hentai");
    public Cape() {
        super("Capes", "What do you think?", 0, Category.RENDER);
    }

    public void onEvent(Event e){
        if(e instanceof Render){
            this.setSuffix(mode.getMode());
        }
    }
}
