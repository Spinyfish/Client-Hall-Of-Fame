package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.MovementEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.movement.MotionUtils;

/**
 * @author Dort, Auth
 */

public class Flight extends Module {

    private final EnumValue<Mode> mode = new EnumValue<>("Mode", this, Flight.Mode.values());
    private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 10, SliderUnit.BPT);
    private final BooleanValue viewBobbing = new BooleanValue("View Bobbing", this, true);

    public Flight(ModuleData moduleData) {
        super(moduleData);
        register(mode, speed, viewBobbing);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (viewBobbing.getValue()) {
            mc.thePlayer.cameraYaw = 0.1F;
        }
    }

    @Subscribe
    public void onMove(MovementEvent event) {
        switch (mode.getValue()) {
            case VANILLA:
                event.setMotionY(mc.thePlayer.motionY = (mc.thePlayer.movementInput.jump ? speed.getValue() : mc.thePlayer.movementInput.sneak ? -speed.getValue() : 0) * 0.5D);
                MotionUtils.setMotion(event, speed.getValue());
                break;

            case MINEPLEXLP:
                event.setMotionY(mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 0.42F : mc.thePlayer.movementInput.sneak ? -0.42F : 0.000001);
                if (!Client.INSTANCE.getModuleManager().isEnabled(LongJump.class) && !Client.INSTANCE.getModuleManager().isEnabled(Speed.class))
                    MotionUtils.setMotion(event, 0.4);
                break;

            case MINEPLEXHP:
                event.setMotionY(mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 0.42F : mc.thePlayer.movementInput.sneak ? -0.42F : 0.000001);
                if (!Client.INSTANCE.getModuleManager().isEnabled(LongJump.class) && !Client.INSTANCE.getModuleManager().isEnabled(Speed.class))
                    MotionUtils.setMotion(event, 0.32);
                break;
        }
    }

    @Override
    public String getSuffix() {
        return " \2477" + mode.getValue().getDisplayName();
    }

    public enum Mode implements INameable {
        VANILLA("Vanilla"), MINEPLEXLP("Mineplex Low Ping"), MINEPLEXHP("Mineplex High Ping");
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