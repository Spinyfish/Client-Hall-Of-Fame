package wtf.automn.module.impl.movement;

import net.minecraft.client.settings.KeyBinding;
import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;

@ModuleInfo(name = "sprint", displayName = "Sprint", category = Category.MOVEMENT)
public class ModuleSprint extends Module {
    @Override
    protected void onDisable() {

    }

    @Override
    protected void onEnable() {
    }
    @EventHandler
    public void onUpdate(final EventPlayerUpdate e) {
        KeyBinding.setKeyBindState(this.MC.gameSettings.keyBindSprint.getKeyCode(), true);
    }
}
