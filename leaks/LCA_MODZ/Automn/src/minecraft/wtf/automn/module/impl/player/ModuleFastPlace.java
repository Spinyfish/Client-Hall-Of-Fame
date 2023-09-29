package wtf.automn.module.impl.player;


import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;

@ModuleInfo(name = "fastplace", displayName = "FastPlace", category = Category.PLAYER)
public class ModuleFastPlace extends Module {
    @Override
    protected void onDisable() {
        MC.rightClickDelayTimer = 4;
    }

    @Override
    protected void onEnable() {
        MC.rightClickDelayTimer = 0;
    }

    @EventHandler
    public void onUpdate(final EventPlayerUpdate e) {
        MC.rightClickDelayTimer = 0;
    }
}