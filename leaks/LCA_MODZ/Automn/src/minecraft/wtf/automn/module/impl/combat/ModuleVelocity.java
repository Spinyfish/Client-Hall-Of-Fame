package wtf.automn.module.impl.combat;


import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;

@ModuleInfo(name = "velocity", displayName = "Velocity", category = Category.COMBAT)
public class ModuleVelocity extends Module {
    @Override
    protected void onDisable() {

    }

    @Override
    protected void onEnable() {

    }

    @EventHandler
    public void onUpdate(final EventPlayerUpdate e) {
        if(mc.thePlayer.hurtTime != 0){
            mc.thePlayer.motionX = 0f;
            mc.thePlayer.motionZ = 0f;
        }
    }
}