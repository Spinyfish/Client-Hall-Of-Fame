package dev.africa.pandaware.impl.module.misc;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.utils.client.Printer;

@ModuleInfo(name = "AntiBan", description = "SUMMER V5", category = Category.MISC)
public class AntiBanModule extends Module {
    private final BooleanSetting alice = new BooleanSetting("Alice", false);

    public AntiBanModule() {
        this.registerSettings(this.alice);
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            if (this.alice.getValue()) {
                Printer.chat("Sent C0Nigger Packet!");
                Printer.chat("Received C0Nigger Packet!");
            }
            Printer.chat("DONT USE ANTIBAN ON HYPIXEL");
            event.setY(event.getY());
            event.setX(event.getX());
            event.setZ(event.getZ());
            event.setYaw(event.getYaw());
            event.setPitch(event.getPitch());
            event.setOnGround(event.isOnGround());
        }
    };
}
