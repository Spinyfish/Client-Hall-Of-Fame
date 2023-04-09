package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.MovementEvent;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.enums.PacketDirection;
import tech.dort.dortware.impl.utils.movement.MotionUtils;

public class CustomFly extends Module {

    private boolean didDamage;

    private final NumberValue motionX = new NumberValue("Motion X", this, 1, 0.1, 10, SliderUnit.BPT);
    private final NumberValue motionY = new NumberValue("Motion Y", this, 1, 0, 5);
    private final NumberValue timerSpeed = new NumberValue("Timer", this, 1, 0.1, 5);
    private final BooleanValue creativeFly = new BooleanValue("Creative Fly", this, false);
    private final BooleanValue flagCheck = new BooleanValue("Flag Check", this, true);
    private final BooleanValue timer = new BooleanValue("Timer", this, false);
    private final BooleanValue damage = new BooleanValue("Damage", this, false);

    public CustomFly(ModuleData moduleData) {
        super(moduleData);
        register(motionX, motionY, timerSpeed, creativeFly, flagCheck, timer, damage);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacketDirection() == PacketDirection.INBOUND) {
            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                if (flagCheck.getValue()) {
                    this.toggle();
                }
            }
        }
    }

    @Subscribe
    public void onMove(MovementEvent event) {
        if (damage.getValue() && !didDamage) {
            MotionUtils.damagePlayer(true);
            didDamage = true;
        }
        MovementInput movementInput = mc.thePlayer.movementInput;
        if (movementInput.jump) event.setMotionY(motionY.getValue());
        if (movementInput.sneak) event.setMotionY(-motionY.getValue());
        if (!movementInput.sneak && !movementInput.jump) event.setMotionY(0);
        if (creativeFly.getValue()) mc.thePlayer.capabilities.allowFlying = true;
        if (timer.getValue()) mc.timer.timerSpeed = timerSpeed.getValue().floatValue();
        MotionUtils.setMotion(event, motionX.getValue());
    }

    @Override
    public void onDisable() {
        didDamage = false;
    }

}
