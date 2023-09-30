package dev.africa.pandaware.impl.module.movement.spider.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.spider.SpiderModule;
import dev.africa.pandaware.impl.setting.EnumSetting;
import lombok.AllArgsConstructor;

public class VerusSpider extends ModuleMode<SpiderModule> {
    public VerusSpider(String name, SpiderModule parent) {
        super(name, parent);

        this.registerSettings(this.mode);
    }

    private final EnumSetting<SpiderMode> mode = new EnumSetting<>("Mode", SpiderMode.JUMP);

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE && this.mode.getValue() == SpiderMode.JUMP) {
            if (mc.thePlayer.isCollidedHorizontally) {
                mc.thePlayer.motionY = 0.5f;
            }
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (mc.thePlayer.isCollidedHorizontally && this.mode.getValue() == SpiderMode.MOTION) {
            event.y = 0.5f;
        }
    };

    @AllArgsConstructor
    private enum SpiderMode {
        JUMP("Jump"),
        MOTION("Motion");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
