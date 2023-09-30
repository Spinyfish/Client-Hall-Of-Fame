package dev.africa.pandaware.impl.module.combat.antibot.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.module.combat.antibot.AntiBotModule;
import dev.africa.pandaware.impl.setting.NumberSetting;

public class TicksAntiBot extends ModuleMode<AntiBotModule> {
    private final NumberSetting ticks = new NumberSetting("Ticks", 100, 1, 20);

    public TicksAntiBot(String name, AntiBotModule parent) {
        super(name, parent);

        this.registerSettings(
                this.ticks
        );
    }

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        mc.theWorld.playerEntities.forEach(entityPlayer -> {
            if (entityPlayer.ticksExisted <= this.ticks.getValue().intValue()) {
                Client.getInstance().getIgnoreManager().add(entityPlayer, false);
            } else {
                Client.getInstance().getIgnoreManager().remove(entityPlayer, false);
            }
        });
    };
}
