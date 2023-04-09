package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.impl.events.MovementEvent;
import tech.dort.dortware.impl.events.StrafeEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.movement.MotionUtils;

/**
 * @author Auth
 */

public class Strafe extends Module {

    public Strafe(ModuleData moduleData) {
        super(moduleData);
    }

    @Subscribe
    public void onUpdate(StrafeEvent event) {
        if (event.getState() != StrafeEvent.State.JUMP) {
            MotionUtils.setMotion(Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ));
        }
    }

    @Subscribe
    public void onMove(MovementEvent event) {
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.jumpMovementFactor = 0.02F;
    }


    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
//        mc.thePlayer.motionY = 0.42F;
        mc.thePlayer.jumpMovementFactor = 0.02f;
        super.onDisable();
    }
}
