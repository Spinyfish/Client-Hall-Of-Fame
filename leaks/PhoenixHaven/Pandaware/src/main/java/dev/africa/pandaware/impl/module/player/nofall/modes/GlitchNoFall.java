package dev.africa.pandaware.impl.module.player.nofall.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.player.nofall.NoFallModule;
import dev.africa.pandaware.utils.client.Printer;
import dev.africa.pandaware.utils.player.PlayerUtils;

public class GlitchNoFall extends ModuleMode<NoFallModule> {
    public GlitchNoFall(String name, NoFallModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (this.getParent().canFall() && mc.thePlayer.fallDistance > 2.5 && PlayerUtils.isOnGround(1)) {
            event.setOnGround(true);
            event.setY(event.getY() - 1f);

            Printer.chat("§7[§cNo Fall§7] §fGlitched 1 block down");

            mc.thePlayer.fallDistance = 0;
        }
    };
}
