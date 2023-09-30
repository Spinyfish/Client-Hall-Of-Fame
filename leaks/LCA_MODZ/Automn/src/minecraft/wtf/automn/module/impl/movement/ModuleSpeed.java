package wtf.automn.module.impl.movement;

import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;

@ModuleInfo(name = "speed", displayName = "Speed", category = Category.MOVEMENT)
public class ModuleSpeed extends Module {
    @Override
    protected void onDisable() {

    }

    @Override
    protected void onEnable() {

    }

    @EventHandler
    public void onUpdate(final EventPlayerUpdate e) {
        this.MC.gameSettings.keyBindJump.pressed = this.MC.gameSettings.keyBindForward.pressed;
    }
}
