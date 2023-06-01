package frapppyz.cutefurry.pics.modules.impl.move;

import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import org.lwjgl.input.Keyboard;

public class Sprint extends Mod {
    public Sprint() {
        super("Sprint", "Sprints for you...", 0, Category.MOVE);
    }

    public void onEvent(Event e){
        if(e instanceof Update){
            mc.gameSettings.keyBindInventory.pressed = true;
        }
    }
}
