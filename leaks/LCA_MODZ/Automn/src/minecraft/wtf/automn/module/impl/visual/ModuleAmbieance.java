package wtf.automn.module.impl.visual;

import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;
import wtf.automn.utils.math.TimeUtil;

@ModuleInfo(name = "ambieance", displayName = "Ambieance", category = Category.VISUAL)
public class ModuleAmbieance extends Module {

    TimeUtil timeUtil = new TimeUtil();

    @Override
    protected void onDisable() {

    }

    @Override
    protected void onEnable() {

    }

    @EventHandler
    public void onUpdate(final EventPlayerUpdate e) {

    }
}