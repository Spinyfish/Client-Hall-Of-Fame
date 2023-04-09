package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.MovementEvent;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.utils.movement.MotionUtils;

public class Glide extends Module {

    private final NumberValue speedValue = new NumberValue("Speed", this, 1, 0.1, 5.0);
    private final NumberValue motionY = new NumberValue("Motion Y", this, 0.15, 0.1, 2.5);
    private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, Mode.values());
    private final BooleanValue flagCheck = new BooleanValue("Flag Check", this, true);

    public Glide(ModuleData moduleData) {
        super(moduleData);
        register(enumValue, motionY, speedValue, flagCheck);
    }

    @Subscribe
    public void onMove(MovementEvent event) {
        switch (enumValue.getValue()) {
            case VANILLA:
                MotionUtils.setMotion(speedValue.getValue());
                event.setMotionY(mc.thePlayer.movementInput.jump ? motionY.getValue() : -motionY.getValue());
                break;
            case CUBECRAFT:
                MotionUtils.setMotion(1.51);
                mc.timer.timerSpeed = 0.65F;
                event.setMotionY(mc.thePlayer.movementInput.jump ? 0.1535 : -0.1535);
                break;
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            if (flagCheck.getValue()) {
                this.toggle();
            }
        }
    }

    public enum Mode implements INameable {
        VANILLA("Vanilla"), CUBECRAFT("Old Cubecraft");
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
