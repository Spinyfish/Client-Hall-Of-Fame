package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.MovementEvent;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.utils.movement.MotionUtils;

public class CustomSpeed extends Module {

    private final NumberValue motionX = new NumberValue("Motion X", this, 1, 0.1, 10, SliderUnit.BPT);
    private final NumberValue motionY = new NumberValue("Motion Y", this, 0.42, 0, 1.2);
    private final NumberValue timerSpeed = new NumberValue("Timer", this, 1, 0.1, 5);
    private final BooleanValue water = new BooleanValue("Water", this, true);
    private final BooleanValue flagCheck = new BooleanValue("Flag Check", this, true);
    private final BooleanValue timer = new BooleanValue("Timer", this, false);
    private final BooleanValue damage = new BooleanValue("Damage", this, false);
    private final BooleanValue bhop = new BooleanValue("Hop", this, true);

    public CustomSpeed(ModuleData moduleData) {
        super(moduleData);
        register(motionX, motionY, timerSpeed, water, flagCheck, timer, damage, bhop);
    }

    @Subscribe
    public void onMove(MovementEvent event) {
        if (timer.getValue()) mc.timer.timerSpeed = timerSpeed.getValue().floatValue();
        if (mc.thePlayer.isInWater() && !water.getValue()) return;
        if (bhop.getValue() && mc.thePlayer.isMovingOnGround()) {
            if (damage.getValue() && mc.thePlayer.hurtTime == 0) MotionUtils.damagePlayer(false);
            event.setMotionY(mc.thePlayer.motionY = motionY.getValue());
        }
        if (mc.thePlayer.isMoving())
            MotionUtils.setMotion(event, motionX.getValue());
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            if (flagCheck.getValue()) {
                this.toggle();
            }
        }
    }
}
