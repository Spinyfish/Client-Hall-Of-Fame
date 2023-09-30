package dev.africa.pandaware.impl.module.movement.step.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.StepEvent;
import dev.africa.pandaware.impl.module.movement.step.StepModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.player.PlayerUtils;

public class JumpStep extends ModuleMode<StepModule> {
    private final NumberSetting jumpMotion = new NumberSetting("Jump Motion", 1, 0.38, 0.42, 0.01);
    private final BooleanSetting stopMidair = new BooleanSetting("Stop Midair", false);
    private final BooleanSetting constantMotion = new BooleanSetting("Constant Motion", false);
    private final NumberSetting midairTick = new NumberSetting("Midair Tick", 5, 1, 3, 1, this.stopMidair::getValue);
    private final NumberSetting midairMotion = new NumberSetting("Midair stop motion", 1, -1, 0, 0.1, this.stopMidair::getValue);

    private final TimeHelper timer = new TimeHelper();

    public JumpStep(String name, StepModule parent) {
        super(name, parent);

        this.registerSettings(
                this.jumpMotion,
                this.stopMidair,
                this.constantMotion,
                this.midairTick,
                this.midairMotion
        );
    }

    @EventHandler
    EventCallback<StepEvent> onStep = event -> {
        if (mc.thePlayer.isCollidedHorizontally && (PlayerUtils.isMathGround() || this.constantMotion.getValue())) {
            if (PlayerUtils.isBlockAbove(1) || PlayerUtils.inLiquid()) return;
            mc.thePlayer.motionY = this.jumpMotion.getValue().floatValue();
            timer.reset();
        }

        if (this.stopMidair.getValue()) {
            if (mc.thePlayer.getAirTicks() == this.midairTick.getValue().intValue() &&
                    !(timer.getMs() > (this.midairTick.getValue().intValue() + 0.25) * 50L)) {
                mc.thePlayer.motionY = this.midairMotion.getValue().floatValue();
            }
        }
    };
}
