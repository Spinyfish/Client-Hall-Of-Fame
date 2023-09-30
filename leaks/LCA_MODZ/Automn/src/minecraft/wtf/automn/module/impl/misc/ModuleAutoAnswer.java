package wtf.automn.module.impl.misc;


import net.minecraft.client.settings.KeyBinding;
import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;

@ModuleInfo(name = "autoanswer", displayName = "AutoAnswer", category = Category.MISC)
public class ModuleAutoAnswer extends Module {
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
