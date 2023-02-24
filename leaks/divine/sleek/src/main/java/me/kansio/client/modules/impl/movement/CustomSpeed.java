package me.kansio.client.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.utils.player.PlayerUtil;

@ModuleData(
        name = "Custom Speed",
        description = "Gives the ability to customize speed in many ways",
        category = ModuleCategory.MOVEMENT
)
public class CustomSpeed extends Module {

    private NumberValue<Double> motionY = new NumberValue<>("Base MotionY", this, 0.42, 0.05, 5.0, 0.01);
    private NumberValue<Double> groundSpeed = new NumberValue<>("Onground speed", this, 0.4, 0d, 10.0, 0.01);

    @Subscribe
    public void onUpdate(MoveEvent event) {
        if (mc.thePlayer.isMovingOnGround()) {
            event.setMotionY(mc.thePlayer.motionY = PlayerUtil.getMotion(motionY.getValue().floatValue()));
            PlayerUtil.setMotion(event, groundSpeed.getValue());
        }
    }

}
