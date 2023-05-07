package client.module.impl.movement;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.module.impl.movement.flight.FakeTransactionFlight;
import client.module.impl.movement.flight.MotionFlight;
import client.module.impl.movement.flight.MushMCFlight;
import client.module.impl.movement.flight.VulcanFlight;
import client.util.player.MoveUtil;
import client.value.impl.ModeValue;

@ModuleInfo(name = "Flight", description = "Grants you the ability to fly", category = Category.MOVEMENT)
public class Flight extends Module {
    public final ModeValue mode = new ModeValue("Mode", this)
            .add(new MotionFlight("Motion", this))
            .add(new MushMCFlight("MushMC", this))
            .add(new VulcanFlight("Vulcan", this))
            .add(new FakeTransactionFlight("Transaction", this))
            .setDefault("Vulcan");

    @Override
    protected void onDisable(){
        mc.timer.timerSpeed = 1f;
        MoveUtil.stop();
    }
}
