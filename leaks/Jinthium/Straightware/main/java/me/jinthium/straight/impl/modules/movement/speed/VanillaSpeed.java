package me.jinthium.straight.impl.modules.movement.speed;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.modules.combat.TargetStrafe;
import me.jinthium.straight.impl.modules.movement.Speed;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;

@ModeInfo(name = "Vanilla", parent = Speed.class)
public class VanillaSpeed extends ModuleMode<Speed> {
    private final NumberSetting speed = new NumberSetting("Speed", 1.5f, 0.1f, 10f, 0.1f);

    public VanillaSpeed(){
        this.registerSettings(speed);
    }

    @Callback
    final EventCallback<PlayerMoveUpdateEvent> playerMoveUpdateEventEventCallback = event -> {
        if(mc.thePlayer.isMoving()){
            if(mc.thePlayer.onGround){
                mc.thePlayer.jump();
            }
            mc.thePlayer.motionX *= mc.thePlayer.motionZ *= 0;
            event.setFriction(speed.getValue().floatValue());
            TargetStrafe.strafe(event, speed.getValue().floatValue());
        }
    };
}
