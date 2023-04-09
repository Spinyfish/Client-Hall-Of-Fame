package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.MovementEvent;

public class FastLadder extends Module {

    private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, FastLadder.Mode.values());
    private final NumberValue motionY = new NumberValue("Motion Y", this, 0.42, 0.1, 1, false);
    private final NumberValue timer = new NumberValue("Timer", this, 2, 0.1, 10);

    private boolean doingTimer;

    public FastLadder(ModuleData moduleData) {
        super(moduleData);
        register(enumValue, motionY, timer);
    }

    @Subscribe
    public void onMove(MovementEvent event) {
        Mode mode = enumValue.getValue();
        switch (mode) {
            case VANILLA:
                if (mc.thePlayer.isOnLadder()) {
                    event.setMotionY(mc.thePlayer.motionY = motionY.getValue());
                }
                break;

            case TIMER:
                if (mc.thePlayer.isOnLadder() && mc.thePlayer.isCollidedHorizontally && mc.thePlayer.isMoving()) {
                    mc.timer.timerSpeed = timer.getValue().floatValue();
                    doingTimer = true;
                } else if (doingTimer) {
                    mc.timer.timerSpeed = 1.0F;
                    doingTimer = false;
                }
                break;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        doingTimer = false;
    }

    public enum Mode implements INameable {
        VANILLA("Vanilla"), TIMER("Timer");
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
