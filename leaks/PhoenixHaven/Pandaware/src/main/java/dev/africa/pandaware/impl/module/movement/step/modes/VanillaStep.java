package dev.africa.pandaware.impl.module.movement.step.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.StepEvent;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.impl.module.movement.step.StepModule;

public class VanillaStep extends ModuleMode<StepModule> {
    public VanillaStep(String name, StepModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<StepEvent> onStep = event -> {
        if (mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.onGround &&
                !(Client.getInstance().getModuleManager().getByClass(SpeedModule.class).getData().isEnabled())) {
            float stepHeight = this.getParent().getStepHeight().getValue().floatValue();
            event.setStepHeight(stepHeight);
        }
    };
}
