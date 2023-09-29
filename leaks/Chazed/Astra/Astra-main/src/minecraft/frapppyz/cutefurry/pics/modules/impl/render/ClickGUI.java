package frapppyz.cutefurry.pics.modules.impl.render;

import frapppyz.cutefurry.pics.clickgui.ClickGUIMain;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Mod {
    public ClickGUI() {
        super("ClickGUI", "Shows yo mods brah", Keyboard.KEY_RSHIFT, Category.RENDER);
    }

    public void onEnable(){
        mc.displayGuiScreen(new ClickGUIMain());
    }
}
