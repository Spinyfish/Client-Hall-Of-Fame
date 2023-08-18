package me.jinthium.straight.impl.modules.movement.flight;

import com.google.gson.annotations.Expose;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.movement.PlayerMoveEvent;
import me.jinthium.straight.impl.modules.combat.TargetStrafe;
import me.jinthium.straight.impl.modules.movement.Flight;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;

@ModeInfo(name = "Motion", parent = Flight.class)
public class MotionFly extends ModuleMode<Flight> {

    private final NumberSetting speed = new NumberSetting("Speed", 1.5f, 0.1f, 10f, 0.1f);

    public MotionFly(){
        this.registerSettings(speed);
    }

    @Override
    public void onDisable() {
        mc.thePlayer.speedInAir = 0.02F;
        super.onDisable();
    }

    @Callback
    final EventCallback<PlayerMoveEvent> playerMoveEventEventCallback = event -> {
            event.setY(mc.thePlayer.motionY = TargetStrafe.canStrafe() ? 0 : mc.thePlayer.movementInput.jump ? 0.42F : mc.thePlayer.movementInput.sneak ? -0.42F : 0);
            MovementUtil.setSpeed(event, speed.getValue().floatValue());
            TargetStrafe.strafe(event, speed.getValue().floatValue());
//       if(mc.thePlayer.onGround) event.setY(mc.thePlayer.motionY = 4.4f);
//        if (mc.thePlayer.fallDistance > 0 && mc.thePlayer.ticksExisted % 2 == 0) {
//            event.setY(mc.thePlayer.motionY = -0.09701);
//        }else if(mc.thePlayer.fallDistance > 0){
//            event.setY(mc.thePlayer.motionY *= 1);
//        }
    };
}
