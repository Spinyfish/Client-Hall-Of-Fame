package client.module.impl.render;


import client.gayclicks.Clickgui;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;

@ModuleInfo(name = "ClickGui", description = "gay", category = Category.RENDER)
public class ClickXuy extends Module {
    @Override
    public void onEnable(){
        super.onEnable();
        mc.displayGuiScreen(new Clickgui());
    }
    @Override
    public void onDisable(){
        super.onDisable();

    }
}
