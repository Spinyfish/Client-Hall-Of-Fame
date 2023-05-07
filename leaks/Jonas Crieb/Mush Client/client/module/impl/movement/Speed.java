package client.module.impl.movement;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.module.impl.movement.flight.MotionFlight;
import client.module.impl.movement.speed.MushMCSpeed;
import client.util.player.MoveUtil;
import client.value.impl.ModeValue;

@ModuleInfo(name = "Speed", description = "Grants you the ability to fly", category = Category.MOVEMENT)
public class Speed extends Module {
    public final ModeValue mode = new ModeValue("Mode", this)
            .add(new MushMCSpeed("MushMC", this)).setDefault("MushMCS");
    @Override
    protected void onDisable(){
        mc.timer.timerSpeed = 1f;
        MoveUtil.stop();
    }
}
