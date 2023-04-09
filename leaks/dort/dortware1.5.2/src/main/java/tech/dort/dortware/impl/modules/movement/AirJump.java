package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.impl.events.UpdateEvent;

public class AirJump extends Module {

    public AirJump(ModuleData moduleData) {
        super(moduleData);
    }

    @Subscribe
    public void onMove(UpdateEvent event) {
        mc.thePlayer.onGround = true;
    }
}
