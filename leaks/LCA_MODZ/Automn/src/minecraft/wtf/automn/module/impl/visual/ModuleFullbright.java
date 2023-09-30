package wtf.automn.module.impl.visual;


import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.visual.EventRender2D;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;

@ModuleInfo(name = "fullbright", displayName = "FullBright", category = Category.VISUAL)
public class ModuleFullbright extends Module {
    @Override
    protected void onDisable() {
        MC.gameSettings.gammaSetting = 0;
    }

    @Override
    protected void onEnable() {
        MC.gameSettings.gammaSetting = 10;
    }

    @EventHandler
    public void onUpdate(final EventRender2D e) {

    }
}