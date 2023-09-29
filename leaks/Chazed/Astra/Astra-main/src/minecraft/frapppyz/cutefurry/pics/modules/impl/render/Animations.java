package frapppyz.cutefurry.pics.modules.impl.render;

import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;

public class Animations extends Mod {
    public static Mode mode = new Mode("Mode", "1.7", "1.7", "Frapppyz", "Autist", "Swank");
    public Animations() {
        super("Animations", "Change ur Animations OwO", 0, Category.RENDER);
        addSettings(mode);
        this.setToggledSilently(true);
    }

    public void onEvent(Event e){
        if(e instanceof Render){
            this.setSuffix(null);
        }
    }
}
