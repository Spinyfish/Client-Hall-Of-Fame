package wtf.automn.module.impl.movement;

import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;
import wtf.automn.utils.player.PlayerUtil;

@ModuleInfo(name = "fly", displayName = "Fly", category = Category.MOVEMENT)

public class ModuleFlight extends Module {
    public boolean gotDmg;
    @Override
    protected void onDisable() {
        mc.thePlayer.motionX = 0f;
        mc.thePlayer.motionZ = 0f;
        mc.timer.timerSpeed = 1F;
    }
    @Override
    protected void onEnable() {

        gotDmg = false;
        PlayerUtil.verusFlyCheckDisabler();
    }

    @EventHandler
    public void onUpdate(final EventPlayerUpdate e) {
                mc.thePlayer.onGround = true;
                PlayerUtil.verusFlyCheckDisabler();
                if(mc.gameSettings.keyBindJump.pressed){
                    mc.thePlayer.motionY = 1F;
                }else{
                    mc.thePlayer.motionY = 0f;
                }
    }
}
