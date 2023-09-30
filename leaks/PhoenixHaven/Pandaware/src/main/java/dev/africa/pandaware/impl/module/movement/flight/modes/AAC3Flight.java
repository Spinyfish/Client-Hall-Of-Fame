package dev.africa.pandaware.impl.module.movement.flight.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.player.MovementUtils;

public class AAC3Flight extends ModuleMode<FlightModule> {

    private final BooleanSetting hop = new BooleanSetting("Hop", false);
    private final NumberSetting hopDelay = new NumberSetting("Hop Delay", 10, 1, 3, 1, hop::getValue);
    private final NumberSetting hopHeight = new NumberSetting("Hop Height", 0.5, 0, 0.4, 0.01, hop::getValue);

    private double startY;

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            mc.thePlayer.motionY = -0.0784000015258789;
            if (hop.getValue()) {
                if (mc.thePlayer.ticksExisted % hopDelay.getValue().intValue() == 0) {
                    mc.thePlayer.motionY = hopHeight.getValue().doubleValue();
                }
            }
        }
    };

    public AAC3Flight(String name, FlightModule parent) {
        super(name, parent);

        this.registerSettings(
                this.hop,
                this.hopDelay,
                this.hopHeight
        );
    }

    @Override
    public void onEnable() {
        startY = mc.thePlayer.posY;
    }
}
